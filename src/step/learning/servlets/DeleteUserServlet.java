package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.Console;
import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.services.BodyParseService;

@Singleton
public class DeleteUserServlet extends HttpServlet {
    @Inject
    private UserDAO userDAO;

    @Inject
    BodyParseService bodyParseService;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        JSONObject body = bodyParseService.parseBody(req);

        String login = body.getString("login");
        String token = body.getString("token");

        User user = userDAO.getUserByToken(token);

        if(user == null || user.getRole() != 'a'){
            res.getWriter().write("access denied");
            return;
        }

        System.out.println(login);
        User deleteUser = userDAO.getUser(login);

        if(deleteUser.getRole() == 'a'){
            res.getWriter().write("you can't delete me)");
            return;
        }

        if(!userDAO.deleteUser(login)){
            System.out.println("DeleteUserServlet::doPost | incorrect id");
            res.sendRedirect(req.getRequestURI());
            return;
        }

        res.getWriter().write("deleted successfully");
    }
}
