package org.isomorphism.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("a")
public class HelloController {

    // 127.0.0.1:77/a/hello

    @GetMapping("hello")
    public Object hello() {
        return "Hello World";
    }

}
