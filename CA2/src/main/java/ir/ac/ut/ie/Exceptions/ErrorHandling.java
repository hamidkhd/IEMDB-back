package ir.ac.ut.ie.Exceptions;

public class ErrorHandling extends Exception {
    private final String message;

    public ErrorHandling(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}