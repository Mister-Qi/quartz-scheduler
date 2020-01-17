package sg.ncs.quartz.scheduler.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sg.ncs.quartz.scheduler.common.Constants;
import sg.ncs.quartz.scheduler.config.MyScheduledTaskProperties;

import java.util.HashMap;

import static org.mockito.Mockito.when;

/**
 * @description TODO
 * @author Qi Qi
 * @version 1.0
 * @created 1/17/2020
 */
@SpringBootTest
public class RunTimeCommandTaskTest {

    @Autowired
    private MyScheduledTaskProperties myScheduledTaskProperties;

    @Mock
    JobExecutionContext jobExecutionContext;

    @BeforeEach
    public void prepareJobExecution(){
        JobDataMap jobDataMap=new JobDataMap();
//        jobDataMap.put(Constants.COMMAND,"C:/Users/P1313521/Desktop/bin/moe-help.cmd >D:/temp/test-show.txt");

        jobDataMap.put(Constants.COMMAND,"C:\\Users\\P1313521\\Desktop\\bin\\moe-help.cmd > %time:~0,2%_%time:~3,2%_%time:~6,2%.txt");
        jobDataMap.put(Constants.TASK_NAME,"task-test");
        jobDataMap.put(Constants.WORK_DIRECTORY,"D:/temp/log");
        jobDataMap.put(Constants.TIME_OUT,10);
        JobDetail build = JobBuilder.newJob(RunTimeCommandTask.class).withIdentity("test-job").setJobData(jobDataMap).build();
        when(jobExecutionContext.getJobDetail()).thenReturn(build);
    }

    @Test
    public void testCommandProcess() throws JobExecutionException {
        RunTimeCommandTask runTimeCommandTask = new RunTimeCommandTask();
        runTimeCommandTask.executeInternal(jobExecutionContext);
    }
}
