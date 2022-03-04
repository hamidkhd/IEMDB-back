package ir.ac.ut.ie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.*;
import ir.ac.ut.ie.Entities.*;
import ir.ac.ut.ie.Exceptions.ErrorHandling;

import java.io.*;


public class Server {
    private final Javalin app;
    private static MainSystem mainSystem;
    private static ObjectMapper mapper;

    public Server(int port) {
        app = Javalin.create().start(port);
        mainSystem = new MainSystem();
        mapper = new ObjectMapper();
    }

    public static void main(String[] args) throws Exception {
        DataBase dataBase = new DataBase();
        Server server = new Server(8080);
        server.setTakenInformation();
        server.startServer();
    }

    private void setTakenInformation() throws Exception {
        try {
            for (Actor actor : DataBase.getActorsList())
                MainSystem.addActorFromDataBase(actor);
            for (Movie movie : DataBase.getMoviesList())
                MainSystem.addMovieFromDataBase(movie);
            for (User user : DataBase.getUsersList())
                MainSystem.addUserFromDataBase(user);
            for (Comment comment : DataBase.getCommentsList())
                MainSystem.addCommentFromDataBase(comment);
        } catch (IOException exception) {
            Server.printOutput(new Output(false, "InvalidCommand"));
        } catch (ErrorHandling exception) {
            Server.printOutput(new Output(false, exception.getMessage()));
        }
    }

    public void startServer() throws Exception {
        app.get("/movies", new MainSystem.watchMoviesListHandler());
        app.get("/movies/:movie_id", new MainSystem.watchMoviePageHandler());
        app.post("/movies/:movie_id", new MainSystem.watchMoviePageNotLoginHandler());
        app.get("/movies/login/:movie_id/:user_id", new MainSystem.watchMoviePageLoginHandler());
        app.post("/movies/login/:movie_id/:user_id", new MainSystem.watchMoviePageLoginPostHandler());
        app.get("/actors/:actor_id", new MainSystem.watchActorPageHandler());
        app.get("/watchList/:user_id", new MainSystem.showUserWatchListHandler());
        app.post("/watchList/:user_id", new MainSystem.removeFromUserWatchListHandler());
        app.get("/watchList/:user_id/:movie_id", new MainSystem.addToUserWatchListHandler());
        app.get("/rateMovie/:user_id/:movie_id/:rate", new MainSystem.rateMovieHandler());
        app.get("/voteComment/:user_id/:comment_id/:vote", new MainSystem.voteCommentHandler());
        app.get("/movies/search/:genre", new MainSystem.searchMovieByGenreHandler());
        app.get("/movies/search/:start_year/:end_year", new MainSystem.searchMovieByReleaseDateHandler());
        app.get("/200", new MainSystem.page200());
        app.get("/403", new MainSystem.page403());
        app.get("/404", new MainSystem.page404());
        app.exception(Exception.class, (e, context) -> context.result(e.getMessage()));
    }

    public static void printOutput(Output output) throws JsonProcessingException {
        String print = mapper.writeValueAsString(output);
        print = print.replace("\\", "");
        System.out.println(print);
    }

}
