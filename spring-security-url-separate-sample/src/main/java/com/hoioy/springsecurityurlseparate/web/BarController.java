package com.hoioy.springsecurityurlseparate.web;


import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class BarController {

    @GetMapping("/bar")
    public String getSample() {
        return "get bar";
    }

    @PostMapping("/bar")
    public String post(HttpServletRequest request) {
        return "post bar";
    }

    @DeleteMapping("/bar")
    public String delete(String sampleId) {
        return "delete bar";
    }

    @PutMapping("/bar")
    @CrossOrigin(allowedHeaders = "*")
    public String put(HttpServletRequest request) {
        return "put bar";
    }
}

