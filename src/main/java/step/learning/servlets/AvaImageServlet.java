package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.jetty.server.Request;
import org.json.JSONObject;
import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.services.BodyParseService;
import step.learning.services.MimeService;
import step.learning.services.UploadService;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AvaImageServlet extends HttpServlet {
    @Inject
    private MimeService mimeService;
    @Inject
    private UploadService uploadService;
    @Inject
    private BodyParseService bodyParseService;
    @Inject
    private UserDAO userDAO;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setHeader("Access-Control-Allow-Origin","*");
        try {
            if ("POST".equals(req.getMethod()) && req.getContentType().contains("multipart/form-data")) {
                req.setAttribute(Request.MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement(""));
            }

            List<MimeService.MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MimeService.MediaType.IMAGE);

            String path = uploadService.Upload(req.getPart("ava"),
                    req.getServletContext().getRealPath("/"),
                    mediaTypes);
            Part partLogin = req.getPart("login");
            byte[] loginBytes = new byte[(int) partLogin.getSize()];
            InputStream is = partLogin.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            dis.readFully(loginBytes);
            dis.close();
            String login = new JSONObject(new String(loginBytes, StandardCharsets.UTF_8)).getString("login");
            User user = userDAO.getUser(login);
            user.setAvatar(path);
            userDAO.update(user, user.getId());
        }catch (Exception ex){
            res.getWriter().write("1: " + ex.getMessage());
            return;
        }
        res.getWriter().write("0: successfully loaded");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin","*");
        String login = req.getParameter("login");
        User user = userDAO.getUser(login);
        if(user == null){
            resp.getWriter().write("");
            return;
        }
        String ava = user.getAvatar();
        if(ava != null && !ava.equals(""))
            resp.getWriter().write(ava);
        else
            resp.getWriter().write("");
    }
}
