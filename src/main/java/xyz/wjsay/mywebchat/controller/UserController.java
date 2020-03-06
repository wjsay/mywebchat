package xyz.wjsay.mywebchat.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.wjsay.mywebchat.domain.User;
import xyz.wjsay.mywebchat.redis.RedisService;
import xyz.wjsay.mywebchat.redis.UserPrefix;
import xyz.wjsay.mywebchat.result.CodeMsg;
import xyz.wjsay.mywebchat.service.UserService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.PathParam;

@Controller
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;

    @RequestMapping("/")
    public String defaultPage() {
        return "login";
    }

    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    @ResponseBody
    // 因为我拦截了User类型的参数，所以这里无法用User获取form表单数据
    public CodeMsg register(@PathParam("username")String username, @PathParam("password")String password,
                            @PathParam("nickname")String nickname, @PathParam("gender")Integer gender) {
        User user = new User(username, password, nickname, gender);
        if (user == null) {
            return CodeMsg.ARGUMENT_ILLEGAL;
        }
        int status = userService.addUser(user);
        if (status == 0) {
            return CodeMsg.SUCCESS;
        } else if (status == -1) {
            return  CodeMsg.USER_EXIST;
        } else {
            return CodeMsg.SERVER_ERROR;
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CodeMsg login(HttpServletResponse response,
                         @RequestParam("username")String username, @RequestParam("password")String password) {
        // logger.info(username + ":" + password);
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return CodeMsg.ARGUMENT_ILLEGAL;
        }
        if (userService.checkUse(response, username, password)) {
            return CodeMsg.SUCCESS;
        }else {
            return CodeMsg.INFO_ERROR;
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public CodeMsg logout(User user) {
        if (user == null) {
            return CodeMsg.NO_LOGIN;
        }
        try {
            redisService.del(UserPrefix.TOKEN, user.getUsername());
            return CodeMsg.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return CodeMsg.SERVER_ERROR;
        }
    }
}
