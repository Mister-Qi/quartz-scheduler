package sg.ncs.quartz.scheduler.config;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import sg.ncs.quartz.scheduler.common.Constants;
import sg.ncs.quartz.scheduler.pojo.ScheduledTask;
import sg.ncs.quartz.scheduler.task.RunTimeCommandTask;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Qi Qi
 * @version 1.0
 * @description TODO
 * @created 1/16/2020
 */

@Configuration
public class QuartzSchedulerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(QuartzSchedulerConfigurer.class);

    private static final int DEFAULT_TIME_OUT = 30;

    @Autowired
    private MyScheduledTaskProperties myScheduledTaskProperties;

    @Bean
    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer() {
        return new MySchedulerFactoryBeanCustomizer(this.myScheduledTaskProperties);
    }

//    private final List<ScheduledTask> scheduledTasks;

//    public QuartzSchedulerConfigurer(ScheduledTasksProperties scheduledTasksProperties){
//        this.scheduledTasks =scheduledTasksProperties.getScheduledTasks();
//    }

//    @Override
//    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
//        IntStream.range(0, this.scheduledTasks.size()).forEach(index->
//            registerTaskBean(this.scheduledTasks.get(index),registry)
//        );
//    }
//
//    private void registerTaskBean(ScheduledTask scheduledTask, BeanDefinitionRegistry registry) {
//        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(JobDetailImpl.class);
//
//        beanDefinitionBuilder.addPropertyValue("name",scheduledTask.getTaskName());
//        beanDefinitionBuilder.addPropertyValue("jobClass",RunTimeCommandTask.class);
//        beanDefinitionBuilder.addPropertyValue("jobDataMap",createJobDataMap(scheduledTask));
//        beanDefinitionBuilder.addPropertyValue("durability",true);
//
//        registry.registerBeanDefinition( scheduledTask.getTaskName()+"_TaskBean", beanDefinitionBuilder.getBeanDefinition());
//
//    }
//
//    private JobDataMap createJobDataMap(ScheduledTask task){
//        JobDataMap dataMap=new JobDataMap();
//        Assert.hasText(task.getCommand(),"Task command must not be null");
//        dataMap.put(Constants.COMMAND,task.getCommand());
//        if(task.getTimeout()<1){
//            task.setTimeout(DEFAULT_TIME_OUT);
//        }
//        dataMap.put(Constants.TIME_OUT,task.getTimeout());
//        if(!StringUtils.isEmpty(task.getWorkDirectory())){
//            dataMap.put(Constants.WORK_DIRECTORY,task.getWorkDirectory());
//        }
//        return dataMap;
//    }
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        // Nothing to do.
//    }

    /**
     * Customize AutoConfigured quartzScheduler, dynamically set user defined Task and Trigger.
     */
    static class MySchedulerFactoryBeanCustomizer implements SchedulerFactoryBeanCustomizer {

        private final List<ScheduledTask> scheduledTasks;

        private final List<JobDetail> configuredJobDetails = new ArrayList<>();

        private final List<Trigger> configuredTrigger = new ArrayList<>();

        MySchedulerFactoryBeanCustomizer(MyScheduledTaskProperties scheduledTasksProperties) {
            this.scheduledTasks = scheduledTasksProperties.getScheduledTasks();
            this.scheduledTasks.forEach(task -> createTask(task));
        }

        private void createTask(ScheduledTask task) {
            JobDetail jobDetail = createConfiguredJobDetail(task);
            this.configuredJobDetails.add(jobDetail);
            this.configuredTrigger.add(createConfiguredCronTrigger(jobDetail, task));
        }

        @Override
        public void customize(SchedulerFactoryBean schedulerFactoryBean) {
            Assert.isTrue(this.configuredJobDetails.size() == this.configuredTrigger.size(), "JobDetails must have the same size with Triggers.");
            schedulerFactoryBean.setJobDetails(configuredJobDetails.toArray(new JobDetail[0]));
            schedulerFactoryBean.setTriggers(configuredTrigger.toArray(new Trigger[0]));
        }

        /**
         * create JobDetail with configurations
         */
        private JobDetail createConfiguredJobDetail(ScheduledTask task) {
            JobBuilder jobBuilder = JobBuilder.newJob(RunTimeCommandTask.class).storeDurably()
                    .withIdentity(task.getTaskName());
            return jobBuilder.setJobData(createJobDataMap(task)).build();
        }

        /**
         * create JobDataMap for each configured task.
         */
        private JobDataMap createJobDataMap(ScheduledTask task) {
            JobDataMap dataMap = new JobDataMap();
            Assert.hasText(task.getCommand(), "Task command must not be null.");
            Assert.hasText(task.getTaskName(), "TaskName must not be null.");
            dataMap.put(Constants.COMMAND, task.getCommand());
            dataMap.put(Constants.TASK_NAME, task.getTaskName());
            if (task.getTimeout() < 1) {
                task.setTimeout(DEFAULT_TIME_OUT);
            }
            dataMap.put(Constants.TIME_OUT, task.getTimeout());
            if (!StringUtils.isEmpty(task.getWorkDirectory())) {
                dataMap.put(Constants.WORK_DIRECTORY, task.getWorkDirectory());
            }
            return dataMap;
        }

        /**
         * create cron trigger with particular task and jobDetail
         */
        private Trigger createConfiguredCronTrigger(JobDetail jobDetail, ScheduledTask task) {
            Assert.isTrue(CronExpression.isValidExpression(task.getCronExpression()), "User defined cronExpression must be valid.");
            return TriggerBuilder.newTrigger().forJob(jobDetail)
                    .withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpression()))
                    .build();
        }
    }
}
