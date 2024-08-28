package org.example.config;

import org.example.models.Form;
import org.example.models.User;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

public class MongoConfig {

    private static final String CONNECTION_STRING = "mongodb+srv://miguelura1:mongoDB28@cluster0.utxuopz.mongodb.net/"; // URL de conexión de MongoDB
    private static final String DATABASE_NAME = "ProyectoDFinal"; // Nombre de la base de datos

    private static Datastore datastore;

    /**
     * Método estático para inicializar y obtener la instancia de Datastore.
     *
     * @return Instancia de Datastore configurada
     */
    public static Datastore getDatastore() {
        if (datastore == null) {
            MongoClient mongoClient = MongoClients.create(CONNECTION_STRING); // Crear el cliente de MongoDB
            datastore = Morphia.createDatastore(mongoClient, DATABASE_NAME); // Crear el Datastore de Morphia
            datastore.getMapper().map(User.class, Form.class); // Mapear las clases de modelo
            datastore.ensureIndexes(); // Asegurar índices de la base de datos
        }
        return datastore;
    }

    /**
     * Método para cerrar la conexión de MongoDB.
     */
    public static void close() {
        if (datastore != null) {
            datastore.getSession().close(); // Cerrar el cliente de MongoDB
        }
    }
}

