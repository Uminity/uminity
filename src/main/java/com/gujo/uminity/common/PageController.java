package com.gujo.uminity.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/login")
    public String login() {
        return "login.html";
    }

    @GetMapping("/register")
    public String register() {
        return "register.html";
    }
    
    @GetMapping("/postDetail/{postId}")
    public String postDetail(@PathVariable("postId") String postId) {
    	System.out.println(postId);
        return "/postDetail.html";
    }
    
}
