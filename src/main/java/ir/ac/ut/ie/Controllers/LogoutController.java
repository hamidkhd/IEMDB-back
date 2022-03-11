package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.UserManager;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserManager.getInstance().setCurrentUser(null);
        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/login.jsp");
        requestDispatcher.forward(request, response);
    }
}
