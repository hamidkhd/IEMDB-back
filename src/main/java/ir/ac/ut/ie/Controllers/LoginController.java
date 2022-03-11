package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.UserManager;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userEmail = request.getParameter("email");
        String redirect;
        if (StringUtils.isBlank(userEmail))
            redirect = "/login.jsp";
        else {
            try {
                DataBase.getInstance().userNotFound(userEmail);
                UserManager.getInstance().setCurrentUser(userEmail);
                redirect = "/index.jsp";
            } catch (Exception exception) {
                request.setAttribute("errorMessage", exception.getMessage());
                redirect = "/error.jsp";
            }
        }
        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(redirect);
        requestDispatcher.forward(request, response);
    }
}
