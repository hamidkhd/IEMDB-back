package ir.ac.ut.ie.Entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ir.ac.ut.ie.Exceptions.InvalidCommand;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class Actor {
    private Integer id;
    private String name;
    private String birthDate;
    private String nationality;
    private String image;
    private ArrayList<Movie> moviesPlayed;

    public void update(Actor updatedActor) {
        name = updatedActor.getName();
        birthDate = updatedActor.getBirthDate();
        nationality = updatedActor.getNationality();
    }

    public ObjectNode getInformation(ObjectMapper mapper) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("actorId", id);
        objectNode.put("name", name);
        return objectNode;
    }

    public void checkForInvalidCommand() throws InvalidCommand {
        if (id==null || name==null || birthDate==null || nationality==null)
            throw new InvalidCommand();
    }

    public int getAge() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        Date date = formatter.parse(this.birthDate);
        LocalDate birthDate = new java.sql.Date(date.getTime()).toLocalDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        return age;
    }

    public void setMoviesPlayed(ArrayList<Movie> moviesPlayed) {
        this.moviesPlayed = new ArrayList<>();
        this.moviesPlayed = moviesPlayed;
    }

    public ArrayList<Movie> getMoviesPlayed() {
        return moviesPlayed;
    }

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getBirthDate(){
        return birthDate;
    }
    public String getNationality() {
        return nationality;
    }
    public String getImage() {
        return image;
    }
}
