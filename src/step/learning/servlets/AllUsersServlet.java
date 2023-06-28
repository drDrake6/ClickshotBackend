package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.services.LoadConfigService;

//@WebServlet("/test")
@Singleton

public class AllUsersServlet extends HttpServlet {

    @Inject
    private UserDAO userDAO;

    @Inject
    private LoadConfigService loadConfigService;

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String s = req.getContextPath();
        List<User> users = userDAO.getAllUsers();
        JSONArray jaUsers = new JSONArray();
        for (int i = 0; i < users.size(); i++) {
            jaUsers.put(i, new JSONObject(users.get(i)));
        }

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin","*");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jaUsers.toString());
    }


}
