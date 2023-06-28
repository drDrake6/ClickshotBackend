package step.learning.services;

import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class MimeService {

    public enum MediaType {IMAGE, VIDEO, AUDIO, PDF};
    private Map<String, String> imageTypes;
    private Map<String, String> videoTypes;
    private Map<String, String> audioTypes;

    private Map<MediaType, Map<String, String>> checker;
    public MimeService() {
        imageTypes = new HashMap<>();
        videoTypes = new HashMap<>();
        audioTypes = new HashMap<>();
        imageTypes.put("bmp",  "image/bmp" );
        imageTypes.put("gif",  "image/gif" );
        imageTypes.put("jpg",  "image/jpeg");
        imageTypes.put("jpeg", "image/jpeg");
        imageTypes.put("png",  "image/png" );
        imageTypes.put("webp", "image/webp");
        videoTypes.put("mp4",  "video/mp4");
        audioTypes.put("mp3",  "audio/mpeg");
        audioTypes.put("mpeg",  "audio/mpeg");
        checker = new HashMap<>();
        checker.put(MediaType.IMAGE, imageTypes);
        checker.put(MediaType.VIDEO, videoTypes);
        checker.put(MediaType.AUDIO, audioTypes);
    }

    public boolean checkMimeType(MediaType mediaType, String extension){
        return checker.get(mediaType).containsKey(extension.toLowerCase());
    }

    public boolean checkMimeTypes(List<MediaType> mediaTypes, String extension){
        for (MimeService.MediaType mediaType : mediaTypes) {
            if(checkMimeType(mediaType, extension)){
                return true;
            }
        }
        return false;
    }

    public String getMimeType(String extension){
        if(imageTypes.containsKey(extension)){
            return imageTypes.get(extension);
        }
        return null;
    }
}
