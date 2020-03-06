package xyz.wjsay.mywebchat.redis;

public class BasePrefix implements IKeyPrefix {
    private int expire;
    private String prefix;

    public BasePrefix() {
        expire = 0;
        prefix = "";
    }

    public BasePrefix(String prefix) {
        this.prefix = prefix;
    }

    public BasePrefix(int expire, String prefix) {
        this.expire = expire;
        this.prefix = prefix;
    }

    @Override
    public int getExpireSeconds() {
        return expire;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }
}
