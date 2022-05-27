package ir.ac.ut.ie.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.ac.ut.ie.DataBase;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")

public class OAuthController {
    private final String clientId = "90f5cc2a63133b5dac81";
    private final String clientSecret = "b44c60256483271cb825a327a09d7dd2161de579";

    @RequestMapping(value = "/callback", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String callback(@RequestParam(value = "code") String code) throws Exception {
        String accessTokenURL = String.format(
                "https://github.com/login/oauth/access_token?client_id=%s&client_secret=%s&code=%s",
                clientId, clientSecret, code
        );
        HttpClient client = HttpClient.newHttpClient();
        URI accessTokenURI = URI.create(accessTokenURL);
        HttpRequest.Builder accessTokenBuilder = HttpRequest.newBuilder().uri(accessTokenURI);
        HttpRequest accessTokenRequest = accessTokenBuilder.POST(HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json").build();
        HttpResponse<String> accessTokenResult = client.send(accessTokenRequest, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> resultBody = mapper.readValue(accessTokenResult.body(), HashMap.class);
        String accessToken = (String) resultBody.get("access_token");

        ///////////////////////////////////////////////////////////////////////////////////
        URI userDateURI = URI.create("https://api.github.com/user");
        HttpRequest.Builder userDataBuilder = HttpRequest.newBuilder().uri(userDateURI);
        HttpRequest req = userDataBuilder.GET().header("Authorization", String.format("token %s", accessToken)).build();
        HttpResponse<String> userDataResult = client.send(req, HttpResponse.BodyHandlers.ofString());

        ///////////////////////////////////////////////////////////////////////////////////

        HashMap<String, Object> userData = mapper.readValue(userDataResult.body(), HashMap.class);

        String email = (String) userData.get("email");
        String name = (String) userData.get("name");
        String nickname = (String) userData.get("login");
        String birth_date = (String) userData.get("created_at");

        DataBase.getInstance().addUserWithGithub(email, null, name, nickname, birth_date);

        return userDataResult.body();
    }
}