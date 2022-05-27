package ir.ac.ut.ie;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.ac.ut.ie.Entities.Actor;
import ir.ac.ut.ie.Entities.Comment;
import ir.ac.ut.ie.Entities.Movie;
import ir.ac.ut.ie.Entities.User;
import ir.ac.ut.ie.Repository.*;
import ir.ac.ut.ie.Utilities.HashCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class DataBase {
    private static DataBase instance;
    static private ObjectMapper mapper;
    static private String host;
    private static MovieRepository movieRepository = null;
    private static ActorRepository actorRepository = null;
    private static UserRepository userRepository = null;
    private static CommentRepository commentRepository = null;

    @Autowired
    public DataBase(MovieRepository movieRepository, ActorRepository actorRepository,
                    UserRepository userRepository, CommentRepository commentRepository) {
        DataBase.movieRepository = movieRepository;
        DataBase.actorRepository = actorRepository;
        DataBase.userRepository = userRepository;
        DataBase.commentRepository = commentRepository;
    }

    @PostConstruct
    public void initialize() {
        mapper = new ObjectMapper();
        host = "http://138.197.181.131:5000";
        setInformation();
    }

    public static DataBase getInstance() {
        if (instance == null)
            instance = new DataBase(movieRepository, actorRepository, userRepository, commentRepository);
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
            setActorMoviesPlayed();
        }
        catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void setMoviesList() throws Exception {
        String data = getConnection("/api/v2/movies");
        Movie[] moviesList = mapper.readValue(data, Movie[].class);
        movieRepository.saveAll(Arrays.asList(moviesList));
    }

    private void setActorsList() throws Exception {
        String data = getConnection("/api/v2/actors");
        Actor[] actorsList = mapper.readValue(data, Actor[].class);
        actorRepository.saveAll(Arrays.asList(actorsList));
    }

    private void setActorMoviesPlayed() {
        for (Actor actor : actorRepository.findAll()) {
            actor.setMoviesPlayed((ArrayList<Movie>) movieRepository.findAllByCast(actor.getId()));
            actorRepository.save(actor);
        }
    }

    public List<Movie> getActorMoviesPlayed(Integer actorId) {
        return movieRepository.findAllByCast(actorId);
    }

    private void setUsersList() throws Exception {
        String data = getConnection("/api/users");
        User[] usersList = mapper.readValue(data, User[].class);
        for (User user: usersList)
            user.setPassword(HashCreator.getInstance().getMD5Hash(user.getPassword()));
        userRepository.saveAll(Arrays.asList(usersList));
    }

    private void setCommentsList() throws Exception {
        String data = getConnection("/api/comments");
        Comment[] commentsList = mapper.readValue(data, Comment[].class);
        commentRepository.saveAll(Arrays.asList(commentsList));

        for (Comment comment : commentRepository.findAll()) {
            comment.setUsername(userRepository.findUserByEmail(comment.getUserEmail()).getName());
            Movie commentMovie = movieRepository.findMovieById(comment.getMovieId());
            commentMovie.addComment(comment);
            movieRepository.save(commentMovie);
            commentRepository.save(comment);
        }
    }

    public List<Movie> moviesToShow(boolean defaultSort, String searchBy, String searchValue) {
        List<Movie> searchedMoviesList = setSearchedMovies(searchValue, searchBy);
        if (defaultSort)
            return searchedMoviesList;
        else
        {
            List<Movie> tempMovies = movieRepository.findAllByOrderByReleaseDateDesc();
            tempMovies.retainAll(searchedMoviesList);
            return tempMovies;
        }
    }

    public List<Movie> setSearchedMovies(String searchValue, String searchBy) {
        List<Movie> searchedMoviesList = new ArrayList<>();
        if(searchBy.equals("genre"))
            searchedMoviesList = movieRepository.findAllByGenresContains(searchValue);
        if(searchBy.equals("name"))
            searchedMoviesList = movieRepository.findAllByNameContains(searchValue);
        if(searchBy.equals("releaseDate"))
            searchedMoviesList = movieRepository.findAllByReleaseDateAfter(searchValue);
        if(searchBy.equals(""))
            searchedMoviesList = movieRepository.findAllByOrderByImdbRateDesc();
        return searchedMoviesList;
    }

    public User getAuthenticatedUser(String username, String password) {
        User user = userRepository.findUserByEmailAndPassword(username, password);
        if (user != null)
            return user;
        User errorUser = new User();
        errorUser.setName("error");
        return errorUser;
    }

    public User addUser(String username, String password, String name, String nickname, String birth_date) throws ParseException, NoSuchAlgorithmException {
        if (userRepository.findUserByEmail(username) != null)
        {
            User errorUser = new User();
            errorUser.setName("error");
            return errorUser;
        }
        Date birthDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(birth_date);
        String hashPassword = HashCreator.getInstance().getMD5Hash(password);
        User user = new User(username, hashPassword, nickname, name, birthDate);
        userRepository.save(user);
        return user;
    }

    public Comment addComment(String userEmail, Integer movieId, String text) {
        String username = userRepository.findUserByEmail(userEmail).getName();
        Comment comment = new Comment(userEmail, movieId, text, username);
        movieRepository.findMovieById(movieId).addComment(comment);
        commentRepository.save(comment);
        movieRepository.save(movieRepository.findMovieById(movieId));
        return comment;
    }
}