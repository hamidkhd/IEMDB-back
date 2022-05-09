package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.Movie;
import ir.ac.ut.ie.Entities.Test;
import ir.ac.ut.ie.Repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
public class MoviesController {
    @Autowired
    DataBase dataBase;
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
        Map<Integer, Movie> moviesMap = DataBase.getInstance().moviesToShow(Boolean.parseBoolean(defaultSort), searchBy, searchValue);
        Movie[] movies = moviesMap.values().toArray(new Movie[0]);
        System.out.println("happend");
        return movies;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void testing() throws IOException {
        Test test = new Test(377, "hahah");
//        dataBase.testing(test);
        System.out.println("hiii");

    }
}


