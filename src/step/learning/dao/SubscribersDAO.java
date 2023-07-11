package step.learning.dao;

import com.google.inject.Inject;
import step.learning.entities.User;
import step.learning.services.DataService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubscribersDAO {

    private final DataService dataService;

    @Inject
    public SubscribersDAO(DataService dataService)
    {
        this.dataService = dataService;
    }

    public void subscribe(String author, String subscriber){
        Boolean isSubscribed = isSubscribedTo(author, subscriber, true);
        String sql;
        if(isSubscribed != null){
            if(isSubscribed)
                sql = "UPDATE Subscribers SET deleted = null, date = NOW() WHERE subscriberId = (SELECT id FROM Users WHERE login = ?) AND userId = (SELECT id FROM Users WHERE login = ?)";
            else
                sql = "INSERT INTO Subscribers (subscriberId, userId) VALUES ((SELECT id FROM Users WHERE login = ?), (SELECT id FROM Users WHERE login = ?))";
        }
        else
            return;

        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  subscriber);
            prep.setString(2,  author);
            prep.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("SubscribersDAO::subscribe() " + ex.getMessage()
                    + "\n" + sql);
        }
    }

    public void unsubscribe(String author, String subscriber){
        String sql = "UPDATE Subscribers SET deleted = NOW() WHERE subscriberId = (SELECT id FROM Users WHERE login = ?) AND userId = (SELECT id FROM Users WHERE login = ?)";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  subscriber);
            prep.setString(2,  author);
            prep.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("SubscribersDAO::unsubscribe() " + ex.getMessage()
                    + "\n" + sql);
        }
    }

    public Boolean isSubscribedTo(String author, String subscriber, boolean includeDeleted){

        String sql = "SELECT * FROM Subscribers WHERE subscriberId = (SELECT id FROM Users WHERE login = ?) AND userId = (SELECT id FROM Users WHERE login = ?)";
        if(!includeDeleted)
            sql += " AND deleted IS NULL";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1,  subscriber);
            prep.setString(2,  author);
            ResultSet res = prep.executeQuery();
            if(res.next()){
                return true;
            }
            else return false;
        } catch (SQLException ex) {
            System.out.println("SubscribersDAO::isSubscribedTo() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public int subscribersAmount(String author, boolean includeDeleted){

        String sql = "SELECT COUNT(*) as count FROM Subscribers WHERE userId = (SELECT id FROM Users WHERE login = ?) AND subscriberId IN (SELECT id FROM Users WHERE Users.deleted IS NULL )";
        if(!includeDeleted)
            sql += " AND deleted IS NULL";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, author);
            ResultSet res = prep.executeQuery();
            if(res.next()){
                return res.getInt("count");
            }
            else return 0;
        } catch (SQLException ex) {
            System.out.println("SubscribersDAO::subscribersAmount() " + ex.getMessage()
                    + "\n" + sql);
        }
        return -1;
    }

    public int subscribingAmount(String author, boolean includeDeleted){

        String sql = "SELECT COUNT(*) as count FROM Subscribers WHERE subscriberId = (SELECT id FROM Users WHERE login = ?) AND userId IN (SELECT id FROM Users WHERE Users.deleted IS NULL )";
        if(!includeDeleted)
            sql += " AND deleted IS NULL";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, author);
            ResultSet res = prep.executeQuery();
            if(res.next()){
                return res.getInt("count");
            }
            else return 0;
        } catch (SQLException ex) {
            System.out.println("SubscribersDAO::subscribingAmount() " + ex.getMessage()
                    + "\n" + sql);
        }
        return -1;
    }

    public List<User> getSubscribers(String author, int from, int amount, boolean includeDeleted){

        String sql = "SELECT * FROM Users WHERE id IN " +
                "(SELECT subscriberId FROM Subscribers " +
                "WHERE userId = " +
                "(SELECT id FROM Users WHERE login = ?) " +
                "AND subscriberId IN " +
                "(SELECT id FROM Users WHERE Users.deleted IS NULL ))";
        if(!includeDeleted)
            sql += " AND deleted IS NULL";
        sql += " ORDER BY login LIMIT ?, ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, author);
            prep.setInt(2, from);
            prep.setInt(3, amount);
            ResultSet res = prep.executeQuery();
            List<User> users = new ArrayList<>();
            if(res.next()){
                users.add(new User(res));
            }
            return users;
        } catch (SQLException ex) {
            System.out.println("SubscribersDAO::getSubscribers() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }

    public List<User> getSubscribing(String author, int from, int amount, boolean includeDeleted){

        String sql = "SELECT * FROM Users WHERE id IN " +
                "(SELECT userId FROM Subscribers " +
                "WHERE subscriberId = " +
                "(SELECT id FROM Users WHERE login = ?) " +
                "AND userId IN " +
                "(SELECT id FROM Users WHERE Users.deleted IS NULL ))";
        if(!includeDeleted)
            sql += " AND deleted IS NULL";
        sql += " ORDER BY login LIMIT ?, ?";
        try (PreparedStatement prep =
                     dataService.getConnection().prepareStatement(sql)) {
            prep.setString(1, author);
            prep.setInt(2, from);
            prep.setInt(3, amount);
            ResultSet res = prep.executeQuery();
            List<User> users = new ArrayList<>();
            if(res.next()){
                users.add(new User(res));
            }
            return users;
        } catch (SQLException ex) {
            System.out.println("SubscribersDAO::getSubscribing() " + ex.getMessage()
                    + "\n" + sql);
        }
        return null;
    }
}
