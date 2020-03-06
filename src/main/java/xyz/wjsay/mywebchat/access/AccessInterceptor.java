package xyz.wjsay.mywebchat.access;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import xyz.wjsay.mywebchat.domain.User;
import xyz.wjsay.mywebchat.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            User user = this.getUser(request, response);
            UserContext.setUser(user);
        }
        return super.preHandle(request, response, handler);
    }

    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramValue = request.getParameter(UserService.COOKIE_NAME_TOKEN);
        String cookieValue = this.getCookieValue(request, UserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(paramValue) && StringUtils.isEmpty(cookieValue)) {
            return null;
        }
        String value = StringUtils.isEmpty(paramValue) ? cookieValue : paramValue;
        return userService.getUserByTokenVal(response, value);
    }

    private String getCookieValue(HttpServletRequest request, String token) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(UserService.COOKIE_NAME_TOKEN)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
