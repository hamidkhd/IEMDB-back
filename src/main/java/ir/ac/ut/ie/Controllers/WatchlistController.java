package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.Entities.Movie;
import ir.ac.ut.ie.Entities.User;
import ir.ac.ut.ie.Exceptions.AgeLimitError;
import ir.ac.ut.ie.Exceptions.MovieAlreadyExists;
import ir.ac.ut.ie.Repository.MovieRepository;
import ir.ac.ut.ie.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.*;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WatchlistController extends HttpServlet {
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    UserRepository userRepository;

    private Movie[] getWatchlist(String userId) throws Exception {
        Set<Integer> movieIds = userRepository.findUserByEmail(userId).getWatchList();
        return movieRepository.findAllByIdIn(movieIds).toArray(new Movie[0]);
    }

    @RequestMapping(value = "/getWatchlist/{userId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie[] getUser(@PathVariable(value = "userId") String userId) throws Exception {
        return getWatchlist(userId);
    }

    @RequestMapping(value = "/addToWatchlist/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public String addToWatchlist(
            @PathVariable(value = "userId") String userId,
            @RequestParam(value = "movieId") Integer movieId,
            @RequestParam(value = "ageLimit") Integer ageLimit) throws InterruptedException, IOException{
        try {
            User user = userRepository.findUserByEmail(userId);
            user.addToWatchList(movieId, ageLimit);
            userRepository.save(user);
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
        userRepository.findUserByEmail(userId).getWatchList().remove(movieId);
        userRepository.save(userRepository.findUserByEmail(userId));
        return getWatchlist(userId);
    }

    @RequestMapping(value = "/getRecommendedMovies/{userId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie[] getRecommendedMovies(@PathVariable(value = "userId") String userId) throws Exception {
        List <Integer> recommended_movies = new ArrayList<>();
        List <Integer> movieId_byScore = new ArrayList<>();
        List <Integer> scores = new ArrayList<>();
        User current_user =  userRepository.findUserByEmail(userId);
        for (Movie movie : movieRepository.findAll()) {
            int genre_similarity_score = 0;
            for (Integer movieId_in_WatchList : current_user.getWatchList()) {
                Movie movie_in_WatchList = movieRepository.findMovieById(movieId_in_WatchList);
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
            finalList[i] = movieRepository.findMovieById(recommended_movies.get(i));
        return finalList;
    }
}


