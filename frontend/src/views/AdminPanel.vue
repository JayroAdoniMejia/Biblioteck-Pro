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
import Toast from '../components/Toast.vue'; 

const router = useRouter();
const API_BASE_URL = 'http://localhost:8080/api/books';

// --- GESTIÓN DE ESTADO Y ROLES ---
const libros = ref([]);
const filtro = ref('');
const libroSeleccionado = ref(null);
const vistaGrid = ref(true);
const mostrarFormulario = ref(false);
const esModoClaro = ref(document.documentElement.getAttribute('data-theme') === 'light');

// --- SISTEMA DE TOAST (NOTIFICACIONES) ---
const toast = ref({ visible: false, mensaje: '', icono: '', tipo: 'success' });

const mostrarNotificacion = (msg, icon, type = 'success') => {
  toast.value = { visible: true, mensaje: msg, icono: icon, tipo: type };
  setTimeout(() => { toast.value.visible = false; }, 3000);
};

// --- SISTEMA DE FAVORITOS, LECTURAS Y CATEGORÍAS ---
const favoritos = ref(JSON.parse(localStorage.getItem('favs') || '[]'));
const lecturas = ref(JSON.parse(localStorage.getItem('lecturas') || '{}'));
const filtroCategoria = ref('Todos');
const mostrarSoloFavoritos = ref(false); 
const categorias = ['Todos', 'Programación', 'Historia', 'Medicina', 'Diseño', 'General'];

const userRole = ref(localStorage.getItem('userRole') || 'LECTOR');
const userName = ref(localStorage.getItem('userName') || 'Usuario');
const esAdmin = computed(() => userRole.value === 'ADMIN');

// --- MÉTODOS DE INTERACCIÓN ---
const toggleFavorito = (libroId) => {
  const index = favoritos.value.indexOf(libroId);
  if (index > -1) {
    favoritos.value.splice(index, 1);
    mostrarNotificacion('Eliminado de tu estante', '📂');
  } else {
    favoritos.value.push(libroId);
    mostrarNotificacion('¡Guardado en tu estante!', '🔖');
  }
  localStorage.setItem('favs', JSON.stringify(favoritos.value));
};

const registrarLectura = (libro) => {
  libroSeleccionado.value = libro;
  const id = libro.id || libro._id;
  
  // Evolución de la lógica de lecturas para soportar progreso y fecha
  if (typeof lecturas.value[id] !== 'object') {
    lecturas.value[id] = { veces: (lecturas.value[id] || 0), fecha: null, progreso: 0 };
  }
  
  lecturas.value[id].veces++;
  lecturas.value[id].fecha = new Date().toISOString();
  // Simulamos un inicio de lectura al 10% si es nuevo
  if (lecturas.value[id].progreso === 0) lecturas.value[id].progreso = 10;
  
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
  if (mostrarSoloFavoritos.value) {
    resultado = resultado.filter(l => favoritos.value.includes(l.id || l._id));
  }
  if (filtroCategoria.value !== 'Todos') {
    resultado = resultado.filter(l => l.category === filtroCategoria.value);
  }
  if (filtro.value) {
    const search = filtro.value.toLowerCase();
    resultado = resultado.filter(l => 
      l.title.toLowerCase().includes(search) || l.author.toLowerCase().includes(search)
    );
  }
  return resultado;
});

// Lógica para "Leídos más recientes"
const leidosRecientes = computed(() => {
  return Object.entries(lecturas.value)
    .filter(([id, data]) => data.fecha)
    .sort((a, b) => new Date(b[1].fecha) - new Date(a[1].fecha))
    .slice(0, 4)
    .map(([id, data]) => {
      const libro = libros.value.find(l => (l.id || l._id) === id);
      return libro ? { ...libro, ...data } : null;
    })
    .filter(l => l !== null);
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
    mostrarNotificacion('Libro eliminado permanentemente', '🗑️', 'error');
  } catch (error) { alert('Error al eliminar'); }
};

onMounted(obtenerLibros);
</script>

