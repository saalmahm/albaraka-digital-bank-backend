package com.albaraka.digital.controller;

import com.albaraka.digital.dto.auth.LoginRequest;
import com.albaraka.digital.dto.auth.LoginResponse;
import com.albaraka.digital.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor  // Lombok crée automatiquement le constructeur avec tous les champs final
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        // Authentification de l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Récupération des détails utilisateur
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Génération du token JWT
        String token = jwtService.generateToken(userDetails);

        // Retourner la réponse au client
        return new LoginResponse(token);
    }
}
