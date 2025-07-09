package com.uca.parcialfinalncapas.controller;

import com.uca.parcialfinalncapas.dto.AuthenticationRequest;
import com.uca.parcialfinalncapas.dto.AuthenticationResponse;
import com.uca.parcialfinalncapas.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        String token = jwtUtil.generateToken(req.getUsername());
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}