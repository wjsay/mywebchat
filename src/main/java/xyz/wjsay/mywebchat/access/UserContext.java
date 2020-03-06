package xyz.wjsay.mywebchat.access;

import xyz.wjsay.mywebchat.domain.User;

public class UserContext {
    private static ThreadLocal<User> threadLocal = new ThreadLocal<>();
    public static void setUser(User user) {
        threadLocal.set(user);
    }
    public static User getUser() {
        return threadLocal.get();
    }
}
