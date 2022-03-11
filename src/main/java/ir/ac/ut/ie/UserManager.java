package ir.ac.ut.ie;

public class UserManager {
    private String currentUser;
    private static UserManager instance;

    private UserManager() {
        currentUser = null;
    }

    public static UserManager getInstance() {
        if (instance == null)
            instance = new UserManager();
        return instance;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getCurrentUser() {
        return currentUser;
    }
}
