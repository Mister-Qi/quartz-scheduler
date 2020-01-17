package sg.ncs.quartz.scheduler.exception;

/**
 * @description exception thrown when scheduled runtime command task encounter error.
 * @author Qi Qi
 * @version 1.0
 * @created 1/16/2020
 */
public class CommandException extends RuntimeException {

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
