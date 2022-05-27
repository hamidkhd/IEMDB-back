package ir.ac.ut.ie.Repository;

import ir.ac.ut.ie.Entities.Actor;

import ir.ac.ut.ie.Entities.Movie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ActorRepository extends CrudRepository<Actor, Integer> {
    Actor findActorsById(int id);
    List<Actor> findAllByMoviesPlayed(Movie movie);
}
