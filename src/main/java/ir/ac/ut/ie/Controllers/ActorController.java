package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.Actor;
import ir.ac.ut.ie.Entities.Movie;
import ir.ac.ut.ie.UserManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


@RestController
public class ActorController {
    @RequestMapping(value = "/getActorMovies/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie[] getActorMovies(@PathVariable(value = "id") String id) throws Exception {
        TimeUnit.SECONDS.sleep(3);
        return DataBase.getInstance().getActorMoviesPlayed(Integer.parseInt(id)).toArray(new Movie[0]);
    }

    @RequestMapping(value = "/getActor/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Actor getActor(@PathVariable(value = "id") String id) throws Exception {
        TimeUnit.SECONDS.sleep(3);
        return DataBase.getInstance().getActorById(Integer.parseInt(id));
    }
}
