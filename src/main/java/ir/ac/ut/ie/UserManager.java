package ir.ac.ut.ie;

import ir.ac.ut.ie.Entities.Movie;


import java.util.ArrayList;

public class UserManager {
    private String currentUser;
    private static UserManager instance;
    private boolean search;
    private boolean defaultSort;

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

    public boolean isSearch() {
        return search;
    }
    public void setSearch(boolean search) {
        this.search = search;
    }

    public boolean isDefaultSort() {
        return defaultSort;
    }

    public void setDefaultSort(boolean defaultSort) {
        this.defaultSort = defaultSort;
    }
}
