package ir.ac.ut.ie.Repository;

import ir.ac.ut.ie.Entities.Rate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateRepository extends CrudRepository<Rate, String> {
    Rate findRateByUserEmailAndMovieId(String userEmail, Integer movieId);
}
