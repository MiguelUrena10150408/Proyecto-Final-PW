package org.example.controllers;

import org.example.models.Form;
import org.example.models.User;
import org.example.services.FormService;
import org.example.services.UserService;
import org.example.config.MongoConfig;
import io.javalin.http.Context;
import io.javalin.websocket.WsContext;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FormController {

    private static final FormService formService;
    private static final UserService userService;
    private static final Gson gson = new Gson();

    // Mapa para almacenar las conexiones WebSocket
    private static final Map<WsContext, User> connectedClients = new ConcurrentHashMap<>();

    static {
        // Inicialización de servicios utilizando el Datastore configurado en MongoConfig
        formService = new FormService();
        userService = new UserService();
    }

    /**
     * Maneja la creación y el envío de un formulario desde la solicitud HTTP.
     *
     * @param ctx El contexto de Javalin que contiene la solicitud HTTP
     */
    public static void submitForm(Context ctx) {
        String name = ctx.formParam("name");
        String sector = ctx.formParam("sector");
        String schoolLevel = ctx.formParam("schoolLevel");
        String geoPosition = ctx.formParam("geoPosition");
        String imageBase64 = ctx.formParam("imageBase64");
        String username = ctx.sessionAttribute("currentUser");

        if (username != null) {
            User user = userService.findUserByUsername(username);
            if (user != null) {
                Form form = formService.createForm(name, sector, schoolLevel, geoPosition, imageBase64, user);
                ctx.json(form);
                ctx.status(201);
                broadcastForm(form);  // Transmitir a través de WebSocket
            } else {
                ctx.status(404).result("Usuario no encontrado.");
            }
        } else {
            ctx.status(401).result("No autenticado.");
        }
    }

    /**
     * Muestra todos los formularios en un mapa.
     *
     * @param ctx El contexto de Javalin que contiene la solicitud HTTP
     */
    public static void showMap(Context ctx) {
        List<Form> formsWithGeo = formService.getAllFormsWithGeoPosition();
        ctx.render("/templates/map.html", Map.of("forms", formsWithGeo));
    }

    /**
     * Maneja la recepción de mensajes WebSocket para sincronización de datos.
     *
     * @param ctx El contexto de WebSocket
     * @param message El mensaje recibido desde el cliente WebSocket
     */
    public static void handleWebSocketMessage(WsContext ctx, String message) {
        User user = connectedClients.get(ctx);
        if (user != null) {
            Form form = gson.fromJson(message, Form.class);
            form.setSubmittedBy(user);
            formService.updateForm(form);
            broadcastForm(form);  // Transmitir actualización a todos los clientes conectados
        } else {
            ctx.send("Usuario no autenticado para sincronización.");
        }
    }

    /**
     * Maneja la conexión de nuevos clientes WebSocket.
     *
     * @param ctx El contexto de WebSocket
     */
    public static void handleWebSocketConnect(WsContext ctx) {
        String username = ctx.sessionAttribute("currentUser");
        if (username != null) {
            User user = userService.findUserByUsername(username);
            connectedClients.put(ctx, user);
            ctx.send("Conectado como " + username);
        } else {
            ctx.send("Usuario no autenticado.");
            ctx.session.close();
        }
    }

    /**
     * Maneja la desconexión de clientes WebSocket.
     *
     * @param ctx El contexto de WebSocket
     */
    public static void handleWebSocketClose(WsContext ctx) {
        connectedClients.remove(ctx);
    }

    /**
     * Método para transmitir un formulario a todos los clientes conectados a través de WebSocket.
     *
     * @param form El formulario que se enviará a todos los clientes
     */
    private static void broadcastForm(Form form) {
        String formJson = gson.toJson(form);
        connectedClients.keySet().forEach(ws -> ws.send(formJson));
    }

    /**
     * Método para listar todos los formularios de un usuario específico.
     *
     * @param ctx El contexto de Javalin que contiene la solicitud HTTP
     */
    public static void listFormsByUser(Context ctx) {
        String username = ctx.pathParam("username");
        List<Form> userForms = formService.getFormsByUser(username);
        if (!userForms.isEmpty()) {
            ctx.json(userForms);
        } else {
            ctx.status(404).result("No se encontraron formularios para este usuario.");
        }
    }

    /**
     * Método para eliminar un formulario por su ID.
     *
     * @param ctx El contexto de Javalin que contiene la solicitud HTTP
     */
    public static void deleteForm(Context ctx) {
        String formId = ctx.pathParam("id");
        Form form = formService.findFormById(formId);
        if (form != null) {
            formService.deleteForm(formId);
            ctx.status(200).result("Formulario eliminado con éxito.");
            broadcastFormDeletion(formId);  // Notificar eliminación a través de WebSocket
        } else {
            ctx.status(404).result("Formulario no encontrado.");
        }
    }

    /**
     * Método para notificar a todos los clientes conectados que un formulario ha sido eliminado.
     *
     * @param formId El ID del formulario eliminado
     */
    private static void broadcastFormDeletion(String formId) {
        String message = gson.toJson(Map.of("action", "delete", "id", formId));
        connectedClients.keySet().forEach(ws -> ws.send(message));
    }
}
