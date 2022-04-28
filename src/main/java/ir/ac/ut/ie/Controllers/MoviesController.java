package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.Movie;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
public class MoviesController {

    @RequestMapping(value = "/getMovies", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie[] getMovies(
            @RequestParam(value = "defaultSort") String defaultSort,
            @RequestParam(value = "searchBy", required = false) String searchBy,
            @RequestParam(value = "searchValue", required = false) String searchValue) throws IOException, InterruptedException {

        if(searchValue == null)
            searchValue ="";
        if(searchBy == null)
            searchBy ="";
        System.out.println(searchValue);
        Map<Integer, Movie> moviesMap = DataBase.getInstance().moviesToShow(Boolean.parseBoolean(defaultSort), searchBy, searchValue);
        Movie[] movies = moviesMap.values().toArray(new Movie[0]);
        TimeUnit.SECONDS.sleep(3);
        return movies;
    }
}


