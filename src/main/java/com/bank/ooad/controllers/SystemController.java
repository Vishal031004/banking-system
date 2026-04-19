package com.bank.ooad.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SystemController {
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }
}
