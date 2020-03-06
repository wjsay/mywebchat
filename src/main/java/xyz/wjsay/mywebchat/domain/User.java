package xyz.wjsay.mywebchat.domain;

public class User {
    private String username;
    private String password;
    private String nickname;
    private int gender; // 0未知，1女生，2男生
    private long lastLoginTime;

    public User() {
    }

    public User(String username, String password, String nickname, int gender) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public int getGender() {
        return gender;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Override
    public String toString() {
        return "{" +
                "\"username\":\"" + username + '\"' +
                ", \"nickname\":\"" + nickname + '\"' +
                ", \"gender\":" + gender +
                '}';
    }
}
