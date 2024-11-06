package com.lingvoFriend.backend.Controllers;

import com.lingvoFriend.backend.Repositories.UserRepository;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

// for now basic controller for future paths
// at / and /home return some text
// every other get request path redirects to /home

@RestController
@AllArgsConstructor
class Controller {

    private final UserRepository repository;

    @GetMapping("/")
    public String Greetings() {
        return "<h1>Hey<h1>";
    }

    @GetMapping("/home")
    public String Greetings1() {

        return "<h1>Home<h1>";
    }

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public RedirectView handleAllGetRequests() {
        return new RedirectView("/home");
    }
}
