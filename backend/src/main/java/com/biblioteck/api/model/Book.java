package com.biblioteck.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "books") // Esto creará la colección automáticamente en Mongo
public class Book {
    @Id
    private String _id;
    private String title;
    private String author;
    private String category; // Ej: Historia, Ciencia, Filosofía
    private String pdfUrl;   // Link al archivo para el lector PWA
    private Integer year;
    private String description;
}