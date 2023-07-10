package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.PostDAO;
import step.learning.dao.UserDAO;
import step.learning.entities.Comment;
import step.learning.entities.Post;
import step.learning.entities.User;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class ChangePostServlet extends HttpServlet {
    @Inject
    private BodyParseService bodyParseService;

    @Inject
    private PostDAO postDAO;

    @Inject
    private UserDAO userDAO;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        JSONObject body = bodyParseService.parseBody(req);
        String token = body.getString("token");
        Post post = null;
        try {
            post = new Post(body.getJSONObject("post"));
        } catch (Exception ex) {
            res.getWriter().write("1: internal error, " + ex.getMessage());
            return;
        }

        User user = userDAO.getUserByToken(token);
        if(user == null){
            res.getWriter().write("2: access denied");
            return;
        }

        Boolean has = postDAO.authorHasPost(post.getId(), user.getLogin());

        if(has == null){
            res.getWriter().write("1: internal error");
            return;
        }
        else if (!has){
            res.getWriter().write("2: access denied");
            return;
        }

        postDAO.update(post, post.getId());
        res.getWriter().write("0: successfully changed");
    }
}
