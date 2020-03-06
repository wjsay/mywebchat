package xyz.wjsay.mywebchat.controller;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.ContextLoader;
import xyz.wjsay.mywebchat.domain.ChatMessage;
import xyz.wjsay.mywebchat.domain.User;
import xyz.wjsay.mywebchat.redis.RedisService;
import xyz.wjsay.mywebchat.redis.UserPrefix;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Controller
@ServerEndpoint("/room")
public class MyWebSocket {
    private static Logger logger = LoggerFactory.getLogger(MyWebSocket.class);
    private static Integer onlineCount = 0;
    private static Set<MyWebSocket> webSockets = new CopyOnWriteArraySet<>();
    private static Hashtable<String, MyWebSocket> socketMap = new Hashtable<>();
    private User user;
    private User chatingUser;
    private Session session;
    private static RedisService redisService;

    @Autowired
    public void setRedisService(RedisService redisService){
        MyWebSocket.redisService = redisService;
    }
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        String token = session.getQueryString();
        try {
            this.user = redisService.get(UserPrefix.TOKEN, token, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.user == null) {
            logger.warn(token);
            onClose(session);
            return;
        }
        webSockets.add(this);
        socketMap.put(user.getUsername(), this);
        synchronized (onlineCount) {
            onlineCount += 1;
        }
        logger.info(user.getUsername() + "上线！");
        this.sendNotifyMessage(1);
    }

    @OnClose
    public void onClose(Session session) {
        webSockets.remove(this);
        if (user != null) {
            socketMap.remove(user.getUsername());
            logger.info(user.getUsername() + "下线。");
        }
        synchronized (onlineCount) {
            onlineCount -= 1;
        }
        sendNotifyMessage(-1);
        user = null;
    }

    @OnError
    public void onError(Throwable throwable) {
        logger.error(throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String json, Session session) {
        ChatMessage chatMessage = JSON.parseObject(json, ChatMessage.class);
        if(!(chatMessage.getContent() instanceof String)) {
            return;
        }
        chatMessage.setType(3);
        String message = JSON.toJSONString(chatMessage);
        if (chatMessage.getTo() == null) {  // 群聊消息
            webSockets.forEach(webSocket->{
                if (!webSocket.user.getUsername().equals(chatMessage.getFrom())) {
                    try {
                        webSocket.session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                    }
                }
            });
        } else if (chatMessage.getType() == 2) {
            MyWebSocket webSocket = socketMap.get(chatMessage.getTo());
            if (webSocket != null) {
                try {
                    webSocket.session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }
        }
    }
    @OnMessage
    public void onMessage(ByteBuffer buffer, Session session) {
        if (chatingUser == null) {
            // assert user.getUsername().equals(chatMessage.getFrom());
            webSockets.forEach(webSocket->{
                if (!webSocket.user.getUsername().equals(user.getUsername())) {
                    try {
                        webSocket.session.getBasicRemote().sendBinary(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                    }
                }
            });
        } else {
            MyWebSocket webSocket = socketMap.get(chatingUser.getUsername());
            if (webSocket != null) {
                try {
                    webSocket.session.getBasicRemote().sendBinary(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }
        }
    }

    private void sendNotifyMessage(int type) {
        if (user == null) return;
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(type);
        chatMessage.setContent(user);
        chatMessage.setFrom(user.getUsername());
        chatMessage.setTo(null);
        if (webSockets.size() > 0) {
            try {
                webSockets.iterator().next().session.getBasicRemote().sendText(chatMessage.toString());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }

    public static Set<String> getAllUsers() {
        return socketMap.keySet();
    }

    public User getUser() {
        return user;
    }

    public User getChatingUser() {
        return chatingUser;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setChatingUser(User chatingUser) {
        this.chatingUser = chatingUser;
    }
}
