package com.hoioy.springsecurityurlseparate.web;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BarController {

    @GetMapping("/bar")
    public String getSample() {
        return "get bar";
    }
}

