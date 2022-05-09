package ir.ac.ut.ie;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import ir.ac.ut.ie.Entities.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class IEMDBApplication {

    public static void main(String[] args) {
        SpringApplication.run(IEMDBApplication.class, args);
    }

}
