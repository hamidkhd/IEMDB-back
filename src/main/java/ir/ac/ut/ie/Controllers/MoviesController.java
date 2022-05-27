package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.Movie;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")

public class MoviesController {

    @RequestMapping(value = "/getMovies", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)

    public List<Movie> getMovies(
            @RequestParam(value = "defaultSort") String defaultSort,
            @RequestParam(value = "searchBy", required = false) String searchBy,
            @RequestParam(value = "searchValue", required = false) String searchValue) throws IOException {

        if(searchValue == null)
            searchValue ="";
        if(searchBy == null)
            searchBy ="";

        List<Movie> movies = DataBase.getInstance().moviesToShow(Boolean.parseBoolean(defaultSort), searchBy, searchValue);
        return movies;
    }
}


