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
const mostrarFormulario = ref(false);
const esModoClaro = ref(false);

// Función para el selector de pastilla del tema con persistencia visual
const aplicarTema = (claro) => {
  esModoClaro.value = claro;
  document.documentElement.setAttribute('data-theme', claro ? 'light' : 'dark');
};

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
      <div class="header-container">
        <div class="header-brand">
          <span class="logo-emoji">📘</span>
          <div class="brand-info">
            <h1>Biblioteck Pro</h1>
            <span class="version-tag">v2.0</span>
          </div>
        </div>
        
        <div class="header-actions">
          <div class="search-wrapper">
            <span class="search-icon">🔍</span>
            <input v-model="filtro" type="text" placeholder="Buscar en la biblioteca..." class="search-input" />
          </div>

          <div class="divider"></div>

          <div class="toggle-group">
            <div class="theme-pill">
              <button @click="aplicarTema(false)" :class="{ active: !esModoClaro }">Dark</button>
              <button @click="aplicarTema(true)" :class="{ active: esModoClaro }">Light</button>
            </div>
            
            <button @click="vistaGrid = !vistaGrid" class="icon-btn-toggle" :title="vistaGrid ? 'Ver como Lista' : 'Ver como Grid'">
              {{ vistaGrid ? '📋' : '🖼️' }}
            </button>
          </div>
        </div>
      </div>
    </header>

    <main class="app-content">
      <div class="stats-and-actions">
        <BookStats :total="totalLibros" :autores="autoresUnicos" />
        
        <button 
          @click="mostrarFormulario = !mostrarFormulario" 
          class="btn-primary-add"
          :class="{ active: mostrarFormulario }"
        >
          <span class="btn-icon">{{ mostrarFormulario ? '✕' : '＋' }}</span>
          {{ mostrarFormulario ? 'Cerrar Registro' : 'Nuevo Libro' }}
        </button>
      </div>

      <transition name="slide-fade">
        <div v-if="mostrarFormulario" class="form-container">
          <BookForm :baseUrl="API_BASE_URL" @libroGuardado="obtenerLibros" />
        </div>
      </transition>

      <section class="display-section">
        <div class="section-meta">
          <h2 class="section-title">
            {{ vistaGrid ? '📚 Catálogo' : '⚙️ Gestión' }}
          </h2>
          <div class="badge">{{ librosFiltrados.length }} títulos</div>
        </div>

        <div v-if="vistaGrid" class="books-grid">
          <BookCard 
            v-for="libro in librosFiltrados" 
            :key="libro.id" 
            :libro="libro"
            @ver="l => libroSeleccionado = l"
          />
        </div>

        <div v-else class="table-view">
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
/* 1. VARIABLES Y TEMAS MEJORADOS */
:root {
  /* DARK TOTAL: Negro profundo al estilo GitHub */
  --bg-main: #010409;
  --bg-header: #0d1117;
  --bg-card: #0d1117;
  --bg-input: #010409;
  --text-main: #8b949e;
  --text-bright: #f0f6fc;
  --accent: #8957e5;
  --accent-hover: #a371f7;
  --border: #30363d;
  --shadow: 0 8px 24px rgba(0,0,0,0.5);
}

[data-theme="light"] {
  --bg-main: #f6f8fa;
  --bg-header: #ffffff;
  --bg-card: #ffffff;
  --bg-input: #f3f4f6;
  --text-main: #24292f;
  --text-bright: #000000;
  --accent: #0969da;
  --accent-hover: #0a76f1;
  --border: #d0d7de;
  --shadow: 0 4px 12px rgba(0,0,0,0.05);
}

/* Reglas Globales para evitar bordes claros */
body {
  margin: 0;
  background-color: var(--bg-main);
  transition: background-color 0.3s ease;
}

/* 2. HEADER Y CONTROLES */
.app-header {
  background: var(--bg-header);
  border-bottom: 1px solid var(--border);
  padding: 0.8rem 0;
  position: sticky; top: 0; z-index: 1000;
}

.header-container {
  max-width: 1400px; margin: 0 auto; padding: 0 2rem;
  display: flex; justify-content: space-between; align-items: center;
}

.header-brand { display: flex; align-items: center; gap: 12px; }
.logo-emoji { font-size: 2rem; }
.brand-info h1 { font-size: 1.25rem; margin: 0; color: var(--text-bright); font-weight: 800; }
.version-tag { font-size: 0.7rem; color: var(--accent); font-weight: bold; }

.header-actions { display: flex; align-items: center; gap: 1.5rem; }

.search-wrapper {
  background: var(--bg-input);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 6px 12px;
  display: flex; align-items: center;
}
.search-input {
  background: transparent; border: none; color: var(--text-main);
  margin-left: 8px; width: 220px; outline: none; font-size: 0.9rem;
}

.divider { width: 1px; height: 24px; background: var(--border); }

.toggle-group { display: flex; align-items: center; gap: 1rem; }

.theme-pill {
  background: var(--bg-input); border: 1px solid var(--border);
  border-radius: 20px; padding: 3px; display: flex;
}
.theme-pill button {
  padding: 5px 15px; border: none; background: none; color: var(--text-main);
  font-size: 0.75rem; font-weight: 700; cursor: pointer; border-radius: 15px; transition: 0.3s;
}
.theme-pill button.active { background: var(--accent); color: white; }

.icon-btn-toggle {
  background: var(--bg-input); border: 1px solid var(--border);
  width: 38px; height: 38px; border-radius: 10px; cursor: pointer;
  display: flex; align-items: center; justify-content: center; 
  font-size: 1.1rem; transition: 0.2s;
}
.icon-btn-toggle:hover { border-color: var(--accent); transform: scale(1.05); }

/* 3. CONTENIDO */
.app-content { max-width: 1400px; margin: 0 auto; padding: 2rem; display: flex; flex-direction: column; gap: 2.5rem; }
.stats-and-actions { display: flex; justify-content: space-between; align-items: center; }

.btn-primary-add {
  background: var(--accent); color: white; border: none;
  padding: 10px 20px; border-radius: 10px; font-weight: 700;
  display: flex; align-items: center; gap: 8px; cursor: pointer;
  transition: 0.3s;
}
.btn-primary-add.active { background: #cf222e; }

.section-title { color: var(--text-bright); }
.badge { background: var(--bg-input); border: 1px solid var(--border); color: var(--text-main); padding: 4px 12px; border-radius: 12px; font-size: 0.8rem; }

.books-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 30px; }

/* Transiciones */
.slide-fade-enter-active { transition: all 0.3s ease-out; }
.slide-fade-leave-active { transition: all 0.2s cubic-bezier(1, 0.5, 0.8, 1); }
.slide-fade-enter-from, .slide-fade-leave-to { transform: translateY(-20px); opacity: 0; }

@media (max-width: 1200px) { .books-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 800px) { .books-grid { grid-template-columns: repeat(2, 1fr); } }
</style>