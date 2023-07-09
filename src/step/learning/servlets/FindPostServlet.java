package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONArray;
import org.json.JSONObject;
import step.learning.dao.PostDAO;
import step.learning.entities.Post;
import step.learning.entities.User;
import step.learning.services.BodyParseService;
import step.learning.services.LoadConfigService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Singleton
public class FindPostServlet extends HttpServlet {
    @Inject
    private PostDAO postDAO;
    @Inject
    private LoadConfigService loadConfigService;
    @Inject
    private BodyParseService bodyParseService;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        JSONObject body = bodyParseService.parseBody(req);

        int amount;
        int from;
        JSONObject params;
        try {
            amount = Integer.parseInt(body.getString("amount"));
            from = Integer.parseInt(body.getString("from"));
            params = body.getJSONObject("params");
        }catch (Exception ex){
            res.getWriter().write("1: invalid parameters " + ex.getMessage());
            return;
        }


        try{
            if(!params.isNull("addDate") &&
                    Timestamp.valueOf(params.getJSONObject("addDate").getString("from") + " 00:00:00")
                            .after(Timestamp.valueOf(params.getJSONObject("addDate").getString("to") + " 00:00:00"))){
                String tmp = params.getJSONObject("addDate").getString("from");
                JSONObject addDate = params.getJSONObject("addDate");
                addDate.put("from", addDate.getString("to"));
                addDate.put("to", tmp);
                params.put("addDate", addDate);
            }
        }catch (Exception ex){
            res.getWriter().write("1: invalid parameters " + ex.getMessage());
            return;
        }


        List<Post> posts = postDAO.findSomePosts(from, amount, params);
        JSONArray jPosts = new JSONArray();
        for (int i = 0; i < posts.size(); i++) {
            jPosts.put(i, new JSONObject(posts.get(i)));
        }

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin","*");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jPosts.toString());
    }
}
