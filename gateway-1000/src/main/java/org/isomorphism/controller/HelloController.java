package org.isomorphism.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("g")
public class HelloController {

    // 127.0.0.1:1000/g/hello

    @GetMapping("hello")
    public Object hello() {
        return "Hello World";
    }

}
