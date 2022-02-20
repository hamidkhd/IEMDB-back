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
    public String getName() {
        return name;
    }
    public Date getBirthDate(){
       return birthDate;
    }
    public String getNationality() {
        return nationality;
    }
}
