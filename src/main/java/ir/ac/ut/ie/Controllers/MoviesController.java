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


@WebServlet("/movies")
public class MoviesController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (UserManager.getInstance().getCurrentUser() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            UserManager.getInstance().setSearch(false);
            UserManager.getInstance().setDefaultSort(true);
            RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/movies.jsp");
            requestDispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "search":
                UserManager.getInstance().setSearch(true);
                DataBase.getInstance().setSearchedMovies(request.getParameter("search"));
                break;
            case "clear":
                UserManager.getInstance().setSearch(false);
                break;
            case "sort_by_imdb":
                UserManager.getInstance().setDefaultSort(true);
                break;
            case "sort_by_date":
                UserManager.getInstance().setDefaultSort(false);
                break;
        }
        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/movies.jsp");
        requestDispatcher.forward(request, response);
    }

}