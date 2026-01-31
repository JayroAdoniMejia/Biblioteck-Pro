<template>
  <section class="form-card">
    <div class="form-header-modern">
      <div class="icon-circle-container">
        <div class="outer-circle">
          <div class="inner-dot"></div>
        </div>
      </div>
      <div class="header-text">
        <h2 class="main-title">Registrar Nuevo Libro</h2>
        <p class="subtitle">Añade documentos PDF con portadas y persistencia en MongoDB</p>
      </div>
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
          📄 {{ archivoSeleccionado ? truncarNombre(archivoSeleccionado.name) : 'Adjuntar PDF' }}
        </button>

        <button 
          @click="enviarFormulario" 
          :disabled="!nuevoLibro.title || !nuevoLibro.author || !archivoSeleccionado || cargando" 
          class="btn-solid-adaptive"
        >
          <span v-if="cargando">⏳ Guardando...</span>
          <span v-else>☁️ Guardar en Mongo</span>
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const props = defineProps(['baseUrl']);
const emit = defineEmits(['libroGuardado']);

const categoriasDisponibles = ['General', 'Programación', 'Historia', 'Medicina', 'Diseño', 'Sistemas', 'Ciencia'];

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

<style scoped>
.form-card { 
  background: var(--bg-card); /* Usa el color del tema */
  padding: 40px; 
  border-radius: 20px; 
  border: 1px solid var(--border);
  margin-bottom: 2.5rem;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

/* --- ENCABEZADO --- */
.form-header-modern {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 35px;
}

.outer-circle {
  width: 50px;
  height: 50px;
  border: 4px solid #a855f7;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.inner-dot {
  width: 22px;
  height: 22px;
  background: #a855f7;
  border-radius: 50%;
}

.main-title {
  margin: 0;
  font-size: 2.2rem;
  font-weight: 800;
  color: #a855f7; /* Mantenemos el púrpura de la imagen */
  line-height: 1;
}

.subtitle {
  margin: 5px 0 0 0;
  color: var(--text-muted);
  font-size: 1rem;
}

/* --- INPUTS ADAPTATIVOS --- */
.form-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 25px; }
.field-group { display: flex; flex-direction: column; gap: 8px; }

.accent-label { 
  font-size: 0.8rem; 
  color: #c084fc; 
  font-weight: 800; 
  text-transform: uppercase; 
  letter-spacing: 1px;
}

.input-minimal { 
  background: var(--bg-input); 
  border: 2px solid var(--border); 
  color: var(--text-main) !important; /* IMPORTANTE para ver lo que escribes */
  padding: 14px 18px; 
  border-radius: 12px; 
  outline: none;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.input-minimal::placeholder {
  color: var(--text-muted);
  opacity: 0.6;
}

.input-minimal:focus { 
  border-color: #a855f7; 
  box-shadow: 0 0 0 4px rgba(168, 85, 247, 0.1);
}

.select-custom {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' fill='%23a855f7' viewBox='0 0 16 16'%3E%3Cpath d='M7.247 11.14 2.451 5.658C1.885 5.013 2.345 4 3.204 4h9.592a1 1 0 0 1 .753 1.659l-4.796 5.48a1 1 0 0 1-1.506 0z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: calc(100% - 15px) center;
}

.full-width { grid-column: span 2; }
.area-dark { min-height: 120px; resize: none; }

/* --- BOTONES ADAPTATIVOS --- */
.form-actions { 
  grid-column: span 2; 
  display: flex; 
  gap: 15px; 
  justify-content: flex-end; 
  margin-top: 15px;
}

.btn-outline-purple { 
  background: transparent; 
  border: 2px solid #a855f7; 
  color: #a855f7; 
  padding: 12px 24px; 
  border-radius: 12px; 
  cursor: pointer; 
  font-weight: 700;
  transition: 0.3s;
}

.btn-solid-adaptive { 
  background: var(--text-main); /* Negro en light, Blanco en dark */
  color: var(--bg-card); /* Contraste inverso */
  border: none; 
  font-weight: 700;
  padding: 12px 30px; 
  border-radius: 12px; 
  cursor: pointer; 
  transition: 0.3s;
}

.btn-solid-adaptive:hover:not(:disabled) { 
  opacity: 0.85;
  transform: translateY(-2px);
}

.btn-solid-adaptive:disabled { 
  background: var(--border); 
  color: var(--text-muted); 
  cursor: not-allowed; 
}

.status-box {
  padding: 12px;
  border-radius: 12px;
  font-weight: 600;
  text-align: center;
}
.success { background: rgba(34, 197, 94, 0.2); color: #4ade80; }
.error { background: rgba(239, 68, 68, 0.2); color: #f87171; }
</style>