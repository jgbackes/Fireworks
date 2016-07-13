package com.guidewire.devtraining.fireworks;

/**
 * Thrown by the Emitter object if an illegal angle is supplied.
 */
public class EmitterException extends Exception {

    /**
     * Accepts a specific message about the problem.
     *
     * @param message
     */
    public EmitterException(String message) {
        super(message);
    }

}
