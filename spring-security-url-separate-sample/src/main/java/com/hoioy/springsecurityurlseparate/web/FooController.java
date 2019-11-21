package com.hoioy.springsecurityurlseparate.web;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FooController {

    @GetMapping("/foo")
    public String getSample() {
        return "get foo";
    }

}

