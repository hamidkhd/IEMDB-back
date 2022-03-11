package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.UserManager;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("")
public class HomeController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (UserManager.getInstance().getCurrentUser() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            requestDispatcher.forward(request, response);
        }
    }
}
