package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONArray;
import org.json.JSONObject;
import step.learning.dao.PostDAO;
import step.learning.dao.UserDAO;
import step.learning.entities.Post;
import step.learning.entities.User;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class AllPostsServlet extends HttpServlet {
    @Inject
    private BodyParseService bodyParseService;

    @Inject
    private PostDAO postDAO;

    @Inject
    private UserDAO userDAO;

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

        List<Post> posts = postDAO.getAllPosts();
        JSONArray jaUsers = new JSONArray();
        for (int i = 0; i < posts.size(); i++) {
            jaUsers.put(i, new JSONObject(posts.get(i)));
        }

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jaUsers.toString());
    }
}
