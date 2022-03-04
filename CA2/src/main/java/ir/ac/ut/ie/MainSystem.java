package ir.ac.ut.ie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.*;
import ir.ac.ut.ie.Entities.*;
import ir.ac.ut.ie.Exceptions.*;
import org.jetbrains.annotations.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;


public class MainSystem {
    private static ObjectMapper mapper;
    private static Map<Integer, Actor> existingActors;
    private static Map<Integer, Movie> movies;
    private static Map<String, User> users;
    private static Map<Integer, Comment> comments;
    private static Integer commentId;
    private static SimpleDateFormat df;


    public MainSystem() {
        mapper = new ObjectMapper();
        df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.setDateFormat(df);
        existingActors = new HashMap<>();
        movies = new HashMap<>();
        users = new HashMap<>();
        comments = new HashMap<>();
        commentId = 1;
    }


    public static void addActorFromDataBase(Actor actor) throws Exception {
        actor.checkForInvalidCommand();
        if (existingActors.containsKey(actor.getId()))
            existingActors.get(actor.getId()).update(actor);
        else
            existingActors.put(actor.getId(), actor);
        Server.printOutput(new Output(true, "actor added successfully"));
    }

    public static void addMovieFromDataBase(Movie movie) throws Exception {
        movie.checkForInvalidCommand();
        checkCastExist(movie.getCast());

        List<String> actors = new ArrayList<String>();
        for (Integer i : movie.getCast())
            actors.add(getExistingActors().get(i).getName());
        movie.setCastName(actors);

        if (movies.containsKey(movie.getId()))
            movies.get(movie.getId()).update(movie);
        else {
            movie.initialValues();
            movies.put(movie.getId(), movie);
        }
        Server.printOutput(new Output(true, "movie added successfully"));
    }

    public static void addUserFromDataBase(User user) throws Exception {
        checkUserExist(user.getEmail());
        users.put(user.getEmail(), user);
        Server.printOutput(new Output(true, "user added successfully"));
    }

    public static void addCommentFromDataBase(Comment comment) throws Exception {
        comment.checkForInvalidCommand();
        userNotFound(comment.getUserEmail());
        movieNotFound(comment.getMovieId());

        movies.get(comment.getMovieId()).addComment(comment, commentId);
        comments.put(commentId, comment);
        Server.printOutput(new Output(true, "comment with id " + commentId.toString() + " added successfully"));
        commentId += 1;
    }


    private static void checkCastExist(List<Integer> cast) throws Exception {
        for (Integer id : cast)
            if (!existingActors.containsKey(id))
                throw new ActorNotFound();
    }

    private static void checkUserExist(String userEmail) throws Exception {
        if (users.containsKey(userEmail))
            throw new UserAlreadyExists();
    }

    private static void userNotFound(String userEmail) throws Exception {
        if (!users.containsKey(userEmail))
            throw new UserNotFound();
    }

    private static void movieNotFound(Integer movieId) throws Exception {
        if (!movies.containsKey(movieId))
            throw new MovieNotFound();
    }

    private static void commentNotFound(Integer commentId) throws Exception {
        if (!comments.containsKey(commentId))
            throw new CommentNotFound();
    }

    public static Map<Integer, Actor> getExistingActors() {
        return existingActors;
    }

    static class watchMoviesListHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                File file = new File("src/main/resources/movies.html");
                Document document = Jsoup.parse(file, "UTF-8");
                Elements table = document.getElementsByTag("table");
                getMoviesList();
                for (Movie movie : movies.values())
                    table.get(0).append(movie.getHtmlTableForWatchMoviesListHandler());

