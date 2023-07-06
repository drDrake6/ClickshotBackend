package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.jetty.server.Request;
import org.json.JSONObject;
import step.learning.dao.PostDAO;
import step.learning.dao.TaggedPeopleDAO;
import step.learning.dao.UserDAO;
import step.learning.entities.Post;
import step.learning.services.*;
import sun.misc.IOUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Singleton
public class AddPostServlet extends HttpServlet {
    @Inject
    private BodyParseService bodyParseService;
    @Inject
    private PostDAO postDAO;
    @Inject
    private MimeService mimeService;
    @Inject
    private UploadService uploadService;
    @Inject
    private UserDAO userDAO;
    @Inject
    private TaggedPeopleDAO taggedPeopleDAO;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        try {
            if ("POST".equals(req.getMethod()) && req.getContentType().contains("multipart/form-data")) {
                req.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement(""));
            }
            Part imagePart = req.getPart("media");

            List<MimeService.MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MimeService.MediaType.IMAGE);
            mediaTypes.add(MimeService.MediaType.VIDEO);
            mediaTypes.add(MimeService.MediaType.AUDIO);

            String path = uploadService.Upload(imagePart,
                    req.getServletContext().getRealPath("/"),
                    mediaTypes);

            Part postPart = req.getPart("otherInfo");
            byte[] postBytes;
            InputStream is = postPart.getInputStream();
            postBytes = IOUtils.readAllBytes(is);
            String postSource = new String(postBytes, StandardCharsets.UTF_8);
            JSONObject jpost = new JSONObject(postSource);

            jpost.put("id", UUID.randomUUID().toString());
            jpost.put("addDate", LocalDateTime.now().
                    format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            jpost.put("postponePublication", jpost.get("postponePublication"));
            jpost.put("mediaUrl", path);
            Post post = new Post(jpost);
            postDAO.add(post);

            if(!jpost.isNull("taggedPeople")){
                String tmp = jpost.getString("taggedPeople");
                String[] logins = tmp.split(" ");
                taggedPeopleDAO.setTaggedPeople(jpost.getString("id"), logins);
            }
        }catch (Exception ex){
            res.getWriter().write("1: " + ex.getMessage());
            return;
        }
        res.getWriter().write("successfully added");
    }

//    try {
//        if ("POST".equals(req.getMethod()) && req.getContentType().contains("multipart/form-data")) {
//            req.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement(""));
//        }
//        String path = uploadImageService.Upload(req.getPart("ava"), req.getServletContext().getRealPath("/"), ".jpg"); //contentType.substring(contentType.indexOf('/') + 1)
//        Part partLogin = req.getPart("login");
//
//        byte[] loginBytes = new byte[(int)partLogin.getSize()];
//        InputStream is = partLogin.getInputStream();
//        loginBytes = IOUtils.readAllBytes(is);
//        String login = new String(loginBytes, StandardCharsets.UTF_8);
//        User user = userDAO.getUser(login);
//        user.setAvatar(path);
//        userDAO.update(user, user.getId());
//    }catch (Exception ex){
//        res.getWriter().write("1: " + ex.getStackTrace().toString());
//    }
//        res.getWriter().write("0: successfully loaded");
}
