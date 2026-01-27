package com.biblioteck.api.controller;

import com.biblioteck.api.model.User;
import com.biblioteck.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
// Asegúrate de que este puerto sea el que usa tu navegador para abrir Vue
@CrossOrigin(origins = "*") 
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        // Log para depuración: Verás esto en la terminal de Java
        System.out.println("Intentando login para: " + username);

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                System.out.println("✅ Login exitoso para: " + username + " con rol: " + user.getRole());
                return ResponseEntity.ok(Map.of(
                    "username", user.getUsername(),
                    "role", user.getRole()
                ));
            } else {
                System.out.println("❌ Contraseña incorrecta para: " + username);
            }
        } else {
            System.out.println("❓ Usuario no encontrado en MongoDB: " + username);
        }

        return ResponseEntity.status(401).body(Map.of("message", "Credenciales incorrectas"));
    }
}