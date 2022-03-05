package ir.ut.ac.ie;

import static org.junit.jupiter.api.Assertions.*;

import io.javalin.Javalin;
import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.Movie;
import ir.ac.ut.ie.Entities.User;
import ir.ac.ut.ie.MainSystem;
import ir.ac.ut.ie.Server;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;


public class ServerTest {
    private static Server server ;
    private DataBase dataBase;
    private HttpClient client;

    @BeforeAll
    public static void createServer() {
        server = new Server(8080);
        server.startServer();
    }

    @BeforeEach
    public void setup() throws Exception {
        dataBase = new DataBase();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void teardown() {
        dataBase = null;
        client = null;
    }

    private String getUserId(boolean valid) {
        if (!valid)
            return "gmail.com";
        return DataBase.getUsers().entrySet().iterator().next().getKey();
    }

    private int getMovieId() {
        return DataBase.getMovies().entrySet().iterator().next().getKey();
    }

    @Test
    public void whenRateMovieWithValidParametersSuccessResponseReturns() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/rateMovie/" + getUserId(true) + "/" + getMovieId() + "/1"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("/200", response.headers().map().get("Location").get(0));
    }

    @Test
    public void rateMovieInvalidUserCausesError() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/rateMovie/" + getUserId(false) + "/" + getMovieId() + "/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("/404", response.headers().map().get("Location").get(0));
    }

    @Test
    public void whenRateMovieWithValidParametersThenRateIsUpdated() throws Exception {
        Movie movie = DataBase.getMovies().get(getMovieId());
        int prevRateSum = (int) movie.getRating() * movie.getRatingCount();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/rateMovie/" + getUserId(true) + "/" + getMovieId() + "/1"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int newRateSum = (int) movie.getRating() * movie.getRatingCount();
        assertEquals(1, newRateSum - prevRateSum);
    }

    @Test
    public void whenUserRatesMovieTwiceThenRateIsReplaced() throws Exception {
        Movie movie = DataBase.getMovies().get(getMovieId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/rateMovie/" + getUserId(true) + "/" + getMovieId() + "/1"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int firstRateSum = (int) movie.getRating() * movie.getRatingCount();
        request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/rateMovie/" + getUserId(true) + "/" + getMovieId() + "/-1"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int secondRateSum = (int) movie.getRating() * movie.getRatingCount();
        assertEquals(0, firstRateSum - secondRateSum);
    }

    private HttpResponse<String> addMovieToWatchList() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/watchList/" + getUserId(true) + "/" + getMovieId()))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    @Test
    public void WhenMovieIsAddedToWatchListThenSuccessResponseReturns() throws Exception {
        HttpResponse<String> response = addMovieToWatchList();
        assertEquals("/200", response.headers().map().get("Location").get(0));
    }

    @Test
    public void whenMovieMatchesAgeThenIsAddedToWatchList() throws Exception {
        String userId = getUserId(true);
        HttpResponse<String> response = addMovieToWatchList();
        assertTrue(DataBase.getUsers().get(userId).getWatchList().contains(getMovieId()));
    }

    @Test
    public void whenAgeLimitErrorThenAddToWatchListCausesAccessError() throws Exception {
        String userId = "saman@ut.ac.ir";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/watchList/" + userId + "/" + getMovieId()))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("/403", response.headers().map().get("Location").get(0));
    }

    @Test
    public void movieDeletesFromWatchListSuccessfully() throws Exception {
        String userId = getUserId(true);
        addMovieToWatchList();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/watchList/" + userId))
                .POST(HttpRequest.BodyPublishers.ofString("movie_id=" + getMovieId()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertFalse(DataBase.getUsers().get(userId).getWatchList().contains(getMovieId()));
    }

    @Test
    public void whenThereIsNoTimeLimitThenSearchByRealeseDateReturnsAllMovies() throws Exception {
        LocalDate date = LocalDate.now();
        assertEquals(DataBase.getMovies().size(), MainSystem.getMoviesByDate("1800-01-01", date.toString()).size());
    }

    @Test
    public void specificMovieIsFoundWhenSearchByDate() throws Exception {
        boolean found = false;
        for (Movie movie : MainSystem.getMoviesByDate("1999-11-10", "1999-11-12"))
            if (movie.getId() == 1)
                found = true;
        assertTrue(found);
    }

    @Test
    public void WhenReleaseDateNotInPeriodThenMovieNotFound() throws Exception {
        boolean found = false;
        for (Movie movie : MainSystem.getMoviesByDate("1999-11-09", "1999-11-10"))
            if (movie.getId() == 1)
                found = true;
        assertFalse(found);
    }



}
