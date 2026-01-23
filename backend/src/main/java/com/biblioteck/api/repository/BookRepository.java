package com.biblioteck.api.repository;

import com.biblioteck.api.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    
    /**
     * Verifica si existe un libro comparando título y autor.
     * IgnoreCase permite que coincidan aunque varíen mayúsculas y minúsculas.
     */
    boolean existsByTitleIgnoreCaseAndAuthorIgnoreCase(String title, String author);

    /**
     * Búsqueda Pro: Filtra libros que contengan el texto en el título, autor o categoría.
     * Ideal para el buscador dinámico de Biblioteck Pro.
     */
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(
        String title, 
        String author, 
        String category
    );
}