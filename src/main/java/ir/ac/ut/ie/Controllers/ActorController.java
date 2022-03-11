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


@WebServlet("/actors/*")
public class ActorController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (UserManager.getInstance().getCurrentUser() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
        }
        else {
            String redirect;
            try {
                int actor_id = Integer.parseInt(request.getPathInfo().substring(1));
                DataBase.getInstance().actorNotFound(actor_id);
                request.setAttribute("actor_id", actor_id);
                redirect = "/actor.jsp";
            } catch (Exception exception) {
                request.setAttribute("errorMessage", exception.getMessage());
                redirect = "/error.jsp";
            }
            RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(redirect);
            requestDispatcher.forward(request, response);
        }
    }
}
