package ir.ac.ut.ie.Repository;

import ir.ac.ut.ie.Entities.Test;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends CrudRepository<Test, Integer> {
}
