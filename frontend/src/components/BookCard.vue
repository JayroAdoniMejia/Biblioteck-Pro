<template>
  <div class="book-card" @click="$emit('ver', libro)">
    <div class="card-cover">
      <img 
        :src="`https://covers.openlibrary.org/b/title/${encodeURIComponent(libro.title)}-M.jpg?default=false`" 
        :alt="libro.title" 
        class="cover-img"
        @error="handleImageError"
      />
      
      <div class="cover-placeholder">
        <span class="placeholder-icon">{{ obtenerIcono(libro.category) }}</span>
      </div>
      
      <div class="category-badge">
        {{ libro.category }}
      </div>

      <div class="card-overlay">
        <p class="overlay-desc">{{ truncarTexto(libro.description, 120) }}</p>
        <button class="btn-read">📖 Leer más</button>
      </div>
    </div>

    <div class="card-info">
      <h4 class="book-title">{{ libro.title }}</h4>
      <p class="book-author">{{ libro.author }}</p>
      <span class="book-year">{{ libro.year }}</span>
    </div>
  </div>
</template>

<script setup>
const props = defineProps(['libro']);
defineEmits(['ver']);

// Si la imagen de OpenLibrary no existe, ocultamos el elemento img
const handleImageError = (event) => {
  event.target.style.opacity = '0';
};

const obtenerIcono = (categoria) => {
  const cat = categoria?.toLowerCase() || '';
  if (cat.includes('programación')) return '💻';
  if (cat.includes('historia')) return '📜';
  if (cat.includes('ciencia')) return '🧬';
  if (cat.includes('sistemas')) return '⚙️';
  return '📚';
};

const truncarTexto = (texto, limite) => {
  if (!texto) return 'Sin descripción disponible.';
  return texto.length > limite ? texto.substring(0, limite) + '...' : texto;
};
</script>

<style scoped>
/* Se mantienen tus estilos, asegurando que la imagen esté sobre el placeholder */
.book-card {
  background: var(--bg-card);
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  border: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.book-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(187, 134, 252, 0.2);
  border-color: #bb86fc;
}

.card-cover {
  position: relative;
  aspect-ratio: 2/3;
  background: #1a1a1a;
  overflow: hidden;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  position: relative;
  z-index: 2; /* Por encima del placeholder */
  transition: opacity 0.3s ease;
}

.cover-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
  z-index: 1;
}

.placeholder-icon { font-size: 3.5rem; filter: drop-shadow(0 0 10px rgba(187, 134, 252, 0.3)); }

.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(187, 134, 252, 0.95);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 10;
}

.book-card:hover .card-overlay { opacity: 1; }

.overlay-desc { color: #000; font-size: 0.85rem; font-weight: 600; margin-bottom: 15px; text-align: center; }

.btn-read {
  background: #000;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 25px;
  font-weight: bold;
  cursor: pointer;
}

.category-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.7);
  color: #bb86fc;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 0.65rem;
  font-weight: 800;
  z-index: 5;
  border: 1px solid rgba(187, 134, 252, 0.3);
}

.card-info { padding: 18px; background: var(--bg-card); }
.book-title { margin: 0; font-size: 1.1rem; color: #fff; font-weight: 600; }
.book-author { color: var(--text-muted); font-size: 0.9rem; margin: 4px 0; }
.book-year { color: #bb86fc; font-size: 0.8rem; font-weight: bold; }
</style>