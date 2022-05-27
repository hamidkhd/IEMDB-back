package ir.ac.ut.ie.Repository;

import ir.ac.ut.ie.Entities.Actor;
import ir.ac.ut.ie.Entities.Movie;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Integer> {
    Movie findMovieById(int id);
    List<Movie> findAllByOrderByReleaseDateDesc();
    List<Movie> findAllByNameContains(String searchValue);
    List<Movie> findAllByGenresContains(String searchValue);
    List<Movie> findAllByReleaseDateAfter(String searchValue);
    List<Movie> findAllByOrderByImdbRateDesc();
    List<Movie> findAllByCast(int cast);
    List<Movie> findAllByIdIn(Set<Integer> movieIds);
}
