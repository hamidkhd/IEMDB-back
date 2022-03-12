package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.UserManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;


@WebServlet("/watchlist")
public class WatchlistController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (UserManager.getInstance().getCurrentUser() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
        }
        else {
            String redirect;
            try {
                redirect = "/watchlist.jsp";
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

        try {
            if (Objects.equals(action, "remove")) {
                String user_id = request.getParameter("user_id");
                Integer movie_id = Integer.valueOf(request.getParameter("movie_id"));
                DataBase.getInstance().removeFromWatchList(user_id, movie_id);
                redirect = "/watchlist.jsp";
            }
        } catch (Exception exception) {
            request.setAttribute("errorMessage", exception.getMessage());
            redirect = "/error.jsp";
        }

        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(redirect);
        requestDispatcher.forward(request, response);
    }
}


