package step.learning.entities;

import org.json.JSONObject;

import java.sql.ResultSet;

public class Response {
    String commentId;
    Comment answerComment;

    public Response(String commentId, Comment comment) {
        setCommentId(commentId);
        setAnswerComment(comment);
    }

    public Response(JSONObject janswer) throws Exception {
        setCommentId(janswer.getString("commentId"));
        setAnswerComment(new Comment(janswer.getJSONObject("answer")));
    }

    public Response(String commentId, ResultSet answerComment) throws Exception {
        setCommentId(commentId);
        setAnswerComment(new Comment(answerComment));
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Comment getAnswerComment() {
        return answerComment;
    }

    public void setAnswerComment(Comment answerComment) {
        this.answerComment = answerComment;
    }
}
