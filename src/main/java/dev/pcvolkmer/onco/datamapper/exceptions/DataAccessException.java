package dev.pcvolkmer.onco.datamapper.exceptions;

/**
 * Exception to be thrown if no or unexpected multiple data sets where found
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) {
        super(message);
    }
}
