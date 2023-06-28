package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONException;
import org.json.JSONObject;
import step.learning.dao.PostDAO;
import step.learning.dao.TaggedPeopleDAO;
import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class DeletePostServlet extends HttpServlet {
    @Inject
    private BodyParseService bodyParseService;

    @Inject
    private PostDAO postDAO;

    @Inject
    private UserDAO userDAO;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        JSONObject body = bodyParseService.parseBody(req);
        String token = null;
        String postId = null;
        try {
            token = body.getString("token");
            postId = body.getString("postId");
        }
        catch (JSONException ex){
            res.getWriter().write("1: internal error, " + ex.getMessage());
            return;
        }

        User user = userDAO.getUserByToken(token);

        if(user == null){
            res.getWriter().write("2: access denied");
            return;
        }

        Boolean has = postDAO.authorHasPost(postId, user.getLogin());

        if(has == null){
            res.getWriter().write("1: internal error");
            return;
        }
        else if (has || user.getRole() == 'a'){
            if(postDAO.deletePostById(postId))
                res.getWriter().write("0: successfully deleted");
            else
                res.getWriter().write("1: internal error");
            return;
        }

        res.getWriter().write("2: access denied");
    }
}
