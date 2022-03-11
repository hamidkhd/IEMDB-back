package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.Comment;
import ir.ac.ut.ie.Entities.Rate;
import ir.ac.ut.ie.Entities.Vote;
import ir.ac.ut.ie.UserManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/movies/*")
public class MovieController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (UserManager.getInstance().getCurrentUser() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
        }
        else {
            String redirect;
            try {
                int movie_id = Integer.parseInt(request.getPathInfo().substring(1));
                DataBase.getInstance().movieNotFound(movie_id);
                request.setAttribute("movie_id", movie_id);
                redirect = "/movie.jsp";
            } catch (Exception exception) {
                request.setAttribute("errorMessage", exception.getMessage());
                redirect = "/error.jsp";
            }
            RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(redirect);
            requestDispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String redirect = null;
        switch (action){
            case "rate":
                try {
                    String user_id = UserManager.getInstance().getCurrentUser();
                    Integer movie_id = Integer.valueOf(request.getParameter("movie_id"));
                    request.setAttribute("movie_id", movie_id);
                    String rate = request.getParameter("rate_value");
                    DataBase.getInstance().rateMovie(new Rate(user_id, movie_id, Float.parseFloat(rate)));
                    redirect = "/movie.jsp";
                } catch (Exception exception){
                    request.setAttribute("errorMessage", exception.getMessage());
                    redirect = "/error.jsp";
                }
                break;
            case "add":
                try {
                    String user_id = UserManager.getInstance().getCurrentUser();
                    Integer movie_id = Integer.valueOf(request.getParameter("movie_id"));
                    request.setAttribute("movie_id", movie_id);
                    DataBase.getInstance().addToWatchList(user_id, movie_id);
                    redirect = "/movie.jsp";
                } catch (Exception exception){
                    request.setAttribute("errorMessage", exception.getMessage());
                    redirect = "/error.jsp";
                }
                break;
            case "like":
                try {
                    String user_id = UserManager.getInstance().getCurrentUser();
                    Integer comment_id = Integer.valueOf(request.getParameter("like_id"));
                    Integer movie_id = Integer.valueOf(request.getParameter("movie_id"));
                    request.setAttribute("movie_id", movie_id);
                    DataBase.getInstance().voteComment(new Vote(user_id, comment_id, 1));
                    redirect = "/movie.jsp";
                } catch (Exception exception){
                    request.setAttribute("errorMessage", exception.getMessage());
                    redirect = "/error.jsp";
                }
                break;
            case "dislike":
                try {
                    String user_id = UserManager.getInstance().getCurrentUser();
                    Integer comment_id = Integer.valueOf(request.getParameter("dislike_id"));
                    Integer movie_id = Integer.valueOf(request.getParameter("movie_id"));
                    request.setAttribute("movie_id", movie_id);
                    DataBase.getInstance().voteComment(new Vote(user_id, comment_id, -1));
                    redirect = "/movie.jsp";
                } catch (Exception exception){
                    request.setAttribute("errorMessage", exception.getMessage());
                    redirect = "/error.jsp";
                }
                break;
            case "comment":
                try {
                    String user_id = UserManager.getInstance().getCurrentUser();
                    Integer movie_id = Integer.valueOf(request.getParameter("movie_id"));
                    request.setAttribute("movie_id", movie_id);
                    String text = request.getParameter("text");
                    DataBase.getInstance().addComment(user_id, movie_id, text);
                    redirect = "/movie.jsp";
                } catch (Exception exception){
                    request.setAttribute("errorMessage", exception.getMessage());
                    redirect = "/error.jsp";
                }
                break;
        }

        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(redirect);
        requestDispatcher.forward(request, response);
    }
}
