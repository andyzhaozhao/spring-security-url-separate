package com.hoioy.springsecurityurlseparate.web;


import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class FooController {

    @GetMapping("/foo")
    public String getSample() {
        return "get foo";
    }

    @PostMapping("/foo")
    public String post(HttpServletRequest request) {
        return "post foo";
    }

    @DeleteMapping("/foo")
    public String delete(String sampleId) {
        return "delete foo";
    }

    @PutMapping("/foo")
    public String put(HttpServletRequest request) {
        return "put foo";
    }
}

