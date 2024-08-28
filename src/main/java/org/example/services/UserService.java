package org.example.services;

import org.example.config.MongoConfig;
import org.example.models.User;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.experimental.filters.Filters;

import java.util.List;

public class UserService {

    private final Datastore datastore;

    // Constructor que recibe el Datastore de Morphia
    public UserService() {
        this.datastore = MongoConfig.getDatastore();
    }

    /**
     * Método para crear y guardar un nuevo usuario en la base de datos.
     *
     * @param username Nombre de usuario
     * @param password Contraseña del usuario
     * @param role     Rol del usuario (admin, user, etc.)
     * @return El usuario creado y guardado
     */
    public User createUser(String username, String password, String role) {
        User user = new User(username, password, role);
        datastore.save(user);
        return user;
    }

    /**
     * Método para buscar un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario
     * @return El usuario encontrado o null si no existe
     */
    public User findUserByUsername(String username) {
        Query<User> query = datastore.find(User.class).filter(Filters.eq("username", username));
        return query.first();
    }

    /**
     * Método para actualizar la información de un usuario existente.
     *
     * @param user El objeto usuario con la información actualizada
     */
    public void updateUser(User user) {
        datastore.save(user);
    }

    /**
     * Método para eliminar un usuario de la base de datos.
     *
     * @param username Nombre de usuario del usuario a eliminar
     */
    public void deleteUser(String username) {
        Query<User> query = datastore.find(User.class).filter(Filters.eq("username", username));
        datastore.delete(query.first());
    }

    /**
     * Método para obtener una lista de todos los usuarios.
     *
     * @return Lista de usuarios
     */
    public List<User> getAllUsers() {
        return datastore.find(User.class).iterator().toList();
    }

    /**
     * Método para autenticar un usuario mediante su nombre de usuario y contraseña.
     *
     * @param username Nombre de usuario
     * @param password Contraseña del usuario
     * @return El usuario autenticado o null si no coincide
     */
    public User authenticateUser(String username, String password) {
        // Ensure the query is correctly matching the user's credentials
        Query<User> query = datastore.find(User.class).filter(Filters.eq("username", username), Filters.eq("password", password));; // Consider using hashing for passwords

        return query.first();
    }
}
