package step.learning.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.sql.Timestamp;

public class User {
    String id;
    String login;
    String password;
    String avatar;
    String name;
    String surname;
    String email;
    String email_code;
    int email_attempt;
    String salt;
    String bio;
    Timestamp birthday;
    String token;
    char role;

    public User() {
    }
    public User(JSONObject obj) throws Exception {

        this.setId("");
        this.setLogin(obj.getString("login"));
        this.setPassword(obj.getString("password"));
        this.setName(obj.getString("name"));
        this.setSurname(obj.getString("surname"));
        this.setSalt("");
        this.setAvatar(obj.getString("avatar"));
        this.setEmail(obj.getString("email"));
        this.setEmail_code(null);
        this.setEmail_attempt(0);
        this.setBio(obj.getString("bio"));

        this.setBirthday(Timestamp.valueOf(obj.getString("birthday")));

        if(this.getLogin().equals("") ||
           this.getPassword().equals("") ||
           this.getEmail().equals(""))
        {
            throw new Exception("login, password or email can not be null");
        }

        if(!obj.isNull("token")){
            this.setToken(obj.getString("token"));
        }

        if(!obj.isNull("role")){
            this.setRole(obj.getString("role").charAt(0));
        }
    }

    public User(ResultSet res) throws SQLException {
        this.setId(res.getString("id"));
        this.setLogin(res.getString("login"));
        this.setPassword(res.getString("password"));
        this.setName(res.getString("name"));
        this.setSurname(res.getString("surname"));
        this.setSalt(res.getString("salt"));
        this.setAvatar(res.getString("avatar"));
        this.setEmail(res.getString("email"));
        this.setEmail_code(res.getString("email_code"));
        this.setEmail_attempt(res.getInt("email_attempt"));
        this.setBio(res.getString("bio"));
        this.setBirthday(res.getTimestamp("birthday"));
        this.setToken(res.getString("token"));
        this.setRole(res.getString("role").charAt(0));
    }

    public JSONObject ToViewJSON(){
        JSONObject jsonObject = new JSONObject(this);
        jsonObject.remove("role");
        jsonObject.remove("salt");
        jsonObject.remove("email_attempt");
        if(getEmail_code() == null){
            jsonObject.put("isEmailConfirmed", true);
        }
        else {
            jsonObject.put("isEmailConfirmed", !getEmail_code().equals("not_confirmed"));
        }
        jsonObject.remove("email_code");

        return jsonObject;
    }

    public static void updateUser(User user, JSONObject obj){
        user.setName(obj.getString("name"));
        user.setSurname(obj.getString("surname"));
        user.setBio(obj.getString("bio"));
        user.setBirthday(Timestamp.valueOf(obj.getString("birthday")));
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", avatar='" + avatar + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", email_code=" + email_code +
                ", email_attempt=" + email_attempt +
                ", salt='" + salt + '\'' +
                ", bio='" + bio + '\'' +
                ", birthday=" + birthday +
                ", token=" + token +
                ", role=" + role +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail_code() {
        return email_code;
    }

    public void setEmail_code(String email_code) {
        this.email_code = email_code;
    }

    public int getEmail_attempt() {
        return email_attempt;
    }

    public void setEmail_attempt(int email_attempt) {
        this.email_attempt = email_attempt;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public char getRole() {
        return role;
    }

    public void setRole(char role) {
        this.role = role;
    }
}
