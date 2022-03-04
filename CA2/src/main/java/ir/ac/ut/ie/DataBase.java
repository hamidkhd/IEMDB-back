package ir.ac.ut.ie;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.ac.ut.ie.Entities.Actor;
import ir.ac.ut.ie.Entities.Comment;
import ir.ac.ut.ie.Entities.Movie;
import ir.ac.ut.ie.Entities.User;

import java.io.*;
import java.net.*;


public class DataBase {
    static private ObjectMapper mapper;
    static private String host;
    private static Movie[] moviesList;
    static private Actor[] actorsList;
    static private User[] usersList;
    static private Comment[] commentsList;


    public DataBase() throws IOException {
        mapper = new ObjectMapper();
        host = "http://138.197.181.131:5000";
        setInformation();
    }

    static private String getConnection(String path) throws IOException {
        URL url = new URL(host + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        return line;
    }

    static void setInformation() throws IOException {
        try {
            setMoviesList();
            setActorsList();
            setUsersList();
            setCommentsList();
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
    }

    static void setMoviesList() throws IOException {
        String data = getConnection("/api/movies");
        moviesList = mapper.readValue(data, Movie[].class);
    }

    static private void setActorsList() throws IOException {
        String data = getConnection("/api/actors");
        actorsList = mapper.readValue(data, Actor[].class);
    }

    static private void setUsersList() throws IOException {
        String data = getConnection("/api/users");
        usersList = mapper.readValue(data, User[].class);
    }

    static private void setCommentsList() throws IOException {
        String data = getConnection("/api/comments");
        commentsList = mapper.readValue(data, Comment[].class);
    }

    public static Movie[] getMoviesList() {
        return moviesList;
    }

    public static Actor[] getActorsList() {
        return actorsList;
    }

    public static User[] getUsersList() {
        return usersList;
    }

    public static Comment[] getCommentsList() {
        return commentsList;
    }

}
