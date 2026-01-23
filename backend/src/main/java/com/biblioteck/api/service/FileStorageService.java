package com.biblioteck.api.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public String save(MultipartFile file) throws IOException {
        // Si la carpeta no existe, la crea
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }
        
        // Genera un nombre único
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), this.root.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        
        return fileName;
    }

    /**
     * NUEVO MÉTODO: Carga el archivo para enviarlo al navegador (Download)
     */
    public Resource load(String fileName) {
        try {
            Path file = root.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se pudo leer el archivo: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error en la URL del archivo: " + e.getMessage());
        }
    }

    /**
     * Elimina el archivo físico de la carpeta /uploads
     */
    public void delete(String fileName) {
        try {
            Path fileToDelete = this.root.resolve(fileName);
            boolean deleted = Files.deleteIfExists(fileToDelete);
            
            if (deleted) {
                System.out.println("Archivo eliminado físicamente: " + fileName);
            } else {
                System.out.println("El archivo no existía en disco: " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Error al borrar el archivo " + fileName + ": " + e.getMessage());
        }
    }
}