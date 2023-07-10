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
import java.util.ArrayList;
import java.util.List;

@Singleton
public class BanPostServlet extends HttpServlet {
    @Inject
    private PostDAO postDAO;
    @Inject
    private UserDAO userDAO;
    @Inject
    private BodyParseService bodyParseService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        JSONObject body = bodyParseService.parseBody(req);

        if(body.isNull("token")){
            res.getWriter().write("2: access denied");
            return;
        }
        else{
            User user = userDAO.getUserByToken(body.getString("token"));
            if(user == null){
                res.getWriter().write("2: access denied");
                return;
            }
            else if((user.getRole() != 'a'))
            {
                res.getWriter().write("2: access denied");
                return;
            }
        }

        String postId = body.getString("postId");
        boolean baning = Boolean.parseBoolean(body.getString("baning"));

        Boolean completed = postDAO.banPosts(postId, baning);
        if(completed != null){
            if(completed){
                res.getWriter().write("0: post modified successfully");
            }
            else{
                res.getWriter().write("1: post was not modified");
            }
        }
        else{
            res.getWriter().write("3: internal error");
        }
    }
}
