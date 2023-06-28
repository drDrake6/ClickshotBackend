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
        JSONObject body = bodyParseService.parseBody(req);

        if(body.isNull("adminToken")){
            res.getWriter().write("2: access denied");
            return;
        }
        else{
            User user = userDAO.getUserByToken(body.getString("adminToken"));
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


        List<String> banPosts = new ArrayList<>();
        if(!body.isNull("bans")){
            JSONObject bans = body.getJSONObject("bans");
            for (int i = 0; i < bans.length(); i++) {
                banPosts.add(bans.getString(String.valueOf(i)));
            }
        }

        List<String> unbanPosts = new ArrayList<>();
        if(!body.isNull("unbans")) {
            JSONObject unbans = body.getJSONObject("unbans");
            for (int i = 0; i < unbans.length(); i++) {
                unbanPosts.add(unbans.getString(String.valueOf(i)));
            }
        }

        Boolean completed = postDAO.banPosts(banPosts, unbanPosts);
        if(completed != null){
            if(completed){
                res.getWriter().write("0: posts modified successfully");
            }
            else{
                res.getWriter().write("1: no posts were modified");
            }
        }
        else{
            res.getWriter().write("3: internal error");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        List<Post> posts = postDAO.getAllPosts();
        JSONArray jaPosts = new JSONArray();
        for (int i = 0; i < posts.size(); i++) {
            jaPosts.put(i, new JSONObject(posts.get(i)));
        }

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jaPosts.toString());
    }
}
