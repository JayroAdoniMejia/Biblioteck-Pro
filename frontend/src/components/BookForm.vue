<script setup>
import { ref } from 'vue';
import axios from 'axios';

// Definición de Props y Emits
const props = defineProps(['baseUrl']);
const emit = defineEmits(['libroGuardado']);

// 1. Lista de categorías consistente con AdminPanel
const categoriasDisponibles = ['General', 'Programación', 'Historia', 'Medicina', 'Diseño', 'Sistemas', 'Ciencia'];

// Estado del Formulario
const nuevoLibro = ref({ 
  title: '', 
  author: '', 
  category: 'General', 
  year: 2026, 
  description: '',
  imageUrl: '' 
});

const archivoSeleccionado = ref(null);
const fileInput = ref(null);
const cargando = ref(false);
const mensajeEstado = ref('');
const tipoMensaje = ref('');

// Lógica de Archivos
const onFileSelected = (event) => {
  const file = event.target.files[0];
  if (file) {
    archivoSeleccionado.value = file;
    mensajeEstado.value = '';
  }
};

const truncarNombre = (nombre) => {
  return nombre.length > 20 ? nombre.substring(0, 17) + '...' : nombre;
};

// Envío a MongoDB
const enviarFormulario = async () => {
  cargando.value = true;
  mensajeEstado.value = '';
  
  const formData = new FormData();
  formData.append('title', nuevoLibro.value.title);
  formData.append('author', nuevoLibro.value.author);
  formData.append('category', nuevoLibro.value.category);
  formData.append('year', nuevoLibro.value.year);
  formData.append('description', nuevoLibro.value.description);
  formData.append('imageUrl', nuevoLibro.value.imageUrl); 
  formData.append('file', archivoSeleccionado.value);

  try {
    await axios.post(`${props.baseUrl}/upload`, formData);
    tipoMensaje.value = 'success';
    mensajeEstado.value = '✅ ¡Libro guardado en la base de datos!';
    
    setTimeout(() => {
      limpiarFormulario();
      emit('libroGuardado');
    }, 1500);

  } catch (error) {
    tipoMensaje.value = 'error';
    mensajeEstado.value = error.response?.status === 409 
      ? '⚠️ ' + error.response.data 
      : '❌ Error al conectar con el servidor.';
  } finally {
    cargando.value = false;
  }
};

const limpiarFormulario = () => {
  nuevoLibro.value = { title: '', author: '', category: 'General', year: 2026, description: '', imageUrl: '' };
  archivoSeleccionado.value = null;
  mensajeEstado.value = '';
  if (fileInput.value) fileInput.value.value = '';
};
</script>

<template>
  <section class="form-card">
    <div class="form-header">
      <div class="header-main">
        <span class="header-icon">🟣</span>
        <h3>Registrar Nuevo Libro</h3>
      </div>
      <p class="subtitle">Añade documentos PDF con portadas y persistencia en MongoDB</p>
    </div>

    <div class="form-grid">
      <div class="field-group">
        <label class="accent-label">Título del Libro</label>
        <input v-model="nuevoLibro.title" placeholder="Ej: Clean Code" class="input-minimal" />
      </div>

      <div class="field-group">
        <label class="accent-label">Autor</label>
        <input v-model="nuevoLibro.author" placeholder="Nombre del autor" class="input-minimal" />
      </div>

      <div class="field-group full-width">
        <label class="accent-label">URL de la Portada (Opcional)</label>
        <input v-model="nuevoLibro.imageUrl" placeholder="https://ejemplo.com/imagen.jpg" class="input-minimal" />
      </div>

      <div class="field-group">
        <label class="accent-label">Año de Publicación</label>
        <input v-model.number="nuevoLibro.year" type="number" class="input-minimal" />
      </div>
      
      <div class="field-group">
        <label class="accent-label">Categoría</label>
        <select v-model="nuevoLibro.category" class="input-minimal select-custom">
          <option v-for="cat in categoriasDisponibles" :key="cat" :value="cat">
            {{ cat }}
          </option>
        </select>
      </div>

      <div class="field-group full-width">
        <label class="accent-label">Descripción</label>
        <textarea v-model="nuevoLibro.description" placeholder="Resumen del contenido..." class="input-minimal area-dark"></textarea>
      </div>
      
      <div v-if="mensajeEstado" :class="['status-box', tipoMensaje]" class="full-width">
        {{ mensajeEstado }}
      </div>

      <div class="form-actions">
        <input type="file" ref="fileInput" @change="onFileSelected" accept="application/pdf" hidden />
        
        <button @click="fileInput.click()" class="btn-outline-purple">
          {{ archivoSeleccionado ? '✅ ' + truncarNombre(archivoSeleccionado.name) : '📄 Adjuntar PDF' }}
        </button>

        <button 
          @click="enviarFormulario" 
          :disabled="!nuevoLibro.title || !nuevoLibro.author || !archivoSeleccionado || cargando" 
          class="btn-solid-purple"
        >
          <span v-if="cargando">⏳ Guardando...</span>
          <span v-else>☁️ Guardar en Mongo</span>
        </button>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* Se mantienen tus estilos base y se añade la personalización del select */
