package com.hangoutz.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EventController {

    @GetMapping("/welcome")
    public String displayWelcome() {
        return "Controller is up and running...";
    }
}
