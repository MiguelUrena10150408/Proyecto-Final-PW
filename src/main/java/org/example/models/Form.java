package org.example.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;

// Define la entidad "forms" en MongoDB
@Entity("forms")
public class Form {

    // Identificador único para cada formulario
    @Id
    private String id;

    // Nombre capturado en el formulario
    @Property("name")
    private String name;

    // Sector capturado en el formulario
    @Property("sector")
    private String sector;

    // Nivel escolar capturado en el formulario
    @Property("school_level")
    private String schoolLevel;

    // Posición geográfica donde se realizó el registro
    @Property("geo_position")
    private String geoPosition;

    // Imagen capturada y almacenada como base64
    @Property("image_base64")
    private String imageBase64;

    // Usuario que envió el formulario
    @Reference
    private User submittedBy;

    // Constructor por defecto
    public Form() {}

    // Constructor con parámetros
    public Form(String name, String sector, String schoolLevel, String geoPosition, String imageBase64, User submittedBy) {
        this.name = name;
        this.sector = sector;
        this.schoolLevel = schoolLevel;
        this.geoPosition = geoPosition;
        this.imageBase64 = imageBase64;
        this.submittedBy = submittedBy;
    }

    // Getter y Setter para ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter y Setter para Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter y Setter para Sector
    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    // Getter y Setter para SchoolLevel
    public String getSchoolLevel() {
        return schoolLevel;
    }

    public void setSchoolLevel(String schoolLevel) {
        this.schoolLevel = schoolLevel;
    }

    // Getter y Setter para GeoPosition
    public String getGeoPosition() {
        return geoPosition;
    }

    public void setGeoPosition(String geoPosition) {
        this.geoPosition = geoPosition;
    }

    // Getter y Setter para ImageBase64
    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    // Getter y Setter para SubmittedBy (User)
    public User getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(User submittedBy) {
        this.submittedBy = submittedBy;
    }

    @Override
    public String toString() {
        return "Form{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sector='" + sector + '\'' +
                ", schoolLevel='" + schoolLevel + '\'' +
                ", geoPosition='" + geoPosition + '\'' +
                ", submittedBy=" + submittedBy +
                '}';
    }
}
