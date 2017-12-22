package com.ithinkrok.util.math;

/**
 * Represents an issue caused by an invalid expression given to the expression parser.
 */
public class ExpressionException extends RuntimeException {

    public ExpressionException(String message) {
        super(message);
    }

    public ExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
