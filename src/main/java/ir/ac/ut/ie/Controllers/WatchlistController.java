package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.Movie;
import ir.ac.ut.ie.Entities.User;
import ir.ac.ut.ie.Exceptions.AgeLimitError;
import ir.ac.ut.ie.Exceptions.MovieAlreadyExists;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@RestController
public class WatchlistController extends HttpServlet {
    private Movie[] getWatchlist(String userId) throws Exception {
        Set<Integer> movieIds = DataBase.getInstance().getUsers().get(userId).getWatchList();
        Movie[] watchList = new Movie[movieIds.size()];
        int i=0;
        for (Integer id:movieIds) {
            watchList[i] = DataBase.getInstance().getMovieById(id);
            i++;
        }
        return watchList;
    }

    @RequestMapping(value = "/getWatchlist/{userId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie[] getUser(@PathVariable(value = "userId") String userId) throws Exception {
        TimeUnit.SECONDS.sleep(3);
        return getWatchlist(userId);
    }

    @RequestMapping(value = "/addToWatchlist/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public String addToWatchlist(
            @PathVariable(value = "userId") String userId,
            @RequestParam(value = "movieId") Integer movieId,
            @RequestParam(value = "ageLimit") Integer ageLimit) throws InterruptedException, IOException{
        TimeUnit.SECONDS.sleep(3);
        try {
            DataBase.getInstance().getUsers().get(userId).addToWatchList(movieId, ageLimit);
            return "Movie Added To Watchlist Successfully";
        } catch (MovieAlreadyExists e1) {
            return e1.getMessage();
        } catch (AgeLimitError e2) {
            return e2.getMessage();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/deleteFromWatchlist/{userId}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie[] deleteFromWatchlist(
            @PathVariable(value = "userId") String userId,
            @RequestParam(value = "movieId") Integer movieId) throws Exception {
        DataBase.getInstance().getUsers().get(userId).removeFromWatchList(movieId);
        TimeUnit.SECONDS.sleep(3);
        return getWatchlist(userId);
    }

    @RequestMapping(value = "/getRecommendedMovies/{userId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie[] getRecommendedMovies(@PathVariable(value = "userId") String userId) throws Exception {
        List <Integer> recommended_movies = new ArrayList<>();
        List <Integer> movieId_byScore = new ArrayList<>();
        List <Integer> scores = new ArrayList<>();
        User current_user = DataBase.getInstance().getUsers().get(userId);
        for (Movie movie : DataBase.getInstance().getMovies().values()) {
            int genre_similarity_score = 0;
            for (Integer movieId_in_WatchList : current_user.getWatchList()) {
                Movie movie_in_WatchList = DataBase.getInstance().getMovieById(movieId_in_WatchList);
                ArrayList <String> temp_list = new ArrayList<>(movie.getGenres());
                temp_list.retainAll(movie_in_WatchList.getGenres());
                genre_similarity_score += temp_list.size();
            }
            scores.add((int) (3 * genre_similarity_score + movie.getImdbRate() + movie.getRating()));
            movieId_byScore.add(movie.getId());
            movie.setScore((int) (3 * genre_similarity_score + movie.getImdbRate() + movie.getRating()));
        }

        while (movieId_byScore.size() != 0) {
            int max_score_index = scores.indexOf(Collections.max(scores));
            recommended_movies.add(movieId_byScore.get(max_score_index));
            scores.remove(max_score_index);
            movieId_byScore.remove((max_score_index));
        }
        Movie[] finalList = new Movie[3];
        for (int i=0; i<3; i++)
            finalList[i] = DataBase.getInstance().getMovieById(recommended_movies.get(i));
        TimeUnit.SECONDS.sleep(3);
        return finalList;
    }
}


