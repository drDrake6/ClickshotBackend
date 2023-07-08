package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.MimeService;
import step.learning.services.UploadService;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Request;

@MultipartConfig
@Singleton
public class MediaServlet extends HttpServlet {
    @Inject
    private MimeService mimeService;
    @Inject
    private UploadService uploadService;
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            if ("POST".equals(req.getMethod()) && req.getContentType().contains("multipart/form-data")) {
                req.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement(""));
            }

            List<MimeService.MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MimeService.MediaType.IMAGE);
            mediaTypes.add(MimeService.MediaType.VIDEO);
            mediaTypes.add(MimeService.MediaType.AUDIO);

            uploadService.Upload(req.getPart("media"), req.getServletContext().getRealPath("/"), mediaTypes); //contentType.substring(contentType.indexOf('/') + 1)
        }catch (Exception ex){
            res.getWriter().write(ex.getMessage());
            return;
        }
        res.getWriter().write("0: successfully loaded");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestedFile = req.getPathInfo();
        int dotPosition = requestedFile.lastIndexOf('.');
        if(dotPosition == -1){
            resp.getWriter().print("1: Files without extension not allowed");
            return;
        }

        String extension = requestedFile.substring(dotPosition + 1);
        if(!mimeService.checkMimeType(MimeService.MediaType.IMAGE, extension) &&
           !mimeService.checkMimeType(MimeService.MediaType.VIDEO, extension) &&
           !mimeService.checkMimeType(MimeService.MediaType.AUDIO, extension)){
            resp.getWriter().print("2: Files not supported" + extension);
            return;
        }
        String path = req.getServletContext().getRealPath("/");
        File file = new File(path + "/Uploads" + requestedFile);
        if(file.isFile() && file.canRead()){
            resp.setContentType(mimeService.getMimeType(extension));
            resp.setContentLengthLong((file.length()));
            OutputStream writer = null;
            try(InputStream reader = Files.newInputStream(file.toPath())){
                writer = resp.getOutputStream();
                byte[] buf = new byte[2048];
                int bytesRead;
                while((bytesRead = reader.read(buf)) > 0){
                    writer.write(buf, 0, bytesRead);
                }
            }catch(IOException ex){
                resp.reset();
                resp.getWriter().print("4: Unrecognized file server error"  + ex.getClass().getName() + "\n" + ex.getMessage());
                System.out.println("imagesServlet::doGet " + requestedFile +
                        "\n" + ex.getMessage());
            }
        }
        else {
            resp.getWriter().print("3: File not found" + requestedFile);
        }

        //resp.getWriter().print("0: requested file" + requestedFile);
    }


}
