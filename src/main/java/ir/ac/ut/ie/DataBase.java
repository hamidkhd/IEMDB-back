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
        String data = getConnection("/api/v2/movies");
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
        HashMap <Integer, Movie> movieCopy = new LinkedHashMap<>();
        for (Map.Entry<Integer, Movie> entry: moviesToSort.entrySet()) {
            if (!entry.getValue().getReleaseDate().equals("-"))
                movieCopy.put(entry.getKey(), entry.getValue());
        }
        List list = new LinkedList(movieCopy.entrySet());
        HashMap<Integer, Movie> sorted = new LinkedHashMap<>();
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
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
        String data = getConnection("/api/v2/actors");
        Actor[] actorsList = mapper.readValue(data, Actor[].class);
        for (Actor actor: actorsList) {
            actor.checkForInvalidCommand();
            if (existingActors.containsKey(actor.getId()))
                existingActors.get(actor.getId()).update(actor);
            else
                existingActors.put(actor.getId(), actor);

        }
    }

    public ArrayList<Movie> getActorMoviesPlayed(Integer actorId) {
        ArrayList<Movie> moviesPlayed = new ArrayList<>();
        for (Movie movie:movies.values())
            if(movie.getCast().contains(actorId))
                moviesPlayed.add(movie);
        return moviesPlayed;
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
            comment.setUsername(users.get(comment.getUserEmail()).getName());
            movies.get(comment.getMovieId()).addComment(comment, commentId);
            comments.put(commentId, comment);
            commentId += 1;
        }
    }

    public Movie getMovieById(Integer id) throws Exception {
        return movies.get(id);
    }

    public Actor getActorById(Integer id) throws Exception {
        return existingActors.get(id);
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

    private void searchByName(String searchValue) {
        for (Movie movie : movies.values())
            if (movie.getName().toUpperCase().contains(searchValue.toUpperCase()))
                searchedMovies.put(movie.getId(), movie);
    }

    private void searchByReleaseDate(String searchValue) {
        for (Movie movie : movies.values())
            if (movie.getReleaseDate().toUpperCase().contains(searchValue.toUpperCase()))
                searchedMovies.put(movie.getId(), movie);
    }

    private void searchByGenre(String searchValue) {
        for (Movie movie : movies.values())
            for(String genre:movie.getGenres())
                if (genre.contains(searchValue)) {
                    searchedMovies.put(movie.getId(), movie);
                    break;
            }
    }

    private void searchAll() {
        for(Movie movie : movies.values())
            searchedMovies.put(movie.getId(), movie);
    }

    public void setSearchedMovies(String searchValue, String searchBy) {
        searchedMovies.clear();
        if(searchBy.equals("genre"))
            searchByGenre(searchValue);
        if(searchBy.equals("name"))
            searchByName(searchValue);
        if(searchBy.equals("releaseDate"))
            searchByReleaseDate(searchValue);
        if(searchBy.equals(""))
            searchAll();
    }

    public Map<Integer, Movie> moviesToShow(boolean defaultSort, String searchBy, String searchValue) {
        setSearchedMovies(searchValue, searchBy);
        if (defaultSort)
            return searchedMovies;
        else
            return sortMoviesByDate(searchedMovies);

    }

    public User getAuthenticatedUser(String username, String password) {
        if (users.containsKey(username)) {
            if (users.get(username).getPassword().equals(password))
                return users.get(username);
        }
        User errorUser = new User();
        errorUser.setName("error");
        return errorUser;
    }

    public Comment addComment(String userEmail, Integer movieId, String text) {
        String username = users.get(userEmail).getName();
        Comment comment = new Comment(commentId, userEmail, movieId, text, username);
        comments.put(comment.getCommentId(), comment);
        movies.get(movieId).getComments().put(comment.getCommentId(), comment);
        commentId += 1;
        return comment;
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
