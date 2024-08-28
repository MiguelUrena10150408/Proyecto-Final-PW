package org.example.services;

import org.example.config.MongoConfig;
import org.example.models.Form;
import org.example.models.User;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.experimental.filters.Filters;

import java.util.List;

public class FormService {

    private final Datastore datastore;

    // Constructor que recibe el Datastore de Morphia
    public FormService() {
        this.datastore = MongoConfig.getDatastore();;
    }

    /**
     * Método para crear y guardar un nuevo formulario en la base de datos.
     *
     * @param name          Nombre capturado en el formulario
     * @param sector        Sector capturado en el formulario
     * @param schoolLevel   Nivel escolar capturado en el formulario
     * @param geoPosition   Posición geográfica donde se realizó el registro
     * @param imageBase64   Imagen capturada y almacenada como base64
     * @param submittedBy   Usuario que envió el formulario
     * @return El formulario creado y guardado
     */
    public Form createForm(String name, String sector, String schoolLevel, String geoPosition, String imageBase64, User submittedBy) {
        Form form = new Form(name, sector, schoolLevel, geoPosition, imageBase64, submittedBy);
        datastore.save(form);
        return form;
    }

    /**
     * Método para buscar un formulario por su ID.
     *
     * @param id Identificador del formulario
     * @return El formulario encontrado o null si no existe
     */
    public Form findFormById(String id) {
        return datastore.find(Form.class).filter(Filters.eq("_id", id)).first();
    }

    /**
     * Método para obtener todos los formularios enviados por un usuario específico.
     *
     * @param username Nombre de usuario
     * @return Lista de formularios enviados por el usuario
     */
    public List<Form> getFormsByUser(String username) {
        Query<Form> query = datastore.find(Form.class).filter(Filters.eq("submittedBy.username", username));
        return query.iterator().toList();
    }

    /**
     * Método para actualizar un formulario existente.
     *
     * @param form El objeto formulario con la información actualizada
     */
    public void updateForm(Form form) {
        datastore.save(form);
    }

    /**
     * Método para eliminar un formulario de la base de datos por su ID.
     *
     * @param id Identificador del formulario a eliminar
     */
    public void deleteForm(String id) {
        Query<Form> query = datastore.find(Form.class).filter(Filters.eq("_id", id));
        datastore.delete(query.first());
    }

    /**
     * Método para obtener todos los formularios almacenados en la base de datos.
     *
     * @return Lista de todos los formularios
     */
    public List<Form> getAllForms() {
        return datastore.find(Form.class).iterator().toList();
    }

    /**
     * Método para obtener todos los formularios junto con su información geográfica.
     *
     * @return Lista de formularios con información de geolocalización
     */
    public List<Form> getAllFormsWithGeoPosition() {
        Query<Form> query = datastore.find(Form.class).filter(Filters.exists("geoPosition"));
        return query.iterator().toList();
    }
}