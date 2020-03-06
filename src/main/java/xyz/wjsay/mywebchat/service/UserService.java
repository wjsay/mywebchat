package xyz.wjsay.mywebchat.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.wjsay.mywebchat.dao.UserDao;
import xyz.wjsay.mywebchat.domain.User;
import xyz.wjsay.mywebchat.redis.RedisService;
import xyz.wjsay.mywebchat.redis.UserPrefix;
import xyz.wjsay.mywebchat.util.MD5Utils;
import xyz.wjsay.mywebchat.util.UUIDUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserService {
    public static String COOKIE_NAME_TOKEN = "token";
    @Autowired
    UserDao userDao;
    @Autowired
    RedisService redisService;

    public int addUser(User user) {
        try {
            user.setLastLoginTime(System.currentTimeMillis());
            user.setPassword(MD5Utils.md5(user.getPassword()));
            User tmp = userDao.getUserByUsername(user.getUsername());
            if (userDao.getUserByUsername(user.getUsername()) != null) {
                return -1;
            }
            userDao.addUser(user);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -2;
        }
    }

    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public User getUserByTokenVal(HttpServletResponse response, String tokenVal) {
        if (StringUtils.isEmpty(tokenVal)) {
            return null;
        }
        User user = redisService.get(UserPrefix.TOKEN, tokenVal, User.class);
        if (user != null) {
            this.addCookie(response, tokenVal, user);
        }
        return user;
    }

    public boolean checkUse(HttpServletResponse response, String username, String password) {
        User user = userDao.getUserByUsername(username);
        password = MD5Utils.md5(password);
        if (user != null && user.getPassword().equals(password)) {
            String key = UUIDUtils.uuid();
            user.setLastLoginTime(System.currentTimeMillis());
            recordLogin(user);
            this.addCookie(response, key, user);
            return true;
        }
        return false;
    }

    public void addCookie(HttpServletResponse response, String tokenVal, User user) {
        redisService.set(UserPrefix.TOKEN, tokenVal, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, tokenVal);
        cookie.setMaxAge(UserPrefix.TOKEN.getExpireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public boolean recordLogin(User user) {
        try {
            userDao.recordLogin(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
