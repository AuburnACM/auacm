package com.auacm.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class StaticController {
    @RequestMapping(value = {"/problems", "/problems/**", "/blog", "/blog/**",
            "/rankings", "/competitions", "/competitions/**", "/problem/**", "/judge"}, method = RequestMethod.GET)
    public String forward() {
        return "/index.html";
    }
}
