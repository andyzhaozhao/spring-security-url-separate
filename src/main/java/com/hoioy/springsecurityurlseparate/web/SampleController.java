package com.hoioy.springsecurityurlseparate.web;


import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SampleController {

    @GetMapping("/sample")
    public String getSample() {
        return "getSample";
    }

    @PostMapping("/sample")
    public String post(HttpServletRequest request) {
        return "post";
    }

    @DeleteMapping("/sample")
    public String delete(String sampleId) {
        return "delete";
    }

    @PutMapping("/sample")
    @CrossOrigin(allowedHeaders = "*")
    public String put(HttpServletRequest request) {
        return "put";
    }
}

