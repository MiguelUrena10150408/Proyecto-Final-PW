package org.example.controllers;

import org.example.models.User;
import org.example.services.UserService;
import io.javalin.http.Context;

public class UserController {

    private static final UserService userService = new UserService(); // Inicialización directa del servicio

    public static void loginPage(Context ctx) {
        ctx.render("/templates/login.html");
    }

    public static void login(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        if (username != null && password != null) {
            User user = userService.authenticateUser(username, password);
            if (user != null) {
                ctx.sessionAttribute("currentUser", username);
                ctx.redirect("/");
            } else {
                ctx.status(401).result("Nombre de usuario o contraseña incorrectos.");
            }
        } else {
            ctx.status(400).result("Por favor, ingrese tanto el nombre de usuario como la contraseña.");
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
