<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

// IMPORTACIÓN DE COMPONENTES
import BookStats from '../components/BookStats.vue';
import BookTable from '../components/BookTable.vue';
import BookForm from '../components/BookForm.vue';
import BookModal from '../components/BookModal.vue';
import BookCard from '../components/BookCard.vue';

const router = useRouter();
const API_BASE_URL = 'http://localhost:8080/api/books';

// --- GESTIÓN DE ESTADO Y ROLES ---
const libros = ref([]);
const filtro = ref('');
const libroSeleccionado = ref(null);
const vistaGrid = ref(true);
const mostrarFormulario = ref(false);
const esModoClaro = ref(document.documentElement.getAttribute('data-theme') === 'light');

// --- SISTEMA DE FAVORITOS Y LECTURAS ---
const favoritos = ref(JSON.parse(localStorage.getItem('favs') || '[]'));
const lecturas = ref(JSON.parse(localStorage.getItem('lecturas') || '{}'));
const filtroCategoria = ref('Todos');
const categorias = ['Todos', 'Programación', 'Historia', 'Medicina', 'Diseño', 'General'];

const userRole = ref(localStorage.getItem('userRole') || 'LECTOR');
const userName = ref(localStorage.getItem('userName') || 'Usuario');
const esAdmin = computed(() => userRole.value === 'ADMIN');

// --- MÉTODOS DE INTERACCIÓN ---
const toggleFavorito = (libroId) => {
  const index = favoritos.value.indexOf(libroId);
  if (index > -1) favoritos.value.splice(index, 1);
  else favoritos.value.push(libroId);
  localStorage.setItem('favs', JSON.stringify(favoritos.value));
};

const registrarLectura = (libro) => {
  libroSeleccionado.value = libro;
  const id = libro.id || libro._id;
  lecturas.value[id] = (lecturas.value[id] || 0) + 1;
  localStorage.setItem('lecturas', JSON.stringify(lecturas.value));
};

const aplicarTema = (claro) => {
  esModoClaro.value = claro;
  const theme = claro ? 'light' : 'dark';
  document.documentElement.setAttribute('data-theme', theme);
  localStorage.setItem('theme', theme);
};

const logout = () => {
  localStorage.clear();
  router.push('/');
};

