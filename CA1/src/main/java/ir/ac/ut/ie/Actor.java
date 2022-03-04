package ir.ac.ut.ie;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

    public ObjectNode getInformation(ObjectMapper mapper) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("actorId", id);
        objectNode.put("name", name);
        return objectNode;
    }

    public boolean checkForCommand() {
        if (id==null || name==null || birthDate==null || nationality==null)
            return false;
        else
            return true;
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
