package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.UserDAO;
import step.learning.entities.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class GetPublicUserServlet extends HttpServlet {
    @Inject
    private UserDAO userDAO;

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        String login = req.getParameter("login");

        User user = userDAO.getUser(login);
        if(user == null)
        {
            res.getWriter().write("1: no such user");
        }
        else{
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write(userDAO.getPublicUserInfo(login).toString());
        }
    }
}
