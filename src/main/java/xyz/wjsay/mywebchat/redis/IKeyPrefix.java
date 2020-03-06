package xyz.wjsay.mywebchat.redis;

public interface IKeyPrefix {
    int getExpireSeconds();
    String getPrefix();
}
