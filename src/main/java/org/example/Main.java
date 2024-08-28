package org.example;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.example.controllers.FormController;
import org.example.controllers.UserController;
import io.javalin.websocket.WsContext;
import io.javalin.rendering.template.JavalinThymeleaf;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

        public static void main(String[] args) {
            Javalin app = Javalin.create(config -> {
                config.staticFiles.add("/templates", Location.CLASSPATH);
                config.staticFiles.add("/public", Location.CLASSPATH);
                config.fileRenderer(new JavalinThymeleaf());
            }).start(7070);

            // Configuración de rutas HTTP
            app.get("/", ctx -> ctx.render("/templates/index.html"));
            app.get("/login", UserController::loginPage);
            app.post("/login", UserController::login);
            app.get("/register", UserController::registerPage);
            app.post("/register", UserController::registerUser);
            app.post("/submitForm", FormController::submitForm);
            app.get("/map", FormController::showMap);
            app.get("/user/forms/{username}", FormController::listFormsByUser);
            app.delete("/form/{id}", FormController::deleteForm);
            app.get("/form", FormController::showForm);
            app.get("/listSubmissions", FormController::listSubmissions);

            // Configuración de WebSocket
            app.ws("/ws", ws -> {
                ws.onConnect(FormController::handleWebSocketConnect);
                ws.onMessage(ctx -> FormController.handleWebSocketMessage(ctx, ctx.message()));
                ws.onClose(FormController::handleWebSocketClose);
            });


        }
}