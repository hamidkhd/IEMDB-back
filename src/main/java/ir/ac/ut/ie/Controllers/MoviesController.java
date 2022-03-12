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
            RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/movies.jsp");
            requestDispatcher.forward(request, response);
        }
    }
}