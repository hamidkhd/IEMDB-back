package ir.ac.ut.ie.Controllers;
import ir.ac.ut.ie.Entities.Actor;
import ir.ac.ut.ie.Entities.Movie;
import ir.ac.ut.ie.Entities.Rate;
import ir.ac.ut.ie.Repository.ActorRepository;
import ir.ac.ut.ie.Repository.MovieRepository;
import ir.ac.ut.ie.Repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
public class MovieController {
    @Autowired
    RateRepository rateRepository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    ActorRepository actorRepository;

    @RequestMapping(value = "/getMovie/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie getMovie(@PathVariable(value = "id") Integer id) throws Exception {
        return movieRepository.findMovieById(id);
    }

    @RequestMapping(value = "/getMovieActors/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Actor[] getMovieActors(@PathVariable(value = "id") Integer id) {;
        return actorRepository.findAllByMoviesPlayed(movieRepository.findMovieById(id)).toArray(new Actor[0]);
    }

    @RequestMapping(value = "/postRate/{movieId}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie postRate(
            @PathVariable(value = "movieId") Integer movieId,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "rate") int score) {

        Rate newRate = new Rate(userId, movieId, score);
        Movie movie = movieRepository.findMovieById(movieId);
        movie.addRate(newRate, rateRepository);
        rateRepository.save(newRate);
        movieRepository.save(movie);
        return movie;
    }
}
