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
import step.learning.services.BodyParseService;
import step.learning.services.LoadConfigService;

//@WebServlet("/test")
@Singleton

public class AllUsersServlet extends HttpServlet {

    @Inject
    private UserDAO userDAO;

    @Inject
    private BodyParseService bodyParseService;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        res.setHeader("Access-Control-Allow-Origin","*");
        JSONObject body = bodyParseService.parseBody(req);

        if(body.isNull("token")){
            res.getWriter().write("1: access denied");
            return;
        }

        User admin = userDAO.getUserByToken(body.getString("token"));

        if(admin == null){
            res.getWriter().write("1: access denied");
            return;
        }

        List<User> users = userDAO.getAllUsers();
        JSONArray jaUsers = new JSONArray();
        for (int i = 0; i < users.size(); i++) {
            jaUsers.put(i, new JSONObject(users.get(i)));
        }

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jaUsers.toString());
    }


}