<template>
  <div class="biblioteck-app">
    <Toast 
      :visible="toast.visible" 
      :mensaje="toast.mensaje" 
      :icono="toast.icono" 
      :tipo="toast.tipo" 
    />

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
          @click="mostrarSoloFavoritos = !mostrarSoloFavoritos"
          class="cat-pill fav-filter"
          :class="{ 'active-fav': mostrarSoloFavoritos }"
        >
          {{ mostrarSoloFavoritos ? '🔖 Mi Estante' : '📑 Ver Guardados' }}
        </button>

        <div class="divider-v"></div>

        <button 
          v-for="cat in categorias" :key="cat"
          @click="filtroCategoria = cat"
          class="cat-pill"
          :class="{ 'active-cat': filtroCategoria === cat }"
        >
          {{ cat }}
        </button>
      </div>

      <section v-if="leidosRecientes.length > 0 && !mostrarSoloFavoritos" class="recent-section">
        <h3 class="recent-title-label">📖 Continuar leyendo</h3>
        <div class="recent-grid">
          <div v-for="libro in leidosRecientes" :key="libro.id || libro._id" class="recent-card" @click="registrarLectura(libro)">
            <div class="recent-card-info">
              <span class="r-title">{{ libro.title }}</span>
              <span class="r-author">{{ libro.author }}</span>
            </div>
            <div class="recent-progress-wrapper">
              <div class="r-progress-bar">
                <div class="r-progress-fill" :style="{ width: libro.progreso + '%' }"></div>
              </div>
              <span class="r-progress-text">{{ libro.progreso }}%</span>
            </div>
          </div>
        </div>
      </section>

      <transition name="slide-fade">
        <div v-if="mostrarFormulario && esAdmin" class="form-container">
          <BookForm :baseUrl="API_BASE_URL" @libroGuardado="obtenerLibros" />
        </div>
      </transition>

      <section class="display-section">
        <div class="section-meta">
          <h2 class="section-title">
            {{ mostrarSoloFavoritos ? '🔖 Mi Estantería de Caoba' : (vistaGrid ? '📚 Catálogo' : (esAdmin ? '⚙️ Gestión' : '📖 Lista de Títulos')) }}
          </h2>
          <div class="badge">{{ librosFiltrados.length }} títulos</div>
          <div v-if="!esAdmin" class="user-badge">Modo Lectura</div>
        </div>

        <div v-if="vistaGrid" :class="['books-grid', { 'caoba-shelf-view': mostrarSoloFavoritos }]">
          <div v-if="mostrarSoloFavoritos" class="magic-particles"></div>

          <div v-if="librosFiltrados.length === 0" class="empty-results">
            <span class="empty-icon">{{ mostrarSoloFavoritos ? '🔖' : '🔍' }}</span>
            <h3>{{ mostrarSoloFavoritos ? 'Tu estante está vacío' : 'No encontramos lo que buscas' }}</h3>
            <p>Explora el catálogo y guarda libros usando el icono de marcapáginas.</p>
          </div>

          <BookCard 
            v-for="libro in librosFiltrados" 
            :key="libro.id || libro._id" 
            :libro="libro" 
            :esFavorito="favoritos.includes(libro.id || libro._id)"
            :vecesLeido="typeof lecturas[libro.id || libro._id] === 'object' ? lecturas[libro.id || libro._id].veces : (lecturas[libro.id || libro._id] || 0)"
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
/* --- NUEVOS ESTILOS: RECIENTES Y PROGRESO --- */
.recent-section { background: var(--bg-card); padding: 20px; border-radius: 18px; border: 1px solid var(--border); margin-bottom: -10px; }
.recent-title-label { font-size: 0.95rem; color: var(--text-bright); margin-bottom: 15px; font-weight: 700; }
.recent-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 15px; }
.recent-card { background: var(--bg-input); padding: 12px; border-radius: 12px; cursor: pointer; border: 1px solid transparent; transition: 0.3s; }
.recent-card:hover { border-color: var(--accent); transform: translateY(-3px); }
.r-title { display: block; font-size: 0.85rem; font-weight: 700; color: var(--text-bright); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.r-author { font-size: 0.75rem; color: var(--text-muted); }
.recent-progress-wrapper { margin-top: 10px; display: flex; align-items: center; gap: 8px; }
.r-progress-bar { flex: 1; height: 6px; background: var(--border); border-radius: 3px; overflow: hidden; }
.r-progress-fill { height: 100%; background: linear-gradient(90deg, var(--accent), #2ecc71); transition: width 0.6s ease; }
.r-progress-text { font-size: 0.7rem; font-weight: 800; color: var(--accent); min-width: 30px; }

/* --- ESTANTERÍA CAOBA OSCURO --- */
.caoba-shelf-view { position: relative; padding-top: 40px; padding-bottom: 100px; gap: 80px 30px !important; }
.caoba-shelf-view::before {
  content: ""; position: absolute; top: 0; left: -20px; right: -20px; bottom: 0;
  background-image: linear-gradient(transparent 360px, #4a1a1a 360px, #5d2525 362px, #3d1414 365px, #2b0d0d 395px, transparent 395px);
  background-size: 100% 440px; z-index: 0; pointer-events: none;
}

/* --- POLVO DE ESTRELLAS --- */
.magic-particles {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: 1;
  background-image: radial-gradient(circle, #e0a82e 1.2px, transparent 1.2px), radial-gradient(circle, #ffffff 0.8px, transparent 0.8px);
  background-size: 180px 180px, 280px 280px; animation: magicFloat 25s linear infinite; opacity: 0.35;
}
@keyframes magicFloat { 0% { background-position: 0 0, 50px 50px; opacity: 0.2; } 50% { opacity: 0.4; background-position: 100px 300px, -100px 500px; } 100% { background-position: 200px 600px, -200px 1000px; opacity: 0.2; } }

/* --- TUS ESTILOS ORIGINALES INTACTOS --- */
.category-filters { display: flex; align-items: center; gap: 12px; margin-top: -15px; flex-wrap: wrap; }
.divider-v { width: 1px; height: 25px; background: var(--border); margin: 0 5px; }
.cat-pill { background: var(--bg-card); border: 1px solid var(--border); color: var(--text-main); padding: 8px 18px; border-radius: 20px; cursor: pointer; font-size: 0.85rem; font-weight: 600; transition: all 0.3s ease; }
.fav-filter { border-color: var(--accent); color: var(--accent); }
.active-fav { background: var(--accent) !important; color: white !important; border-color: var(--accent) !important; box-shadow: 0 4px 12px rgba(137, 87, 229, 0.3); }
.cat-pill.active-cat { background: var(--accent); color: white; border-color: var(--accent); box-shadow: 0 4px 12px rgba(137, 87, 229, 0.3); }
.empty-results { grid-column: 1 / -1; text-align: center; padding: 60px 20px; background: var(--bg-card); border-radius: 20px; border: 2px dashed var(--border); }
.empty-icon { font-size: 3rem; display: block; margin-bottom: 15px; filter: drop-shadow(0 0 10px rgba(137, 87, 229, 0.2)); }
.empty-results h3 { color: var(--text-bright); margin-bottom: 10px; }
.empty-results p { color: var(--text-muted); }
.book-animation { display: flex; align-items: flex-end; gap: 3px; height: 35px; width: 35px; padding-bottom: 2px; }
.book-spine { width: 8px; border-radius: 2px; animation: bookStack 1.5s infinite ease-in-out; }
.s1 { background: var(--accent); height: 15px; animation-delay: 0.1s; }
.s2 { background: #2ecc71; height: 25px; animation-delay: 0.3s; }
.s3 { background: #3498db; height: 20px; animation-delay: 0.5s; }
@keyframes bookStack { 0%, 100% { transform: translateY(0) scaleY(1); opacity: 1; } 50% { transform: translateY(-10px) scaleY(1.1); opacity: 0.7; } }
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
.books-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 30px; position: relative; }
.slide-fade-enter-active { transition: all 0.3s ease-out; }
.slide-fade-leave-active { transition: all 0.2s cubic-bezier(1, 0.5, 0.8, 1); }
.slide-fade-enter-from, .slide-fade-leave-to { transform: translateY(-20px); opacity: 0; }
@media (max-width: 1200px) { .books-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 800px) { .books-grid { grid-template-columns: repeat(2, 1fr); } }
</style>