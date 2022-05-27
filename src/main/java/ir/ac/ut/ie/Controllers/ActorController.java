package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.Actor;
import ir.ac.ut.ie.Entities.Movie;
import ir.ac.ut.ie.Repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ActorController {
    @Autowired
    ActorRepository actorRepository;

    @RequestMapping(value = "/getActorMovies/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Movie> getActorMovies(@PathVariable(value = "id") Integer id) {
        return DataBase.getInstance().getActorMoviesPlayed(id);
    }

    @RequestMapping(value = "/getActor/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Actor getActor(@PathVariable(value = "id") Integer id) {
        return actorRepository.findActorsById(id);
    }
}
