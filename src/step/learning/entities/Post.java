package step.learning.entities;

import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Post {

    public Post(String id, Timestamp addDate, String author, String description, String mediaUrl, String metadata, Timestamp postponePublication, Timestamp banned) {
        this.setId(                  id);
        this.setAddDate(             addDate);
        this.setAuthor(              author);
        this.setDescription(         description);
        this.setMediaUrl(            mediaUrl);
        this.setMetadata(            metadata);
        this.setPostponePublication( postponePublication);
        this.setBaned(banned);
    }

    public Post(JSONObject post) {

        if(!post.isNull("id")) this.setId(post.getString("id"));
        else this.setId(null);
        this.setAddDate(            Timestamp.valueOf(post.getString("addDate")));
        this.setAuthor(           post.getString("author"));
        this.setDescription(        post.getString("description"));
        this.setMediaUrl(           post.getString("mediaUrl"));
        this.setMetadata(           post.getString("metadata"));
        this.setPostponePublication(Timestamp.valueOf(post.getString("postponePublication")));
        this.setBaned(null);
    }

    public Post(ResultSet res) throws SQLException {
        this.setId(res.getString("id"));
        this.setPostponePublication(res.getTimestamp("postponePublication"));
        this.setAuthor(res.getString("author"));
        this.setDescription(res.getString("description"));
        this.setMediaUrl(res.getString("mediaUrl"));
        this.setMetadata(res.getString("metadata"));
        this.setAddDate(res.getTimestamp("addDate"));
        this.setBaned(res.getTimestamp("baned"));
    }

    String id;
    Timestamp addDate;
    String author;
    String description;
    String mediaUrl;
    String metadata;
    Timestamp postponePublication;
    Timestamp baned;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getAddDate() {
        return addDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAddDate(Timestamp addDate) {
        this.addDate = addDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Timestamp getPostponePublication() {
        return postponePublication;
    }

    public void setPostponePublication(Timestamp postponePublication) {
        this.postponePublication = postponePublication;
    }
    public Timestamp isBaned() {
        return baned;
    }
    public void setBaned(Timestamp baned) {
        this.baned = baned;
    }

    @Override
    public String toString() {
        return "{" + ",\n" +
                "id='" + id + ",\n" +
                "addDate=" + addDate + ",\n" +
                "authorID=" + author + ",\n" +
                "header='" + description + ",\n" +
                "mediaUrl='" + mediaUrl + ",\n" +
                "metadata='" + metadata + ",\n" +
                "postponePublication=" + postponePublication + ",\n" +
                '}';
    }
}
