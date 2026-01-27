package com.biblioteck.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users") // Indica que se guardará en la colección "users" de MongoDB
public class User {
    
    @Id
    private String id;
    private String username;
    private String password;
    private String role; // Aquí guardarás "ADMIN" o "LECTOR"

    // Constructor vacío (necesario para Spring/MongoDB)
    public User() {}

    // Getters y Setters (los necesitas para que Java pueda leer y escribir los datos)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}