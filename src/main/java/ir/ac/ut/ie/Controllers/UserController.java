package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    @RequestMapping(value = "/getUser", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password) {
        return DataBase.getInstance().getAuthenticatedUser(username, password);
    }
}
