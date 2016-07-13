package com.guidewire.devtraining.fireworks;

/**
 * An exception used by the Environment class.
 *
 * @version 1.0
 */
public class EnvironmentException extends Exception {

    /**
     * Accepts a specific message about the problem.
     *
     * @param message
     */
    public EnvironmentException(String message) {
        super(message);
    }

}
