package xyz.wjsay.mywebchat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.wjsay.mywebchat.domain.User;
import xyz.wjsay.mywebchat.result.CodeMsg;

import java.util.Set;

@Controller
public class ChatController {
    @RequestMapping("/chat")
    public String chat(User user, Model model) {
        if (user == null || user.getUsername().equals("")) {
            return "login";
        }
        Set<String> allUsers = MyWebSocket.getAllUsers();
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("user", user);
        model.addAttribute("onlineCount", allUsers.size());
        return "chat";
    }

    @RequestMapping("/refresh")
    @ResponseBody
    public CodeMsg refresh(User user) {
        if (user == null || user.getUsername().equals("")) {
            return CodeMsg.NO_LOGIN;
        }
        Object obj = MyWebSocket.getAllUsers();
        return CodeMsg.success(obj);
    }

}
