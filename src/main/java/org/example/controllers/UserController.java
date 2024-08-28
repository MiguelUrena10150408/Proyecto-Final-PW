package org.example.controllers;

import org.example.models.User;
import org.example.services.UserService;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;

public class UserController {
    private static final UserService userService = new UserService(); // Inicialización directa del servicio

    public static void loginPage(Context ctx) {
        ctx.render("/templates/login.html");
    }

    public static void login(Context ctx) {
        String username = ctx.formParam("username").trim();
        String password = ctx.formParam("password").trim();

        if (username != null && password != null) {
            User user = userService.authenticateUser(username, password);
            if (user != null) {
                ctx.sessionAttribute("currentUser", username); // Establecer atributo de sesión para el usuario que ha iniciado sesión

                //Respuesta JSON que indica éxito
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("token", "dummy-token"); // Placeholder token
                ctx.json(result);
            } else {
                ctx.status(401).json(Map.of("success", false, "error", "Nombre de usuario o contraseña incorrectos."));
            }
        } else {
            ctx.status(400).json(Map.of("success", false, "error", "Por favor, ingrese tanto el nombre de usuario como la contraseña."));
        }
    }



    public static void registerUser(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        String role = ctx.formParam("role");

        if (username != null && password != null && role != null) {
            User existingUser = userService.findUserByUsername(username);
            if (existingUser == null) {
                userService.createUser(username, password, role);
                ctx.status(201).result("Usuario registrado con éxito.");
            } else {
                ctx.status(409).result("El nombre de usuario ya existe. Por favor elija otro.");
            }
        } else {
            ctx.status(400).result("Todos los campos son obligatorios.");
        }
    }

    public static void logout(Context ctx) {
        ctx.sessionAttribute("currentUser", null);
        ctx.redirect("/login");
    }

    public static void registerPage(Context ctx) {
        ctx.render("/templates/register.html");
    }
}
