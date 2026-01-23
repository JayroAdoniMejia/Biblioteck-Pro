# 📚 Biblioteck Pro

**Biblioteck Pro** es un sistema moderno de gestión de documentos digitales diseñado para la organización eficiente de archivos PDF. El proyecto utiliza una arquitectura desacoplada con un backend robusto en **Java/Spring** y una interfaz de usuario minimalista y reactiva construida con **Vue.js**.

---

## 🛠️ Stack Tecnológico

* **Backend:** Java 17+ con Spring Boot.
* **Frontend:** Vue.js 3 (Composition API).
* **Base de Datos:** MongoDB (Persistencia NoSQL para metadatos).
* **Estilo:** CSS3 Minimalista con acentos en Púrpura.

---

## ✨ Características Principales

* **Gestión de Libros:** Registro completo de títulos, autores, años y categorías.
* **Almacenamiento PDF:** Subida y visualización de documentos directamente desde el servidor.
* **Buscador Inteligente:** Filtro en tiempo real por título, autor o descripción.
* **Interfaz Pro:** Diseño oscuro (Dark Mode) optimizado para reducir la fatiga visual, con iconos dinámicos por categoría.
* **Validación de Datos:** Control de duplicados y formatos de archivo permitidos.

---

## 🚀 Instalación y Configuración

### Requisitos Previos
* JDK 17 o superior.
* Node.js & npm.
* Instancia de MongoDB (Local o Atlas).

### Backend (Spring Boot)
1. Navega a la carpeta `/backend`.
2. Configura tu `application.properties` con las credenciales de MongoDB.
3. Ejecuta:
   ```bash
   ./mvnw spring-boot:run


   ## 💻 Frontend (Vue.js 3)


### 🚀 Características del Cliente aun por ajustar y mejorar
* **Framework:** Vue.js 3 con Composition API.


### 🛠️ Scripts Disponibles

En el directorio `/frontend`, puedes ejecutar:

```bash
# Instalar todas las dependencias (axios, vue, etc.)
npm install

# Compilar y ejecutar con Hot-Reload para desarrollo
npm run dev

# Compilar y minificar para producción
npm run build