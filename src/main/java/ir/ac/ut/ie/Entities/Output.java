package ir.ac.ut.ie.Entities;

public class Output {
    private final boolean success;
    private final String data;

    public Output(boolean success, String data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }
    public String getData() {
        return data;
    }
}