package com.example.demo.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @GetMapping("/admin/ping")
    public String adminPing() {
        return "admin ok";
    }
}
