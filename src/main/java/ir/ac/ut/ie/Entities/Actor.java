package ir.ac.ut.ie.Entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ir.ac.ut.ie.Exceptions.InvalidCommand;

import java.time.LocalDate;
import java.time.Period;
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

    public int getAge() {
        LocalDate birthDate = new java.sql.Date(this.birthDate.getTime()).toLocalDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        return age;
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
