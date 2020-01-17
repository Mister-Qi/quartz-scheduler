package sg.ncs.quartz.scheduler.exception;
/**
 * @description Thrown the exception when task reach the time limitation.
 * @author Qi Qi
 * @version 1.0
 * @created 1/16/2020
 */
public class TaskTimeoutException extends RuntimeException{

    public TaskTimeoutException(String message) {
        super(message);
    }
}
