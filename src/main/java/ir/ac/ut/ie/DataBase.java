package ir.ac.ut.ie;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.ac.ut.ie.Entities.*;
import ir.ac.ut.ie.Exceptions.*;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataBase {
    private static DataBase instance;
    static private ObjectMapper mapper;
    static private String host;
    private static Map<Integer, Actor> existingActors;
    private static HashMap<Integer, Movie> movies;
    private static HashMap<Integer, Movie> moviesSortedByDate;
    private static HashMap<Integer, Movie> searchedMovies;
    private static Map<String, User> users;
    private static Map<Integer, Comment> comments;
    private static Integer commentId;


    private DataBase() throws IOException {
        mapper = new ObjectMapper();
        host = "http://138.197.181.131:5000";
        existingActors = new HashMap<>();
        movies = new LinkedHashMap<>();
        moviesSortedByDate = new LinkedHashMap<>();
        searchedMovies = new LinkedHashMap<>();
        users = new HashMap<>();
        comments = new HashMap<>();
        commentId = 1;
        setInformation();
    }

    public static DataBase getInstance() throws IOException {
        if (instance == null)
            instance = new DataBase();
        return instance;
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

    private void setInformation() {
        try {
            setActorsList();
            setMoviesList();
            setUsersList();
            setCommentsList();
        }
        catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    static void setMoviesList() throws Exception {
        String data = getConnection("/api/movies");
        Movie[] moviesList = mapper.readValue(data, Movie[].class);
        Map<Integer, Movie> moviesNotSorted = new HashMap<>();
        for (Movie movie: moviesList) {
            movie.checkForInvalidCommand();
            checkCastExist(movie.getCast());

            List<String> actors = new ArrayList<>();
            for (Integer i : movie.getCast())
                actors.add(existingActors.get(i).getName());
            movie.setCastName(actors);

            if (moviesNotSorted.containsKey(movie.getId()))
                moviesNotSorted.get(movie.getId()).update(movie);
            else {
                movie.initialValues();
                moviesNotSorted.put(movie.getId(), movie);
            }
        }
        sortMoviesByRate(moviesNotSorted);
        moviesSortedByDate = sortMoviesByDate(movies);
    }

    private static void sortMoviesByRate(Map<Integer, Movie> moviesNotSorted) {
        List list = new LinkedList(moviesNotSorted.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) (((Movie)((Map.Entry) (o2)).getValue())).getImdbRate()).compareTo((((Movie)((Map.Entry) (o1)).getValue())).getImdbRate());
            }
        });
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            movies.put((Integer) entry.getKey(), (Movie) entry.getValue());
        }
    }

    private static HashMap<Integer, Movie> sortMoviesByDate(HashMap<Integer, Movie> moviesToSort) {
        List list = new LinkedList(moviesToSort.entrySet());
        HashMap<Integer, Movie> sorted = new LinkedHashMap<>();
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    return sdf.parse(((Movie) ((Map.Entry) o1).getValue()).getReleaseDate()).before(sdf.parse(((Movie) ((Map.Entry) o2).getValue()).getReleaseDate())) ? 1 : -1;
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sorted.put((Integer) entry.getKey(), (Movie) entry.getValue());
        }
        return sorted;
    }

     private void setActorsList() throws Exception {
        String data = getConnection("/api/actors");
        Actor[] actorsList = mapper.readValue(data, Actor[].class);
        for (Actor actor: actorsList) {
            actor.checkForInvalidCommand();
            if (existingActors.containsKey(actor.getId()))
                existingActors.get(actor.getId()).update(actor);
            else
                existingActors.put(actor.getId(), actor);
        }
    }

     private void setUsersList() throws Exception {
        String data = getConnection("/api/users");
        User[] usersList = mapper.readValue(data, User[].class);
        for (User user: usersList) {
            checkUserExist(user.getEmail());
            users.put(user.getEmail(), user);
        }

    }

    private void setCommentsList() throws Exception {
        String data = getConnection("/api/comments");
        Comment[] commentsList = mapper.readValue(data, Comment[].class);
        for(Comment comment: commentsList) {
            comment.checkForInvalidCommand();
            userNotFound(comment.getUserEmail());
            movieNotFound(comment.getMovieId());
            movies.get(comment.getMovieId()).addComment(comment, commentId);
            comments.put(commentId, comment);
            commentId += 1;
        }
    }

    public void addComment(String userEmail, Integer movieId, String text) throws Exception {
        Comment new_comment = new Comment(commentId);
        new_comment.setUserEmail(userEmail);
        new_comment.setMovieId(movieId);
        new_comment.setText(text);
        userNotFound(new_comment.getUserEmail());
        movieNotFound(new_comment.getMovieId());
        movies.get(new_comment.getMovieId()).addComment(new_comment, commentId);
        comments.put(commentId, new_comment);
        commentId += 1;
    }


    public void removeFromWatchList(String userEmail, Integer movieId) throws Exception {
        userNotFound(userEmail);
        movieNotFound(movieId);
        users.get(userEmail).removeFromWatchList(movieId);
    }

    public void addToWatchList(String userEmail, Integer movieId) throws Exception {
        userNotFound(userEmail);
        movieNotFound(movieId);
        int ageLimit = movies.get(movieId).getAgeLimit();
        users.get(userEmail).addToWatchList(movieId, ageLimit);
    }

    public void rateMovie(Rate rate) throws Exception {
        userNotFound(rate.getUserEmail());
        movieNotFound(rate.getMovieId());
        rate.hasError();
        movies.get(rate.getMovieId()).addRate(rate);
    }

    public void voteComment(Vote vote) throws Exception {
        userNotFound(vote.getUserEmail());
        commentNotFound(vote.getCommentId());
        vote.hasError();
        comments.get(vote.getCommentId()).addVote(vote);
    }


    public Movie getMovieById(Integer id) throws Exception {
        return movies.get(id);
    }

    public static void checkCastExist(List<Integer> cast) throws Exception {
        for (Integer id : cast)
            if (!existingActors.containsKey(id))
                throw new ActorNotFound();
    }

    public static void checkUserExist(String userEmail) throws Exception {
        if (users.containsKey(userEmail))
            throw new UserAlreadyExists();
    }

    public void userNotFound(String userEmail) throws Exception {
        if (!users.containsKey(userEmail))
            throw new UserNotFound();
    }

    public void actorNotFound(Integer userEmail) throws Exception {
        if (!existingActors.containsKey(userEmail))
            throw new ActorNotFound();
    }

    public void movieNotFound(Integer movieId) throws Exception {
        if (!movies.containsKey(movieId))
            throw new MovieNotFound();
    }

    public void commentNotFound(Integer commentId) throws Exception {
        if (!comments.containsKey(commentId))
            throw new CommentNotFound();
    }

    public void setSearchedMovies(String searchedMovieName) {
        searchedMovies.clear();
        for (Movie movie : movies.values())
            if (movie.getName().toUpperCase().contains(searchedMovieName.toUpperCase()))
                searchedMovies.put(movie.getId(), movie);
    }

    public Map<Integer, Movie> moviesToShow() {
        if (! UserManager.getInstance().isSearch()) {
            if (UserManager.getInstance().isDefaultSort())
                return movies;
            else
                return moviesSortedByDate;
        }
        else {
            if (UserManager.getInstance().isDefaultSort())
                return searchedMovies;
            else
                return sortMoviesByDate(searchedMovies);
        }
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public Map<Integer, Movie> getMovies() {
        return movies;
    }

    public Map<Integer, Actor> getExistingActors() {
        return existingActors;
    }

    public static Map<Integer, Comment> getComments() {
        return comments;
    }

    public static HashMap<Integer, Movie> getMoviesSortedByDate() {
        return moviesSortedByDate;
    }
}
