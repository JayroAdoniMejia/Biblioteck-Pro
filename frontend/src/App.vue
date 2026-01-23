<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';

// --- IMPORTACIÓN DE COMPONENTES ---
import BookStats from './components/BookStats.vue';
import BookTable from './components/BookTable.vue';
import BookForm from './components/BookForm.vue';
import BookModal from './components/BookModal.vue';

const API_BASE_URL = 'http://localhost:8080/api/books';

const libros = ref([]);
const filtro = ref('');
const libroSeleccionado = ref(null);

const librosFiltrados = computed(() => {
  if (!filtro.value) return libros.value;
  const search = filtro.value.toLowerCase();
  return libros.value.filter(l => 
    l.title.toLowerCase().includes(search) || 
    l.author.toLowerCase().includes(search)
  );
});

const totalLibros = computed(() => librosFiltrados.value.length);
const autoresUnicos = computed(() => new Set(librosFiltrados.value.map(l => l.author)).size);

const obtenerLibros = async () => {
  try {
    const response = await axios.get(API_BASE_URL);
    libros.value = response.data;
  } catch (error) { console.error(error); }
};

const eliminarLibro = async (id) => {
  if (!confirm('⚠️ ¿Eliminar libro y PDF permanentemente?')) return;
  try {
    await axios.delete(`${API_BASE_URL}/${id}`);
    obtenerLibros();
  } catch (error) { alert('Error al eliminar'); }
};

onMounted(obtenerLibros);
</script>

<template>
  <div class="biblioteck-app">
    <header class="app-header">
      <div class="header-left">
        <div class="logo-container">
          <span class="logo-icon">📘</span>
          <h1>Biblioteck Pro</h1>
        </div>
      </div>
      
      <div class="header-right">
        <div class="search-container">
          <input 
            v-model="filtro" 
            type="text" 
            placeholder="Buscar por título o autor..." 
            class="minimal-search"
          />
          <span class="search-icon">🔍</span>
        </div>
        <button @click="obtenerLibros" class="btn-sync" title="Actualizar">
          🔄
        </button>
      </div>
    </header>

    <main class="app-content">
      <div class="stats-grid">
        <BookStats :total="totalLibros" :autores="autoresUnicos" />
      </div>

      <section class="main-sections">
        <BookForm :baseUrl="API_BASE_URL" @libroGuardado="obtenerLibros" />
        <BookTable 
          :libros="librosFiltrados" 
          :baseUrl="API_BASE_URL"
          @eliminar="eliminarLibro"
          @ver="l => libroSeleccionado = l"
          @actualizar="obtenerLibros"
        />
      </section>

      <BookModal 
        v-if="libroSeleccionado"
        :libro="libroSeleccionado" 
        :baseUrl="API_BASE_URL" 
        @cerrar="libroSeleccionado = null" 
      />
    </main>
  </div>
</template>

<style>
/* PALETA DE COLORES MINIMALISTA */
:root {
  --bg-main: #121418;       /* Fondo oscuro profundo */
  --bg-card: #1c1f26;       /* Fondo de secciones */
  --bg-input: #252a33;      /* Fondo de inputs */
  --text-main: #e0e0e0;     /* Texto principal */
  --text-muted: #94a3b8;    /* Texto secundario */
  --accent: #3b82f6;        /* Azul profesional (similar a tu imagen) */
  --border: #2d333d;        /* Bordes sutiles */
}

body {
  margin: 0;
  background-color: var(--bg-main);
  color: var(--text-main);
  font-family: 'Inter', -apple-system, sans-serif;
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left .logo-container { display: flex; align-items: center; gap: 10px; }
.logo-icon { font-size: 1.5rem; }
.header-left h1 { font-size: 1.2rem; font-weight: 600; margin: 0; }

.header-right { display: flex; gap: 1rem; align-items: center; }

/* BUSCADOR ESTILO IMAGEN GENERADA */
.search-container {
  position: relative;
  width: 300px;
}

.minimal-search {
  width: 100%;
  background: var(--bg-input);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 8px 12px 8px 35px;
  color: white;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.minimal-search:focus {
  border-color: var(--accent);
  outline: none;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

.search-icon {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.8rem;
  opacity: 0.5;
}

.btn-sync {
  background: none;
  border: 1px solid var(--border);
  color: white;
  padding: 6px 10px;
  border-radius: 6px;
  cursor: pointer;
}

/* LAYOUT DE CONTENIDO */
.app-content {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1rem;
  display: flex;
  flex-direction: column;
  gap: 2rem; /* Evita que las cosas estén una sobre otra */
}

.main-sections {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* Ajuste para que los componentes hijos usen la nueva paleta */
.form-box, .stats-container, .table-container {
  background: var(--bg-card) !important;
  border: 1px solid var(--border) !important;
  border-radius: 8px !important;
}
</style>