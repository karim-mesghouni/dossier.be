package com.softline.dossier.be.service.exceptions;

/**
 * This exception is made so that we can send error messages to the client side.
 * if we detect this class in the exception handler we are going to send its message to the user
 * check the ExceptionsHandler class
 *
 * @see com.softline.dossier.be.security.config.ExceptionsHandler
 */
public class ClientReadableException extends Exception {
    /**
     * @param message the exception message, should not contain any sensitive data,
     *                this message is designed to be readable for the client
     */
    public ClientReadableException(String message) {
        super(message);
    }
}
