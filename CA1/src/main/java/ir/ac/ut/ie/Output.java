package ir.ac.ut.ie;

public class Output {
    private boolean success;
    private String data;

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
