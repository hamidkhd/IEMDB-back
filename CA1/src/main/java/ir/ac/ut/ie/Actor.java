package ir.ac.ut.ie;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Actor {
    private Integer id;
    private String name;
    private Date birthDate;
    private String nationality;

    public void update(Actor updatedActor) {
        name = updatedActor.getName();
        birthDate = updatedActor.getBirthDate();
        nationality = updatedActor.getNationality();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate(){
       return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
