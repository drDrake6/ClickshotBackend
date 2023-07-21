package step.learning.services;
import com.google.inject.Inject;

import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class UploadService {

    @Inject
    private MimeService mimeService;

    public String Upload(Part file, String realPath, List<MimeService.MediaType> mediaTypes) throws Exception {
        if (file.getSize() > 0) {
            if(!mimeService.checkMimeTypes(mediaTypes, getExtension(file)))
                throw new Exception("7: Files with such extension are not supported");

            String path = randomUUID() + "." + getExtension(file);
            File uploaded = new File(realPath + "/Uploads/" + path);
            Files.copy(file.getInputStream(), uploaded.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return path;
        }
        else throw new Exception("6: Empty file are not supported");
    }

    private boolean checkExtension(Part file, MimeService.MediaType mediaType) throws Exception {
        if(!mimeService.checkMimeType(mediaType, getExtension(file))){
            return false;
        }
        else{
            return true;
        }
    }

    public String getExtension(Part file) throws Exception {
        String contentType = "";
        contentType = file.getContentType();
        String extension;
        int slashPosition = contentType.indexOf('/');
        if (slashPosition == -1) {
            throw new Exception("5: File without extension are not supported");
        } else {
            extension = contentType.substring(slashPosition + 1);
            return extension;
        }
    }

}