// --- LÓGICA DE FILTRADO COMBINADO ---
const librosFiltrados = computed(() => {
  let resultado = libros.value;
  
  if (filtro.value) {
    const search = filtro.value.toLowerCase();
    resultado = resultado.filter(l => 
      l.title.toLowerCase().includes(search) || l.author.toLowerCase().includes(search)
    );
  }
  
  if (filtroCategoria.value !== 'Todos') {
    resultado = resultado.filter(l => l.category === filtroCategoria.value);
  }
  
  return resultado;
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
  if (!esAdmin.value) return;
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
          <div class="book-animation">
            <div class="book-spine s1"></div>
            <div class="book-spine s2"></div>
            <div class="book-spine s3"></div>
          </div>
          <div class="brand-info">
            <h1>Biblioteck Pro</h1>
            <span class="version-tag" :class="{ 'lector-tag': !esAdmin }">
              {{ esAdmin ? 'Panel Admin' : 'Hola, ' + userName }}
            </span>
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
            
            <button @click="vistaGrid = !vistaGrid" class="icon-btn-toggle" :title="vistaGrid ? 'Ver Lista' : 'Ver Grid'">
              {{ vistaGrid ? '📋' : '🖼️' }}
            </button>

            <button v-if="esAdmin" class="icon-btn-toggle" title="Limpiar Biblioteca">🗑️</button>
            <button @click="logout" class="icon-btn-toggle btn-logout" title="Cerrar Sesión">🚪</button>
          </div>
        </div>
      </div>
    </header>

    <main class="app-content">
      <div class="stats-and-actions">
        <BookStats :total="totalLibros" :autores="autoresUnicos" :isAdmin="esAdmin" />
        
        <button v-if="esAdmin" @click="mostrarFormulario = !mostrarFormulario" class="btn-primary-add" :class="{ active: mostrarFormulario }">
          <span class="btn-icon">{{ mostrarFormulario ? '✕' : '＋' }}</span>
          {{ mostrarFormulario ? 'Cerrar Registro' : 'Nuevo Libro' }}
        </button>
      </div>

      <div class="category-filters">
        <button 
          v-for="cat in categorias" :key="cat"
          @click="filtroCategoria = cat"
          class="cat-pill"
          :class="{ 'active-cat': filtroCategoria === cat }"
        >
          {{ cat }}
        </button>
      </div>

      <transition name="slide-fade">
        <div v-if="mostrarFormulario && esAdmin" class="form-container">
          <BookForm :baseUrl="API_BASE_URL" @libroGuardado="obtenerLibros" />
        </div>
      </transition>

      <section class="display-section">
        <div class="section-meta">
          <h2 class="section-title">
            {{ vistaGrid ? '📚 Catálogo' : (esAdmin ? '⚙️ Gestión' : '📖 Lista de Títulos') }}
          </h2>
          <div class="badge">{{ librosFiltrados.length }} títulos</div>
          <div v-if="!esAdmin" class="user-badge">Modo Lectura</div>
        </div>

        <div v-if="vistaGrid" class="books-grid">
          <BookCard 
            v-for="libro in librosFiltrados" 
            :key="libro.id || libro._id" 
            :libro="libro" 
            :esFavorito="favoritos.includes(libro.id || libro._id)"
            :vecesLeido="lecturas[libro.id || libro._id] || 0"
            @ver="registrarLectura" 
            @fav="toggleFavorito"
          />
        </div>

        <div v-else class="table-view">
          <BookTable 
            :libros="librosFiltrados" 
            :baseUrl="API_BASE_URL" 
            :isAdmin="esAdmin"
            @eliminar="eliminarLibro" 
            @ver="registrarLectura" 
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

<style scoped>
/* --- ESTILOS DE LAS NUEVAS BURBUJAS --- */
.category-filters {
  display: flex;
  gap: 12px;
  margin-top: -15px;
  flex-wrap: wrap;
}

.cat-pill {
  background: var(--bg-card);
  border: 1px solid var(--border);
  color: var(--text-main);
  padding: 8px 18px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.cat-pill:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.cat-pill.active-cat {
  background: var(--accent);
  color: white;
  border-color: var(--accent);
  box-shadow: 0 4px 12px rgba(137, 87, 229, 0.3);
}

/* --- ESTILOS BASE ANTERIORES --- */
.book-animation { display: flex; align-items: flex-end; gap: 3px; height: 35px; width: 35px; padding-bottom: 2px; }
.book-spine { width: 8px; border-radius: 2px; animation: bookStack 1.5s infinite ease-in-out; }
.s1 { background: var(--accent); height: 15px; animation-delay: 0.1s; }
.s2 { background: #2ecc71; height: 25px; animation-delay: 0.3s; }
.s3 { background: #3498db; height: 20px; animation-delay: 0.5s; }

@keyframes bookStack {
  0%, 100% { transform: translateY(0) scaleY(1); opacity: 1; }
  50% { transform: translateY(-10px) scaleY(1.1); opacity: 0.7; }
}

.app-header { background: var(--bg-header); border-bottom: 1px solid var(--border); padding: 0.8rem 0; position: sticky; top: 0; z-index: 1000; }
.header-container { max-width: 1400px; margin: 0 auto; padding: 0 2rem; display: flex; justify-content: space-between; align-items: center; }
.header-brand { display: flex; align-items: center; gap: 15px; }
.brand-info h1 { font-size: 1.25rem; margin: 0; color: var(--text-bright); font-weight: 800; }
.version-tag { font-size: 0.7rem; color: var(--accent); font-weight: bold; text-transform: uppercase; }
.lector-tag { color: #2ecc71; }

.header-actions { display: flex; align-items: center; gap: 1.5rem; }
.search-wrapper { background: var(--bg-input); border: 1px solid var(--border); border-radius: 8px; padding: 6px 12px; display: flex; align-items: center; }
.search-input { background: transparent; border: none; color: var(--text-main); margin-left: 8px; width: 220px; outline: none; }
.divider { width: 1px; height: 24px; background: var(--border); }

.toggle-group { display: flex; align-items: center; gap: 1rem; }
.theme-pill { background: var(--bg-input); border: 1px solid var(--border); border-radius: 20px; padding: 3px; display: flex; }
.theme-pill button { padding: 5px 15px; border: none; background: none; color: var(--text-main); font-size: 0.75rem; font-weight: 700; cursor: pointer; border-radius: 15px; transition: 0.3s; }
.theme-pill button.active { background: var(--accent); color: white; }

.icon-btn-toggle { background: var(--bg-input); border: 1px solid var(--border); width: 38px; height: 38px; border-radius: 10px; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; }
.icon-btn-toggle:hover { border-color: var(--accent); transform: scale(1.05); }
.btn-logout:hover { border-color: #cf222e; }

.app-content { max-width: 1400px; margin: 0 auto; padding: 2rem; display: flex; flex-direction: column; gap: 2.5rem; }
.stats-and-actions { display: flex; justify-content: space-between; align-items: center; }

.btn-primary-add { background: var(--accent); color: white; border: none; padding: 10px 20px; border-radius: 10px; font-weight: 700; display: flex; align-items: center; gap: 8px; cursor: pointer; transition: 0.3s; }
.btn-primary-add.active { background: #cf222e; }

.section-meta { display: flex; align-items: center; gap: 15px; margin-bottom: 1rem; }
.section-title { color: var(--text-bright); margin: 0; }
.badge { background: var(--bg-input); border: 1px solid var(--border); color: var(--text-main); padding: 4px 12px; border-radius: 12px; font-size: 0.8rem; }
.user-badge { background: rgba(137, 87, 229, 0.1); color: var(--accent); padding: 4px 12px; border-radius: 12px; font-size: 0.8rem; font-weight: bold; border: 1px solid var(--accent); }

.books-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 30px; }

.slide-fade-enter-active { transition: all 0.3s ease-out; }
.slide-fade-leave-active { transition: all 0.2s cubic-bezier(1, 0.5, 0.8, 1); }
.slide-fade-enter-from, .slide-fade-leave-to { transform: translateY(-20px); opacity: 0; }

@media (max-width: 1200px) { .books-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 800px) { .books-grid { grid-template-columns: repeat(2, 1fr); } }
</style>