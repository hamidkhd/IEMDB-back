package ir.ac.ut.ie.Repository;


import ir.ac.ut.ie.Entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    User findUserByEmail(String id);
    User findUserByEmailAndPassword(String email, String password);
}
