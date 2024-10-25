package com.prodify.cqrs.gatewayservice.controller;

import com.prodify.cqrs.gatewayservice.config.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtUtil jwtUtil;

    public AuthenticationController(@Value("${jwt.secret}") String jwtSecret) {
        this.jwtUtil = new JwtUtil(jwtSecret);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        // In a real application, validate the username and password (e.g., check in a database)
        // Here, we assume authentication is successful if any username/password is given.

        String token = jwtUtil.generateToken(username);
        return ResponseEntity.ok().body(token);
    }
}
