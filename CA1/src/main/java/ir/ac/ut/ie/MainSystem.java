package ir.ac.ut.ie;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainSystem {
    private ObjectMapper mapper;
    Map<Integer, Actor> existingActors;


    public MainSystem() {
        mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.setDateFormat(df);
        existingActors = new HashMap<>();
    }

    public void addActor(String data) throws IOException {
        Actor actor = mapper.readValue(data, Actor.class);
        if (existingActors.containsKey(actor.getId()))
            existingActors.get(actor.getId()).update(actor);
        else
            existingActors.put(actor.getId(), actor);
//        Calendar c = Calendar.getInstance();
//        c.setTime(actor.getBirthDate());
//        System.out.println(c.get(Calendar.MONTH + 1));
    }
}
