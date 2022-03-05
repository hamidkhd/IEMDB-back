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
    private static SimpleDateFormat df;


    public MainSystem() {
        mapper = new ObjectMapper();
        df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.setDateFormat(df);
    }

    static class moviesListHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            File file = new File("src/main/resources/movies.html");
            Document document = Jsoup.parse(file, "UTF-8");
            Elements table = document.getElementsByTag("table");
            for (Movie movie : DataBase.getMovies().values())
                table.get(0).append(movie.getHtmlTableForWatchMoviesListHandler());
            context.contentType("text/html");
            context.result(document.toString());
        }
    }

    static class moviePageHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                File file = new File("src/main/resources/movie.html");
                Document document = Jsoup.parse(file, "UTF-8");
                Elements table = document.getElementsByTag("table");

                String movie_id = context.pathParam("movie_id");
                DataBase.movieNotFound(Integer.valueOf(movie_id));
                Movie movie = DataBase.getMovieById(Integer.valueOf(movie_id));

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
                    String nickname = DataBase.getUsers().get(comment.getValue().getUserEmail()).getNickname();
                    table.get(0).append(comment.getValue().getHtmlTableForWatchMoviePageHandler(nickname));
                }

                context.contentType("text/html");
                context.result(document.toString());
            } catch (MovieNotFound exception) {
                context.redirect("/404");
            }
        }
    }



    static class moviePageGetLoginInformationHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                String movie_id = context.pathParam("movie_id");
                String user_id = context.formParam("user_id");
                DataBase.userNotFound(user_id);
                DataBase.movieNotFound(Integer.valueOf(movie_id));
                context.redirect("/movies/login/" + movie_id + "/" + user_id);
            } catch (MovieNotFound | UserNotFound exception) {
                context.redirect("/404");
            }
        }
    }

    static class moviePageAfterLoginHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                File file = new File("src/main/resources/movie_login.html");
                Document document = Jsoup.parse(file, "UTF-8");
                Elements table = document.getElementsByTag("table");

                String movie_id = context.pathParam("movie_id");
                DataBase.movieNotFound(Integer.valueOf(movie_id));
                Movie movie = DataBase.getMovieById(Integer.valueOf(movie_id));

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
                    String nickname = DataBase.getUsers().get(comment.getValue().getUserEmail()).getNickname();
                    table.get(0).append(comment.getValue().watchMoviePageLoginHandler(nickname));
                }

                context.contentType("text/html");
                context.result(document.toString());
            } catch (MovieNotFound exception) {
                context.redirect("/404");
            }
        }
    }

    static class moviePageAfterLoginGetRateHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                String movie_id = context.pathParam("movie_id");
                String user_id = context.pathParam("user_id");
                DataBase.userNotFound(user_id);
                DataBase.movieNotFound(Integer.valueOf(movie_id));

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
                context.redirect("/404");
            }
        }
    }


    static class actorPageHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                File file = new File("src/main/resources/actor.html");
                Document document = Jsoup.parse(file, "UTF-8");
                Elements table = document.getElementsByTag("table");

                String actor_id = context.pathParam("actor_id");
                List<Integer> casts = new ArrayList<>();
                casts.add(Integer.valueOf(actor_id));
                DataBase.checkCastExist(casts);
                Actor actor = DataBase.getExistingActors().get(Integer.valueOf(actor_id));

                int act_in = 0;
                for (Movie movie : DataBase.getMovies().values()) {
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
                context.redirect("/404");
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
                User user = DataBase.getUsers().get(user_id);

                document.getElementById("name").text("Name: " + user.getName());
                document.getElementById("nickname").text("Nickname: " + user.getNickname());

                for (Integer movieId : user.getWatchList()) {
                    Movie movie = DataBase.getMovieById(movieId);
                    table.get(0).append(movie.showUserWatchListHandler(user_id));
                }

               DataBase.userNotFound(user_id);

                context.contentType("text/html");
                context.result(document.toString());
            } catch (UserNotFound exception) {
                context.redirect("/404");
            }
        }
    }

    static class removeFromUserWatchListHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                String user_id = context.pathParam("user_id");
                String movie_id = context.formParam("movie_id");
                assert movie_id != null;
                DataBase.removeFromWatchList(user_id, Integer.valueOf(movie_id));
                context.redirect("/watchList/" + user_id);
            } catch (MovieNotFound | UserNotFound exception) {
                context.redirect("/404");
            }
        }
    }

    static class addToUserWatchListHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                String user_id = context.pathParam("user_id");
                String movie_id = context.pathParam("movie_id");

                DataBase.addToWatchList(user_id, Integer.valueOf(movie_id));
                context.redirect("/200");
            } catch (MovieNotFound | UserNotFound exception) {
                context.redirect("/404");
            } catch (AgeLimitError exception) {
                context.redirect("/403");
            }
        }
    }


    static class rateMovieHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                String user_id = context.pathParam("user_id");
                String movie_id = context.pathParam("movie_id");
                String rate = context.pathParam("rate");
                DataBase.rateMovie(new Rate(user_id, Integer.valueOf(movie_id), Float.parseFloat(rate)));
                context.redirect("/200");
            } catch (MovieNotFound | UserNotFound exception) {
                context.redirect("/404");
            } catch (InvalidRateScore exception) {
                context.redirect("/403");
            }
        }
    }

    static class voteCommentHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            try {
                String user_id = context.pathParam("user_id");
                String comment_id = context.pathParam("comment_id");
                String vote = context.pathParam("vote");
                DataBase.voteComment(new Vote(user_id, Integer.valueOf(comment_id), Integer.valueOf(vote)));
                context.redirect("/200");
            } catch (CommentNotFound | UserNotFound exception) {
                context.redirect("/404");
            } catch (InvalidVoteValue exception) {
                context.redirect("/403");
            }
        }
    }


    static class searchMovieByReleaseDateHandler implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            File file = new File("src/main/resources/movies.html");
            Document document = Jsoup.parse(file, "UTF-8");
            Elements table = document.getElementsByTag("table");

            String start_year = context.pathParam("start_year");
            String end_year = context.pathParam("end_year");
            for (Movie movie : getMoviesByDate(start_year, end_year))
                table.get(0).append(movie.getHtmlTableForWatchMoviesListHandler());
            context.contentType("text/html");
            context.result(document.toString());
        }
    }

    public static List<Movie> getMoviesByDate(String start_date, String end_date) throws Exception {
        List<Movie> dateMovies = new ArrayList<>();
        for (Map.Entry<Integer, Movie> entry : DataBase.getMovies().entrySet()) {
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

            File file = new File("src/main/resources/movies.html");
            Document document = Jsoup.parse(file, "UTF-8");
            Elements table = document.getElementsByTag("table");

            String genre = context.pathParam("genre");

            for (Movie movie : getMoviesByGenre(genre))
                table.get(0).append(movie.getHtmlTableForWatchMoviesListHandler());

            context.contentType("text/html");
            context.result(document.toString());
        }
    }

    public static List<Movie> getMoviesByGenre(String genre) throws Exception {
        List<Movie> genreMovies = new ArrayList<>();
        for (Movie movie : DataBase.getMovies().values()) {
            if (movie.genreMatch(genre))
                genreMovies.add(movie);
        }
        return genreMovies;
    }

    static class page200 implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            File file = new File("src/main/resources/200.html");
            Document document = Jsoup.parse(file, "UTF-8");
            context.contentType("text/html");
            context.result(document.toString());
        }
    }

    static class page403 implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            File file = new File("src/main/resources/403.html");
            Document document = Jsoup.parse(file, "UTF-8");
            context.contentType("text/html");
            context.result(document.toString());
        }
    }

    static class page404 implements Handler {
        @Override
        public void handle(@NotNull Context context) throws Exception {
            File file = new File("src/main/resources/404.html");
            Document document = Jsoup.parse(file, "UTF-8");
            context.contentType("text/html");
            context.result(document.toString());
        }
    }
}