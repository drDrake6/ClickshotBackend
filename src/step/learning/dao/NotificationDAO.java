package step.learning.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONArray;
import org.json.JSONObject;
import step.learning.services.DataService;
import step.learning.services.LoggerService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class NotificationDAO {

    private final DataService dataService;
    private final LoggerService loggerService;
    String sql;

    @Inject
    public NotificationDAO(DataService dataService, LoggerService loggerService)
    {
        this.dataService = dataService;
        this.loggerService = loggerService;
        sql = "SELECT postId,\n" +
                "       commentId,\n" +
                "       author,\n" +
                "       user,\n" +
                "       date,\n" +
                "       action FROM (SELECT postId,\n" +
                "                           null as commentId,\n" +
                "                           (SELECT Posts.author FROM Posts WHERE Posts.id = SavedBy.postId\n" +
                "                           ) as author,\n" +
                "                           (SELECT Users.login FROM Users WHERE Users.id = SavedBy.userId\n" +
                "                           ) as user,\n" +
                "                           SavedBy.date,\n" +
                "                           'save' as 'action'\n" +
                "                    FROM SavedBy\n" +
                "                    UNION ALL\n" +
                "                    SELECT postId,\n" +
                "                           null as commentId,\n" +
                "                           (SELECT Posts.author FROM Posts WHERE Posts.id = LikedBy.postId\n" +
                "                           ) as author,\n" +
                "                           (SELECT Users.login FROM Users WHERE Users.id = LikedBy.userId\n" +
                "                           ) as user,\n" +
                "                           LikedBy.date,\n" +
                "                           'like' as 'action'\n" +
                "                    FROM LikedBy\n" +
                "                    UNION ALL\n" +
                "                    SELECT postId,\n" +
                "                           id commentId,\n" +
                "                           (SELECT Posts.author FROM Posts WHERE Posts.id = Comments.postId\n" +
                "                           ) as author,\n" +
                "                           author as 'user',\n" +
                "                           Comments.date,\n" +
                "                           'comment' as 'action'\n" +
                "                    FROM Comments\n" +
                "                    UNION ALL\n" +
                "                    SELECT null as postId,\n" +
                "                           null as commentId,\n" +
                "                           (SELECT Users.login FROM Users WHERE Users.id = Subscribers.userId\n" +
                "                           ) as author,\n" +
                "                           (SELECT Users.login FROM Users WHERE Users.id = Subscribers.subscriberId\n" +
                "                           ) as user,\n" +
                "                           Subscribers.date,\n" +
                "                           'subscribe' as 'action'\n" +
                "                    FROM Subscribers\n" +
                "                    UNION ALL\n" +
                "                    SELECT null as postId,\n" +
                "                           null as commentId,\n" +
                "                           (SELECT Users.login FROM Users WHERE Users.id = Subscribers.userId\n" +
                "                           ) as author,\n" +
                "                           (SELECT Users.login FROM Users WHERE Users.id = Subscribers.subscriberId\n" +
                "                           ) as user,\n" +
                "                           Subscribers.deleted as date,\n" +
                "                           'unsubscribe' as 'action'\n" +
                "                    FROM Subscribers\n" +
                "                    WHERE deleted IS NOT NULL\n" +
                "                    UNION ALL\n" +
                "                    SELECT postId,\n" +
                "                           null as commentId,\n" +
                "                           (SELECT Users.login FROM Users WHERE Users.id = TaggedPeople.userId\n" +
                "                           ) as author,\n" +
                "                           (SELECT Posts.author FROM Posts WHERE Posts.id = TaggedPeople.postId\n" +
                "                           ) as user,\n" +
                "                           TaggedPeople.date,\n" +
                "                           'tage' as 'action'\n" +
                "                    FROM TaggedPeople\n" +
                "                    UNION ALL\n" +
                "                    SELECT null as postId,\n" +
                "                           id_response as commentId,\n" +
                "                           (SELECT Comments.author FROM Comments WHERE id = Responses.id_comment\n" +
                "                           ) as author,\n" +
                "                           (SELECT Comments.author FROM Comments WHERE id = Responses.id_response\n" +
                "                           ) as user,\n" +
                "                           Responses.date,\n" +
                "                           'response' as 'action'\n" +
                "                    FROM Responses) as `notifications`\n" +
                "WHERE author = ?\n" +
                "ORDER BY date DESC\n" +
                "LIMIT ?, ?;";
    }

    public JSONArray getSomeNotifications(String author, int from, int amount){
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, author);
            prep.setInt(2, from);
            prep.setInt(3, amount);
            ResultSet res = prep.executeQuery();
            JSONArray notifications = new JSONArray();
            while(res.next()){
                JSONObject notification = new JSONObject();
                notification.put("action", res.getString("action"));
                notification.put("date", res.getString("date"));
                notification.put("user", res.getString("user"));
                notification.put("author", res.getString("author"));
                notification.put("commentId", res.getString("commentId"));
                notification.put("postId", res.getString("postId"));
                notifications.put(notification);
            }
            return notifications;
        } catch (SQLException ex) {
            loggerService.log("NotificationDAO::getSomeNotifications() " + ex.getMessage()
                    + "\n" + sql, LoggerService.Status.ERROR);
        }
        return null;
    }
}
