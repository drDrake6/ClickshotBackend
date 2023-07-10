package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.LikesDAO;
import step.learning.dao.UserDAO;
import step.learning.services.BodyParseService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class LikeServlet extends HttpServlet {
    @Inject
    private BodyParseService bodyParseService;

    @Inject
    private LikesDAO likedDAO;

    @Inject
    private UserDAO userDAO;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        JSONObject body = bodyParseService.parseBody(req);

        if(userDAO.getUserByToken(body.getString("token")) == null)
        {
            res.getWriter().write("2: access denied");
            return;
        }

        String postId = body.getString("postId");
        String login = body.getString("login");
        boolean isLiked = body.getBoolean("isLiked");


        if(isLiked){
            Boolean ok = likedDAO.putLike(postId, login);
            if(ok != null) {
                if (ok)
                    res.getWriter().write("0: Liked successfully");
                else
                    res.getWriter().write("2: you have liked this post already");
            }
            else{
                res.getWriter().write("0: unliked successfully");
            }
        }
        else{
            Boolean ok = likedDAO.unLike(postId, login);
            if(ok != null){
                if (ok)
                    res.getWriter().write("0: unliked successfully");
                else
                    res.getWriter().write("2: you have unliked this post already");
            }
            else{
                res.getWriter().write("1: internal error");
            }
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("Access-Control-Allow-Origin","*");
        String postId = req.getParameter("postId");
        String login = req.getParameter("login");

        JSONObject answer = new JSONObject();
        answer.put("isLiked", likedDAO.isLikedByUser(postId, login, false));
        answer.put("amountLikes", likedDAO.getLikesCount(postId));

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(answer.toString());
    }
}
