package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONObject;
import step.learning.dao.LikesDAO;
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

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        JSONObject body = bodyParseService.parseBody(req);

        String postId = body.getString("postId");
        String login = body.getString("login");
        boolean isLiked = body.getBoolean("isLiked");

        if(isLiked){
            if (likedDAO.putLike(postId, login))
                res.getWriter().write("0: Liked successfully");
            else
                res.getWriter().write("1: internal error");
        }
        else{
            if (likedDAO.unLike(postId, login))
                res.getWriter().write("0: unliked successfully");
            else
                res.getWriter().write("1: internal error");
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String postId = req.getParameter("postId");
        String login = req.getParameter("login");

        JSONObject answer = new JSONObject();
        answer.put("isLiked", likedDAO.isLikedByUser(postId, login));
        answer.put("amountLikes", likedDAO.getLikesCount(postId));

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(answer.toString());
    }
}
