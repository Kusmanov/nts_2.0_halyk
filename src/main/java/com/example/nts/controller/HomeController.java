package com.example.nts.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Tag(name = "Home")
public class HomeController {
    private static final Logger logger = LogManager.getLogger(HomeController.class);

    @GetMapping
    public String home(HttpServletRequest request) {
        String ip = request.getLocalAddr(); // IP-адрес, к которому клиент подключился
        int port = request.getLocalPort();

        return "<a href=\"http://" + ip + ":" + port + "/swagger-ui/index.html\">OpenAPI Documentation</a>";
    }
}