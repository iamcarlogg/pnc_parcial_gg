package com.uca.parcialfinalncapas.controller;

import com.uca.parcialfinalncapas.dto.AuthenticationRequest;
import com.uca.parcialfinalncapas.dto.AuthenticationResponse;
import com.uca.parcialfinalncapas.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationConfiguration authConfig;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationConfiguration authConfig, JwtUtil jwtUtil) {
        this.authConfig = authConfig;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest req) throws Exception {
        // Obtenemos el AuthenticationManager justo a tiempo
        AuthenticationManager authManager = authConfig.getAuthenticationManager();

        // Autenticamos las credenciales
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        // Generamos el JWT sobre el nombre de usuario validado
        String token = jwtUtil.generateToken(auth.getName());
        return new AuthenticationResponse(token);
    }
}
