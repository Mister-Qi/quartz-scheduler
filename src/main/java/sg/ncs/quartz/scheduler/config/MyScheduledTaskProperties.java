package sg.ncs.quartz.scheduler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import sg.ncs.quartz.scheduler.pojo.ScheduledTask;

import java.util.List;

/**
 * @author Qi Qi
 * @version 1.0
 * @description initialize scheduled task in configuration file.
 * @created 1/16/2020
 */

@Configuration
@ConfigurationProperties(prefix = "test-task")
@Data
public class MyScheduledTaskProperties {

    private List<ScheduledTask> scheduledTasks;

}
