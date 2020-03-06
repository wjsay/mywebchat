package xyz.wjsay.mywebchat.domain;

import javax.websocket.OnMessage;

public class ChatMessage {
    private int type; // 2私聊消息，3群聊消息。系统消息：1用户上线，-1用户下线
    private String from;  // 发送者username
    private String to; //
    private Object content; // 消息内容

    public ChatMessage() {
    }

    public ChatMessage(int type, String from, String to, Object content) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.content = content;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Object getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "{" +
                "\"type\":" + type +
                ", \"from\":\"" + from + "\"" +
                ", \"to\":\"" + to + "\"" +
                ", \"content\":" + content.toString() +
                '}';
    }
}
