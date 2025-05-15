package com.gujo.uminity.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/myPage")
    public String myPage() {
        return "myPage.html";
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

    @GetMapping("/postForm")
    public String postCreateForm() {
        return "postForm.html";
    }

    @GetMapping("/postForm/{postId}")
    public String postUpdateForm(@PathVariable("postId") String postId) {
        System.out.println(postId);
        return "/postForm.html";
    }

}
