package com.biblioteck.api.controller;

import com.biblioteck.api.model.Book;
import com.biblioteck.api.repository.BookRepository;
import com.biblioteck.api.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:5173", methods = {
    RequestMethod.GET, 
    RequestMethod.POST, 
    RequestMethod.DELETE, 
    RequestMethod.OPTIONS,
    RequestMethod.PUT
})
public class BookController {

    private final BookRepository bookRepository;
    private final FileStorageService fileStorageService;

    public BookController(BookRepository bookRepository, FileStorageService fileStorageService) {
        this.bookRepository = bookRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    /**
     * Sube un libro incluyendo validación de duplicados y metadatos.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadBook(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("category") String category,
            @RequestParam("year") Integer year,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) throws Exception {
        
        // 1. VALIDACIÓN: Verificar si ya existe (Ignorando mayúsculas/minúsculas)
        if (bookRepository.existsByTitleIgnoreCaseAndAuthorIgnoreCase(title, author)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: Ya existe un libro registrado con el título '" + title + "' y autor '" + author + "'.");
        }
        
        // 2. Guardar archivo físico usando el servicio
        String fileName = fileStorageService.save(file);
        
        // 3. Mapear datos al modelo
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setYear(year);
        book.setDescription(description);
        book.setPdfUrl(fileName); 
        
        // 4. Persistir en MongoDB
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.ok(savedBook);
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Resource resource = fileStorageService.load(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            
            if (book.getPdfUrl() != null && !book.getPdfUrl().isEmpty()) {
                try {
                    fileStorageService.delete(book.getPdfUrl());
                } catch (Exception e) {
                    System.err.println("Error al borrar archivo: " + e.getMessage());
                }
            }
            
            bookRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        
        return ResponseEntity.notFound().build();
    }
}