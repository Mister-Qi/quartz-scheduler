package sg.ncs.quartz.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class QuartzSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuartzSchedulerApplication.class, args);
	}

}
