package ir.ac.ut.ie.Repository;

import ir.ac.ut.ie.Entities.Comment;
import ir.ac.ut.ie.Entities.Movie;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Integer> {
}
