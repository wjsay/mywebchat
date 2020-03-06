package xyz.wjsay.mywebchat.redis;

public class UserPrefix extends BasePrefix {
    private UserPrefix(String prefix) {
        super(prefix);
    }

    public static UserPrefix TOKEN = new UserPrefix("utk");

    @Override
    public int getExpireSeconds() {
        return 24 * 3600;
    }
}
