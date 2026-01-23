<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';

// --- IMPORTACIÓN DE COMPONENTES ---
import BookStats from './components/BookStats.vue';
import BookTable from './components/BookTable.vue';
import BookForm from './components/BookForm.vue';
import BookModal from './components/BookModal.vue';
import BookCard from './components/BookCard.vue';

const API_BASE_URL = 'http://localhost:8080/api/books';

const libros = ref([]);
const filtro = ref('');
const libroSeleccionado = ref(null);
const vistaGrid = ref(true);

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
        <div class="view-toggle">
          <button @click="vistaGrid = true" :class="{ active: vistaGrid }" title="Vista Cuadrícula">🖼️</button>
          <button @click="vistaGrid = false" :class="{ active: !vistaGrid }" title="Vista Lista">📋</button>
        </div>

        <div class="search-container">
          <input 
            v-model="filtro" 
            type="text" 
            placeholder="Buscar por título o autor..." 
            class="minimal-search"
          />
          <span class="search-icon">🔍</span>
        </div>
        <button @click="obtenerLibros" class="btn-sync" title="Actualizar">🔄</button>
      </div>
    </header>

    <main class="app-content">
      <div class="stats-section">
        <BookStats :total="totalLibros" :autores="autoresUnicos" />
      </div>

      <div class="form-section">
        <BookForm :baseUrl="API_BASE_URL" @libroGuardado="obtenerLibros" />
      </div>

      <section class="display-section">
        <h2 class="section-title">
          {{ vistaGrid ? '📚 Catálogo de Novedades' : '⚙️ Gestión de Inventario' }}
        </h2>

        <div v-if="vistaGrid" class="books-grid">
          <BookCard 
            v-for="libro in librosFiltrados" 
            :key="libro.id" 
            :libro="libro"
            @ver="l => libroSeleccionado = l"
          />
        </div>

        <div v-else class="table-container-wrapper">
          <BookTable 
            :libros="librosFiltrados" 
            :baseUrl="API_BASE_URL"
            @eliminar="eliminarLibro"
            @ver="l => libroSeleccionado = l"
            @actualizar="obtenerLibros"
          />
        </div>
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
/* 1. RESET Y VARIABLES (ELIMINA LO ANTERIOR) */
:root {
  --bg-main: #0f1115;      /* Fondo total */
  --bg-card: #181a1f;      /* Fondo secciones */
  --bg-input: #21252b;     /* Fondo inputs */
  --text-main: #f8f9fa;    /* Texto */
  --text-muted: #9ba3af;
  --accent: #bb86fc;       /* Púrpura Pro */
  --border: #2d333d;
}

html, body {
  margin: 0;
  padding: 0;
  background-color: var(--bg-main) !important;
  color: var(--text-main);
  font-family: 'Inter', system-ui, sans-serif;
  min-height: 100vh;
}

/* 2. LAYOUT PRINCIPAL */
.biblioteck-app {
  min-height: 100vh;
  background-color: var(--bg-main);
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

.header-left h1 { font-size: 1.2rem; color: var(--accent); margin: 0; }

.header-right { display: flex; gap: 1rem; align-items: center; }

/* 3. COMPONENTES DE INTERFAZ */
.view-toggle {
  display: flex;
  background: var(--bg-input);
  border-radius: 8px;
  padding: 4px;
  border: 1px solid var(--border);
}

.view-toggle button {
  background: none;
  border: none;
  padding: 6px 14px;
  cursor: pointer;
  border-radius: 6px;
  transition: 0.3s;
  filter: grayscale(1);
}

.view-toggle button.active {
  background: var(--accent);
  filter: grayscale(0);
}

.minimal-search {
  background: var(--bg-input);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 8px 12px 8px 35px;
  color: white;
  width: 250px;
}

/* 4. CONTENIDO */
.app-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 2.5rem;
}

.section-title {
  color: var(--accent);
  font-size: 1.3rem;
  margin-bottom: 1.5rem;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 25px;
}

/* 5. FORZAR ESTILOS EN COMPONENTES HIJOS */
.form-card, .table-container, .stats-card {
  background: var(--bg-card) !important;
  border: 1px solid var(--border) !important;
  border-radius: 12px !important;
  box-shadow: 0 4px 15px rgba(0,0,0,0.2) !important;
}
</style>