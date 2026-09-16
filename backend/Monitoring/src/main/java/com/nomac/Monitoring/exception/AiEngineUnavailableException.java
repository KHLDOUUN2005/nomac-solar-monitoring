package com.nomac.Monitoring.exception;

/**
 * Thrown when the AI engine service is unreachable, times out, or returns
 * a response that can't be interpreted (missing fields, wrong types).
 * Kept distinct from a generic RuntimeException so the global exception
 * handler can map it to a 503 (Service Unavailable) instead of a bare 500.
 */
public class AiEngineUnavailableException extends RuntimeException {

    public AiEngineUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiEngineUnavailableException(String message) {
        super(message);
    }
}