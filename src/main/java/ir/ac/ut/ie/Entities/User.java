package ir.ac.ut.ie.Entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import ir.ac.ut.ie.Exceptions.AgeLimitError;
import ir.ac.ut.ie.Exceptions.MovieAlreadyExists;
import ir.ac.ut.ie.Exceptions.MovieNotFound;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Entity
@Table(name= "User")
public class User {
    @Id
    private String email;
    private String password;
    private String nickname;
    private String name;
    private Date birthDate;
    @ElementCollection
    private Set<Integer> watchList = new HashSet<>();

    public void addToWatchList(Integer movieId, int ageLimit) throws Exception {
        movieAlreadyExists(movieId);
        ageLimitError(ageLimit);
        watchList.add(movieId);
    }

    public void movieAlreadyExists(Integer movieId) throws JsonProcessingException, MovieAlreadyExists {
        if (watchList.contains(movieId))
            throw new MovieAlreadyExists();
    }

    public void ageLimitError(int ageLimit) throws Exception {
        LocalDate birthDate = new java.sql.Date(this.birthDate.getTime()).toLocalDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < ageLimit)
            throw new AgeLimitError();
    }

    public void removeFromWatchList(Integer movieId) throws Exception {
        if (!watchList.contains(movieId))
            throw new MovieNotFound();
        watchList.remove(movieId);
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getNickname() {
        return nickname;
    }
    public String getName() {
        return name;
    }
    public Date getBirthDate() {
        return birthDate;
    }
    public Set<Integer> getWatchList() {
        return watchList;
    }
    public void setName(String name) {
        this.name = name;
    }
}
