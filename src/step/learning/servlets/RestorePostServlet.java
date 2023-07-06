package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.PostDAO;
import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class RestorePostServlet extends HttpServlet {
    @Inject
    private UserDAO userDAO;

    @Inject
    private PostDAO postDAO;

    @Inject
    BodyParseService bodyParseService;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        JSONObject body = bodyParseService.parseBody(req);

        String postId = body.getString("postId");
        String token = body.getString("token");

        User user = userDAO.getUserByToken(token);

        if(user == null || user.getRole() != 'a'){
            res.getWriter().write("access denied");
            return;
        }

        System.out.println(postId);

        if(!postDAO.restorePost(postId)){
            System.out.println("RestorePostServlet::doPost | incorrect id");
            res.sendRedirect(req.getRequestURI());
            return;
        }

        res.getWriter().write("restored successfully");
    }
}
