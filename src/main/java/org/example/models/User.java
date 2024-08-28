package org.example.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

// Define la entidad users en MongoDB
@Entity("users")
public class User {

    // Identificador único para cada usuario
    @Id
    private String id;

    // Nombre de usuario
    @Property("username")
    private String username;

    // Contraseña del usuario
    @Property("password")
    private String password;

    // Rol del usuario (admin, user, etc.)
    @Property("role")
    private String role;

    // Constructor por defecto
    public User() {}

    // Constructor con parámetros
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter y Setter para ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter y Setter para Username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter y Setter para Password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter y Setter para Role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
