package sg.ncs.quartz.scheduler.pojo;

import lombok.Data;

/**
 * @author Qi Qi
 * @version 1.0
 * @description The object model to represent the scheduled cron command task.
 * @created 1/16/2020
 */

@Data
public class ScheduledTask {

    private String taskName;

    private String cronExpression;

    private String command;

    // default 10 seconds for task timeout
    private int timeout;

    private String workDirectory;
}