.select-custom {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' fill='%23bb86fc' viewBox='0 0 16 16'%3E%3Cpath d='M7.247 11.14 2.451 5.658C1.885 5.013 2.345 4 3.204 4h9.592a1 1 0 0 1 .753 1.659l-4.796 5.48a1 1 0 0 1-1.506 0z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: calc(100% - 15px) center;
}

.form-card { 
  background: var(--bg-card); 
  padding: 35px; 
  border-radius: 12px; 
  border: 1px solid var(--border);
  margin-bottom: 2.5rem;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

.form-header { margin-bottom: 30px; border-left: 4px solid #bb86fc; padding-left: 20px; }
.header-main { display: flex; align-items: center; gap: 10px; }
.header-main h3 { margin: 0; font-size: 1.4rem; font-weight: 700; color: #fff; }
.subtitle { color: var(--text-muted); font-size: 0.85rem; margin-top: 6px; }

.form-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
.field-group { display: flex; flex-direction: column; gap: 8px; }

.accent-label { 
  font-size: 0.75rem; 
  color: #bb86fc; 
  font-weight: 700; 
  text-transform: uppercase; 
  letter-spacing: 1px;
}

.input-minimal { 
  background: var(--bg-input); 
  border: 1px solid var(--border); 
  color: white; 
  padding: 12px 16px; 
  border-radius: 8px; 
  outline: none;
  font-size: 0.95rem;
  transition: all 0.3s ease;
}

.input-minimal:focus { 
  border-color: #bb86fc; 
  box-shadow: 0 0 0 3px rgba(187, 134, 252, 0.1);
}

.full-width { grid-column: span 2; }
.area-dark { min-height: 80px; resize: none; }

.status-box {
  padding: 12px;
  border-radius: 8px;
  font-size: 0.85rem;
  text-align: center;
}
.success { background: rgba(0, 255, 127, 0.1); color: #00ff7f; border: 1px solid rgba(0, 255, 127, 0.3); }
.error { background: rgba(255, 69, 58, 0.1); color: #ff453a; border: 1px solid rgba(255, 69, 58, 0.3); }

.form-actions { 
  grid-column: span 2; 
  display: flex; 
  gap: 15px; 
  justify-content: flex-end; 
  margin-top: 10px;
}

.btn-outline-purple { 
  background: transparent; 
  border: 2px solid #bb86fc; 
  color: #bb86fc; 
  padding: 10px 20px; 
  border-radius: 8px; 
  cursor: pointer; 
  font-weight: 600;
  transition: 0.3s;
}
.btn-outline-purple:hover { background: rgba(187, 134, 252, 0.1); }

.btn-solid-purple { 
  background: #bb86fc; 
  color: #121212; 
  border: none; 
  font-weight: 700;
  padding: 12px 25px; 
  border-radius: 8px; 
  cursor: pointer; 
  transition: 0.3s;
}
.btn-solid-purple:hover:not(:disabled) { 
  background: #cfabff; 
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(187, 134, 252, 0.3);
}
.btn-solid-purple:disabled { background: #333; color: #777; cursor: not-allowed; }
</style>