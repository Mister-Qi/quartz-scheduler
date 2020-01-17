package sg.ncs.quartz.scheduler.task;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import sg.ncs.quartz.scheduler.common.Constants;
import sg.ncs.quartz.scheduler.exception.CommandException;
import sg.ncs.quartz.scheduler.exception.TaskTimeoutException;
import sg.ncs.quartz.scheduler.pojo.ScheduledTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @description The task to create operation system process to run command task.
 * @author Qi Qi
 * @version 1.0
 * @created 1/16/2020
 */
public class RunTimeCommandTask extends QuartzJobBean {

    private static final Logger logger = LoggerFactory.getLogger(RunTimeCommandTask.class);

//    private ScheduledTask task;
//
//    public RunTimeCommandTask(ScheduledTask task) {
//        this.task = task;
//    }

    private JobDataMap jobDataMap;

    private static final String BLANK = " ";

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        this.jobDataMap = context.getJobDetail().getJobDataMap();
        String taskName=this.jobDataMap.getString(Constants.TASK_NAME);
        int timeout=this.jobDataMap.getInt(Constants.TIME_OUT);
        String workDir=this.jobDataMap.getString(Constants.WORK_DIRECTORY);
        String[] commands=this.jobDataMap.getString(Constants.COMMAND).split(" ");
        logger.info("Process with task:{}, the commands:{}",taskName,commands);

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        try {
            if(!StringUtils.isEmpty(workDir)){
                processBuilder.directory(new File(workDir));
            }
            Process process = processBuilder.start();

            /*
                wait for timeout limitation for process termination.
                the method block depend on exitValue().
             */
            process.waitFor(timeout, TimeUnit.SECONDS);

            int exitValue = process.exitValue();
            boolean normalTerminate = exitValue==0;
            processTerminateNormally(process,normalTerminate);
        } catch (IOException e) {
            String errorMessage = "Exception occurred when execute task:" + taskName;
            logger.error(errorMessage, e);
            throw new CommandException(errorMessage, e);
        } catch (InterruptedException e) {
            logger.error("Task not finished in time, task:{}",taskName);
            throw new TaskTimeoutException("Task timeout -- task:"+taskName);
        }
    }

    /**
     * process the return error or normal message.
     * @param process the executed process
     * @param normalTerminate the process return 0 for normal ,or others for abnormal
     */
    private void processTerminateNormally(Process process,boolean normalTerminate) throws IOException {
        InputStream messageStream=null;
        try {
            if(!normalTerminate){
                messageStream = process.getErrorStream();
            }
            if (Objects.isNull(messageStream)) {
                messageStream = process.getInputStream();
            }
            String message="NULL_MESSAGE";
            if(Objects.nonNull(messageStream)){
                String output = StreamUtils.copyToString(messageStream, Charset.defaultCharset());
                message=StringUtils.isEmpty(output)?message:output;
            }
            logger.info("Process terminated [{}] with task:{}, the output message: {}"
                    , normalTerminate?"Normal":"Abnormal",this.jobDataMap.getString(Constants.TASK_NAME), message);
        }finally{
            if(Objects.nonNull(messageStream)){
                messageStream.close();
            }
        }
    }
}
