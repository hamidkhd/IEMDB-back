package ir.ac.ut.ie.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import ir.ac.ut.ie.Entities.User;
import ir.ac.ut.ie.Repository.UserRepository;
import ir.ac.ut.ie.Utilities.HashCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")

public class AuthController {
    @Autowired
    UserRepository userRepository;

    public static final String KEY = "iemdb1401";

    private String createToken(String userId) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date exp = c.getTime();

        SecretKey key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, key)
                .setHeaderParam("typ", "JWT")
                .setIssuer("hamid")
                .setIssuedAt(new Date())
                .setExpiration(exp)
                .claim("userId", userId)
                .compact();

        return jwt;
    }

    @PostMapping("login")
    public JsonNode login(@RequestBody JsonNode body) throws Exception {
        if (!body.has("email") || !body.has("password"))
            throw new Exception("The parameters are low.");

        User user = userRepository.findUserByEmail(body.get("email").asText());

        if (user == null)
            throw new Exception("There is no user with this email.");

        if (!Objects.equals(HashCreator.getInstance().getMD5Hash(body.get("password").asText()), user.getPassword()))
            throw new Exception("Wrong password entered.");

        String jwt = createToken(user.getEmail());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resp = objectMapper.createObjectNode();
        resp.put("login", jwt);
        resp.put("userId", user.getEmail());
        return resp;
    }

    @PostMapping("signup")
    public JsonNode signup(@RequestBody String body) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User newUser = objectMapper.readValue(body, User.class);

        if (userRepository.findUserByEmail(newUser.getEmail()) != null)
            throw new Exception("A user is available with this email.");

        userRepository.save(newUser);

        ObjectNode resp = objectMapper.createObjectNode();
        resp.put("success", "true");
        resp.put("message", "User added to database.");
        return resp;
    }
}
