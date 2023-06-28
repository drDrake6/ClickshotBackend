package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.CommentDAO;
import step.learning.dao.PostDAO;
import step.learning.dao.UserDAO;
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
public class DeleteCommentServlet extends HttpServlet {

    @Inject
    private BodyParseService bodyParseService;

    @Inject
    private CommentDAO commentDAO;

    @Inject
    private UserDAO userDAO;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        JSONObject body = bodyParseService.parseBody(req);

        String token = body.getString("token");
        String commentId = body.getString("commentId");
        User user = userDAO.getUserByToken(token);
        if(user == null){
            res.getWriter().write("2: access denied");
            return;
        }

        Boolean has = commentDAO.authorHasComment(commentId, user.getLogin());

        if(has == null){
            res.getWriter().write("1: internal error");
            return;
        }
        else if (has || user.getRole() == 'a'){
            List<String> deletedIds = new ArrayList<>();

            if(commentDAO.deleteCommentById(deletedIds, commentId))
            {
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < deletedIds.size(); i++) {
                    jsonObject.put(String.valueOf(i), deletedIds.get(i));
                }

                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write(jsonObject.toString());
            }

            else
                res.getWriter().write("1: internal error");

            return;
        }

        res.getWriter().write("2: access denied");
    }
}
