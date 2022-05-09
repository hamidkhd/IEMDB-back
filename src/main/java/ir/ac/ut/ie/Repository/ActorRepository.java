package ir.ac.ut.ie.Repository;

import ir.ac.ut.ie.Entities.Actor;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ActorRepository extends CrudRepository<Actor, Integer> {
}
