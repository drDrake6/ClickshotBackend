package step.learning.entities;

import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.Timestamp;

public class Comment {
    String id;
    Timestamp date;
    String author;
    String content;
    String postId;

    public Comment(ResultSet commentRes) throws Exception {
        this(commentRes.getString("id"),
                Timestamp.valueOf(commentRes.getString("date")),
                commentRes.getString("author"),
                commentRes.getString("content"),
                commentRes.getString("postId"));
    }
    public Comment(JSONObject jcomment) throws Exception {
        if(jcomment.isNull("id"))
            setId(null);
        else
            setId(jcomment.getString("id"));
        setDate(Timestamp.valueOf(jcomment.getString("date")));
        setAuthor(jcomment.getString("author"));
        setContent(jcomment.getString("content"));
        setPostId(jcomment.getString("postId"));
    }
    public Comment(String id, Timestamp addDate, String author, String content, String postId) throws Exception {
        setId(id);
        setDate(addDate);
        setAuthor(author);
        setContent(content);
        setPostId(postId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setContent(String content) throws Exception {
        if(!content.equals("") && content != null)
            this.content = content;
        else
            throw new Exception("content can't be empty!");
    }
}