                context.contentType("text/html");
                context.result(document.toString());
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }

    public static void getMoviesList() throws Exception {
        List<ObjectNode> objects = new ArrayList<>();
        for (Map.Entry<Integer, Movie> entry : movies.entrySet()) {
            ObjectNode movie = mapper.createObjectNode();
            entry.getValue().createInformationJson(mapper, movie);
            objects.add(movie);
        }
        ArrayNode arrayNode = mapper.valueToTree(objects);
        ObjectNode movieList = mapper.createObjectNode();
        movieList.putArray("MoviesList").addAll(arrayNode);
        String data = mapper.writeValueAsString(movieList);
        Server.printOutput(new Output(true, data));
    }


    static class watchMoviePageHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                File file = new File("src/main/resources/movie.html");
                Document document = Jsoup.parse(file, "UTF-8");
                Elements table = document.getElementsByTag("table");

                String movie_id = context.pathParam("movie_id");
                movieNotFound(Integer.valueOf(movie_id));
                Movie movie = getMovieById(Integer.valueOf(movie_id));

                document.getElementById("name").text("name: " + movie.getName());
                document.getElementById("summary").text("summary: " + movie.getSummary());
                document.getElementById("releaseDate").text("releaseDate: " + movie.getReleaseDate());
                document.getElementById("director").text("director: " + movie.getDirector());
                document.getElementById("writers").text("writers: " + String.join(", ", movie.getWriters()));
                document.getElementById("genres").text("genres: " + String.join(", ", movie.getGenres()));
                document.getElementById("cast").text("cast: " + String.join(", ", movie.getCastName()));
                document.getElementById("imdbRate").text("imdb Rate: " + movie.getImdbRate());
                document.getElementById("rating").text("rating: " + movie.getRating());
                document.getElementById("duration").text("duration: " + movie.getDuration());
                document.getElementById("ageLimit").text("ageLimit: " + movie.getAgeLimit());

                for (Map.Entry<Integer, Comment> comment : movie.getComments().entrySet()) {
                    String nickname = users.get(comment.getValue().getUserEmail()).getNickname();
                    table.get(0).append(comment.getValue().getHtmlTableForWatchMoviePageHandler(nickname));
                }

                context.contentType("text/html");
                context.result(document.toString());
            } catch (MovieNotFound exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                context.redirect("/404");
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }

    public static Movie getMovieById(Integer id) throws Exception {
        movies.get(id).printMovieInformation(mapper, existingActors);
        return movies.get(id);
    }

    static class watchMoviePageNotLoginHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                String movie_id = context.pathParam("movie_id");
                String user_id = context.formParam("user_id");
                userNotFound(user_id);
                movieNotFound(Integer.valueOf(movie_id));
                context.redirect("/movies/login/" + movie_id + "/" + user_id);
            } catch (MovieNotFound | UserNotFound exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                context.redirect("/404");
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }

    static class watchMoviePageLoginHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                File file = new File("src/main/resources/movie_login.html");
                Document document = Jsoup.parse(file, "UTF-8");
                Elements table = document.getElementsByTag("table");

                String movie_id = context.pathParam("movie_id");
                movieNotFound(Integer.valueOf(movie_id));
                Movie movie = getMovieById(Integer.valueOf(movie_id));

                document.getElementById("name").text("name: " + movie.getName());
                document.getElementById("summary").text("summary: " + movie.getSummary());
                document.getElementById("releaseDate").text("releaseDate: " + movie.getReleaseDate());
                document.getElementById("director").text("director: " + movie.getDirector());
                document.getElementById("writers").text("writers: " + String.join(", ", movie.getWriters()));
                document.getElementById("genres").text("genres: " + String.join(", ", movie.getGenres()));
                document.getElementById("cast").text("cast: " + String.join(", ", movie.getCastName()));
                document.getElementById("imdbRate").text("imdb Rate: " + movie.getImdbRate());
                document.getElementById("rating").text("rating: " + movie.getRating());
                document.getElementById("duration").text("duration: " + movie.getDuration());
                document.getElementById("ageLimit").text("ageLimit: " + movie.getAgeLimit());

                for (Map.Entry<Integer, Comment> comment : movie.getComments().entrySet()) {
                    String nickname = users.get(comment.getValue().getUserEmail()).getNickname();
                    table.get(0).append(comment.getValue().watchMoviePageLoginHandler(nickname));
                }

                context.contentType("text/html");
                context.result(document.toString());
            } catch (MovieNotFound exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                context.redirect("/404");
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }

    static class watchMoviePageLoginPostHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                String movie_id = context.pathParam("movie_id");
                String user_id = context.pathParam("user_id");
                userNotFound(user_id);
                movieNotFound(Integer.valueOf(movie_id));

                String quantity = context.formParam("quantity");
                String add_to = context.formParam("add_to");
                String like = context.formParam("like");
                String dislike = context.formParam("dislike");

                if (quantity != null)
                    context.redirect("/rateMovie/" + user_id + "/" + movie_id + "/" + quantity);
                else if (add_to != null)
                    context.redirect("/watchList/" + user_id + "/" + movie_id);
                else if (like != null)
                    context.redirect("/voteComment/" + user_id + "/" + like + "/" + 1);
                else if (dislike != null)
                    context.redirect("/voteComment/" + user_id + "/" + dislike + "/" + -1);
            } catch (MovieNotFound | UserNotFound exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                context.redirect("/404");
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }


    static class watchActorPageHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                File file = new File("src/main/resources/actor.html");
                Document document = Jsoup.parse(file, "UTF-8");
                Elements table = document.getElementsByTag("table");

                String actor_id = context.pathParam("actor_id");
                List<Integer> casts = new ArrayList<>();
                casts.add(Integer.valueOf(actor_id));
                checkCastExist(casts);
                Actor actor = existingActors.get(Integer.valueOf(actor_id));

                int act_in = 0;
                for (Movie movie : movies.values()) {
                    if (movie.getCast().contains(actor.getId())) {
                        table.get(0).append(movie.getHtmlTableForWatchActorPageHandler());
                        act_in += 1;
                    }
                }

                document.getElementById("name").text("Name: " + actor.getName());
                document.getElementById("birthDate").text("Birth Date: " + actor.getBirthDate());
                document.getElementById("nationality").text("Nationality: " + actor.getNationality());
                document.getElementById("tma").text("Total movies acted in: " + act_in);

                context.contentType("text/html");
                context.result(document.toString());
            } catch (ActorNotFound exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                context.redirect("/404");
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }


    static class showUserWatchListHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                File file = new File("src/main/resources/watchlist.html");
                Document document = Jsoup.parse(file, "UTF-8");
                Elements table = document.getElementsByTag("table");

                String user_id = context.pathParam("user_id");
                User user = users.get(user_id);

                document.getElementById("name").text("Name: " + user.getName());
                document.getElementById("nickname").text("Nickname: " + user.getNickname());

                for (Movie movie : user.getWatchList(mapper, movies))
                    table.get(0).append(movie.showUserWatchListHandler(user_id));

                getWatchList(user_id);

                context.contentType("text/html");
                context.result(document.toString());
            } catch (UserNotFound exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                context.redirect("/404");
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }

    public static void getWatchList(String userEmail) throws Exception {
        userNotFound(userEmail);
        users.get(userEmail).getWatchList(mapper, movies);
    }

    static class removeFromUserWatchListHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                String user_id = context.pathParam("user_id");
                String movie_id = context.formParam("movie_id");
                assert movie_id != null;
                removeFromWatchList(user_id, Integer.valueOf(movie_id));
                context.redirect("/watchList/" + user_id);
            } catch (MovieNotFound | UserNotFound exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                context.redirect("/404");
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }

    public static void removeFromWatchList(String userEmail, Integer movieId) throws Exception {
        userNotFound(userEmail);
        movieNotFound(movieId);
        users.get(userEmail).removeFromWatchList(movieId);
    }


    static class addToUserWatchListHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                String user_id = context.pathParam("user_id");
                String movie_id = context.pathParam("movie_id");

                addToWatchList(user_id, Integer.valueOf(movie_id));
                context.redirect("/200");
            } catch (MovieNotFound | UserNotFound exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                context.redirect("/404");
            } catch (AgeLimitError exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                context.redirect("/403");
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }

    public static void addToWatchList(String userEmail, Integer movieId) throws Exception {
        userNotFound(userEmail);
        movieNotFound(movieId);
        int ageLimit = movies.get(movieId).getAgeLimit();
        users.get(userEmail).addToWatchList(movieId, ageLimit);
    }


    static class rateMovieHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                String user_id = context.pathParam("user_id");
                String movie_id = context.pathParam("movie_id");
                String rate = context.pathParam("rate");

                rateMovie(new Rate(user_id, Integer.valueOf(movie_id), Float.parseFloat(rate)));

                File file = new File("src/main/resources/200.html");
                Document document = Jsoup.parse(file, "UTF-8");

                context.contentType("text/html");
                context.result(document.toString());
            } catch (MovieNotFound | UserNotFound exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                context.redirect("/404");
            } catch (InvalidRateScore exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                context.redirect("/403");
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }
    public static void rateMovie(Rate rate) throws Exception {
        userNotFound(rate.getUserEmail());
        movieNotFound(rate.getMovieId());
        rate.hasError();
        movies.get(rate.getMovieId()).addRate(rate);
        Server.printOutput(new Output(true, "movie rated successfully"));
    }

    static class voteCommentHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                String user_id = context.pathParam("user_id");
                String comment_id = context.pathParam("comment_id");
                String vote = context.pathParam("vote");

                voteComment(new Vote(user_id, Integer.valueOf(comment_id), Integer.valueOf(vote)));

                File file = new File("src/main/resources/200.html");
                Document document = Jsoup.parse(file, "UTF-8");

                context.contentType("text/html");
                context.result(document.toString());
            } catch (CommentNotFound | UserNotFound exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                context.redirect("/404");
            } catch (InvalidVoteValue exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                context.redirect("/403");
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }
    public static void voteComment(Vote vote) throws Exception {
        userNotFound(vote.getUserEmail());
        commentNotFound(vote.getCommentId());
        vote.hasError();
        comments.get(vote.getCommentId()).addVote(vote);
        Server.printOutput(new Output(true, "comment voted successfully"));
    }


    static class searchMovieByReleaseDateHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                File file = new File("src/main/resources/movies.html");
                Document document = Jsoup.parse(file, "UTF-8");
                Elements table = document.getElementsByTag("table");

                String start_year = context.pathParam("start_year");
                String end_year = context.pathParam("end_year");

                for (Movie movie : getMoviesByDate(start_year, end_year))
                    table.get(0).append(movie.getHtmlTableForWatchMoviesListHandler());

                context.contentType("text/html");
                context.result(document.toString());
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }
    public static List<Movie> getMoviesByDate(String start_date, String end_date) throws Exception {
        List<Movie> dateMovies = new ArrayList<>();

        for (Map.Entry<Integer, Movie> entry : movies.entrySet()) {
            if (df.parse(start_date).before(df.parse(entry.getValue().getReleaseDate())) &&
                    df.parse(end_date).after(df.parse(entry.getValue().getReleaseDate()))) {
                dateMovies.add(entry.getValue());
            }
        }
        return dateMovies;
    }

    static class searchMovieByGenreHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            System.out.println("ok");
            try {
                File file = new File("src/main/resources/movies.html");
                Document document = Jsoup.parse(file, "UTF-8");
                Elements table = document.getElementsByTag("table");

                String genre = context.pathParam("genre");

                for (Movie movie : getMoviesByGenre(genre))
                    table.get(0).append(movie.getHtmlTableForWatchMoviesListHandler());

                context.contentType("text/html");
                context.result(document.toString());
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }
    public static List<Movie> getMoviesByGenre(String genre) throws Exception {
        List<ObjectNode> moviesObjectNode = new ArrayList<>();
        List<Movie> genreMovies = new ArrayList<>();

        for (Map.Entry<Integer, Movie> entry : movies.entrySet()) {
            if (entry.getValue().genreMatch(genre)) {
                ObjectNode movie = mapper.createObjectNode();
                entry.getValue().createInformationJson(mapper, movie);
                moviesObjectNode.add(movie);
                genreMovies.add(entry.getValue());
            }
        }
        ObjectNode moviesListByGenre = mapper.createObjectNode();
        ArrayNode moviesArrayNode = mapper.valueToTree(moviesObjectNode);
        moviesListByGenre.putArray("MoviesListByGenre").addAll(moviesArrayNode);
        String outputData = mapper.writeValueAsString(moviesListByGenre);
        Server.printOutput(new Output(true, outputData));
        return genreMovies;
    }


    static class page200 implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                File file = new File("src/main/resources/200.html");
                Document document = Jsoup.parse(file, "UTF-8");
                context.contentType("text/html");
                context.result(document.toString());
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }

    static class page403 implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                File file = new File("src/main/resources/403.html");
                Document document = Jsoup.parse(file, "UTF-8");

                context.contentType("text/html");
                context.result(document.toString());
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }

    static class page404 implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                File file = new File("src/main/resources/404.html");
                Document document = Jsoup.parse(file, "UTF-8");

                context.contentType("text/html");
                context.result(document.toString());
            } catch (Exception exception) {
                Server.printOutput(new Output(false, exception.getMessage()));
                throw exception;
            }
        }
    }

}