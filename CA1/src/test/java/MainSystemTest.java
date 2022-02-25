import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ir.ac.ut.ie.*;
import org.junit.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainSystemTest {
    private MainSystem mainSystem;
    private ObjectMapper mapper;
    int movieId;

    @Before
    public void setup() throws IOException {
        mainSystem = new MainSystem();
        mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.setDateFormat(df);

        mainSystem.addUser("{\"email\": \"hamid@gmail.com\", \"password\": \"1234\", \"nickname\": \"hamid\"" +
                ", \"name\": \"hamid\", \"birthDate\": \"2000-01-01\"}");
        mainSystem.addActor("{\"id\": \"1\", \"name\": \"Edward Norton\", \"birthDate\": \"1969-8-18\", " +
                "\"nationality\": \"American\"}");

        mainSystem.addMovie("{\"id\": \"1\", \"name\": \"Fight Club\", \"summary\": \"text\", \"releaseDate\":" +
                "\"1999-11-11\", \"director\": \"David Fincher\", \"writers\":[\"Chuck Palahniuk\", \"Jim Uhls\"]," +
                " \"genres\": [\"Drama\"], \"cast\": [1], \"imdbRate\": 8.8, \"duration\": \"139\", \"ageLimit\": 16}");

        mainSystem.addMovie("{\"id\": \"2\", \"name\": \"The Prestige\", \"summary\": \"text\", \"releaseDate\":" +
                "\"2006-10-20\", \"director\": \"Christopher Nolan\", \"writers\":[\"Christopher Nolan\", " +
                "\"Jonathan Nolan\", \"Christopher priest\"]," +
                " \"genres\": [\"Mystery\", \"Thriller\"], \"cast\": [1], \"imdbRate\": 8.5, \"duration\": \"130\", \"ageLimit\": 13}");


        mainSystem.addComment("{\"userEmail\": \"hamid@gmail.com\", \"movieId\": 1, \"text\": \"good\"}\n}");
        mainSystem.rateMovie("{\"userEmail\": \"hamid@gmail.com\", \"movieId\": \"1\", \"score\": \"9\"}");

    }

    @After
    public void tearDown() {
        mainSystem = null;
        mapper = null;
    }

    @Test
    public void rateTheMovie_UserNotFound() throws IOException {
        String data = "{\"userEmail\": \"nazanin@gmail.com\", \"movieId\": \"3\", \"score\": \"3\"}";
        Rate rate = mapper.readValue(data, Rate.class);
        assertFalse(mainSystem.getUsers().containsKey(rate.getUserEmail()));
    }

    @Test
    public void rateTheMovie_MovieNotFound() throws IOException {
        String data = "{\"userEmail\": \"nazanin@gmail.com\", \"movieId\": \"3\", \"score\": \"9\"}";
        Rate rate = mapper.readValue(data, Rate.class);
        assertFalse(mainSystem.getMovies().containsKey(rate.getMovieId()));
    }

    @Test
    public void rateTheMovie_ChangeMovieScoreCorrectly() throws IOException {
        String data = "{\"userEmail\": \"hamid@gmail.com\", \"movieId\": \"1\", \"score\": \"9\"}";
        Rate rate = mapper.readValue(data, Rate.class);

        float new_rate = (mainSystem.getMovies().get(rate.getMovieId()).getRating() *
                mainSystem.getMovies().get(rate.getMovieId()).getRatingCount()
                + rate.getScore()) / (mainSystem.getMovies().get(rate.getMovieId()).getRatingCount() + 1);

        mainSystem.rateMovie(data);
        assertEquals(((Object) new_rate), mainSystem.getMovies().get(rate.getMovieId()).getRating());
    }

    @Test
    public void rateTheMovie_UpdateScore() throws IOException {
        String data = "{\"userEmail\": \"hamid@gmail.com\", \"movieId\": \"1\", \"score\": \"7\"}";
        Rate rate = mapper.readValue(data, Rate.class);
        mainSystem.rateMovie(data);
        assertEquals("7", mainSystem.getMovies().get(rate.getMovieId()).getRates().
                get(rate.getUserEmail()).toString());
    }

    @Test
    public void rateTheMovie_InvalidRateScore() throws IOException {
        String data = "{\"userEmail\": \"hamid@gmail.com\", \"movieId\": \"1\", \"score\": \"11\"}";
        Rate rate = mapper.readValue(data, Rate.class);
        assertTrue(rate.hasError());
    }

    @Test
    public void voteTheComment_UserNotFound() throws IOException {
        String data = "{\"userEmail\": \"nazanin@gmail.com\", \"commentId\": \"1\", \"vote\": \"1\"}";
        Vote vote = mapper.readValue(data, Vote.class);
        assertFalse(mainSystem.getUsers().containsKey(vote.getUserEmail()));
    }

    @Test
    public void voteTheComment_CommentNotFound() throws IOException {
        String data = "{\"userEmail\": \"hamid@gmail.com\", \"commentId\": \"2\", \"vote\": \"1\"}";
        Vote vote = mapper.readValue(data, Vote.class);
        assertFalse(mainSystem.getComments().containsKey(vote.getCommentId()));
    }

    @Test
    public void voteTheComment_InvalidVoteValue() throws IOException {
        String data = "{\"userEmail\": \"hamid@gmail.com\", \"commentId\": \"2\", \"vote\": \"2\"}";
        Vote vote = mapper.readValue(data, Vote.class);
        assertTrue(vote.hasError());
    }

    @Test
    public void voteTheComment_SetVoteCorrectly() throws IOException {
        String data = "{\"userEmail\": \"hamid@gmail.com\", \"commentId\": \"1\", \"vote\": \"1\"}";
        Vote vote = mapper.readValue(data, Vote.class);
        Integer new_vote = mainSystem.getComments().get(vote.getCommentId()).getLike()
                + mainSystem.getComments().get(vote.getCommentId()).getDislike() + vote.getVote();
        mainSystem.voteComment(data);
        assertEquals(((Object) new_vote), mainSystem.getComments().get(vote.getCommentId()).getLike()
            + mainSystem.getComments().get(vote.getCommentId()).getDislike());
    }

    @Test
    public void voteTheComment_UpdateVote() throws IOException {
        String data = "{\"userEmail\": \"hamid@gmail.com\", \"commentId\": \"1\", \"vote\": \"1\"}";
        Vote vote = mapper.readValue(data, Vote.class);
        mainSystem.voteComment(data);
        assertEquals("1", mainSystem.getComments().get(vote.getCommentId()).getVotes().
                get(vote.getUserEmail()).toString());
    }

    @Test
    public void addToWatchList_UserNotFound() throws IOException {
        String data = "{\"userEmail\": \"nazanin@gmail.com\", \"movieId\": \"1\"}";
        JsonNode jsonNode = mapper.readTree(data);
        String userEmail = jsonNode.get("userEmail").asText();

        assertFalse(mainSystem.getUsers().containsKey(userEmail));
    }

    @Test
    public void addToWatchList_MovieNotFound() throws IOException {
        String data = "{\"userEmail\": \"hamid@gmail.com\", \"movieId\": \"2\"}";
        JsonNode jsonNode = mapper.readTree(data);
        Integer movieId = jsonNode.get("movieId").asInt();

        assertFalse(mainSystem.getMovies().containsKey(movieId));
    }

    @Test
    public void addToWatchList_UserAlreadyExists() throws IOException {
        String data = "{\"userEmail\": \"hamid@gmail.com\", \"movieId\": \"1\"}";
        JsonNode jsonNode = mapper.readTree(data);
        String userEmail = jsonNode.get("userEmail").asText();
        Integer movieId = jsonNode.get("movieId").asInt();
        mainSystem.addToWatchList(userEmail, movieId);

        assertTrue(mainSystem.getUsers().get(userEmail).movieAlreadyExists(movieId));
    }

    @Test
    public void addToWatchList_AgeLimitError() throws IOException {
        String data = "{\"userEmail\": \"hamid@gmail.com\", \"movieId\": \"1\"}";
        JsonNode jsonNode = mapper.readTree(data);
        String userEmail = jsonNode.get("userEmail").asText();
        Integer movieId = jsonNode.get("movieId").asInt();

        int ageLimit = mainSystem.getMovies().get(movieId).getAgeLimit();

        assertFalse(mainSystem.getUsers().get(userEmail).ageLimitError(ageLimit));
    }

    @Test
    public void addToWatchList_Correctly() throws IOException {
        String data = "{\"userEmail\": \"hamid@gmail.com\", \"movieId\": \"1\"}";
        JsonNode jsonNode = mapper.readTree(data);
        String userEmail = jsonNode.get("userEmail").asText();
        Integer movieId = jsonNode.get("movieId").asInt();
        Integer watchlist_size_before_adding = mainSystem.getUsers().get(userEmail).getWatchList().size();
        mainSystem.addToWatchList(userEmail, movieId);
        Integer watchList_size_after_adding = mainSystem.getUsers().get(userEmail).getWatchList().size();

       assertEquals((Object) (watchlist_size_before_adding+1), watchList_size_after_adding);
    }

    public void addMovieWithSpecificGenre(String genre) throws IOException {
        List<String> genres = new ArrayList<>();
        genres.add(genre);
        Movie actionMovie = spy(Movie.class);
        when(actionMovie.getId()).thenReturn(movieId);
        when(actionMovie.getGenres()).thenReturn(genres);
        mainSystem.addMovie(actionMovie);
    }

    @Test
    public void getMoviesByGenre_MovieFound() throws IOException {
        addMovieWithSpecificGenre("action");
        String data = "{\"genre\": \"action\"}";
        mainSystem.getMoviesByGenre(data);
        verify(mainSystem.getMovies().get(movieId)).createInformationJson(any(ObjectMapper.class), any(ObjectNode.class));
    }

    @Test
    public void getMoviesByGenre_MovieNotFound() throws IOException {
        addMovieWithSpecificGenre("action");
        assertFalse(mainSystem.getMovies().get(movieId).genreMatch("Mystery"));
    }
}
