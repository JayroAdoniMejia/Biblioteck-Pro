package com.biblioteck.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "books") // Esto creará la colección automáticamente en Mongo
public class Book {
    @Id
    private String id; // Es recomendable usar 'id' para que Spring Data lo mapee automáticamente con '_id' de Mongo
    
    private String title;
    private String author;
    private String category;    // Ej: Historia, Ciencia, Filosofía
    private String pdfUrl;      // Link al archivo para el lector PWA
    private String imageUrl;    // Nueva propiedad para la portada del libro
    private Integer year;
    private String description;
}