<template>
  <div class="book-card" @click="$emit('ver', libro)">
    <div class="card-cover">
      <img 
        v-if="libro.imageUrl" 
        :src="libro.imageUrl" 
        :alt="libro.title" 
        class="cover-img"
      />
      <div v-else class="cover-placeholder">
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

const obtenerIcono = (categoria) => {
  const cat = categoria?.toLowerCase() || '';
  if (cat.includes('programación')) return '💻';
  if (cat.includes('historia')) return '📜';
  if (cat.includes('ciencia')) return '🧬';
  return '📚';
};

const truncarTexto = (texto, limite) => {
  if (!texto) return 'Sin descripción disponible.';
  return texto.length > limite ? texto.substring(0, limite) + '...' : texto;
};
</script>

<style scoped>
.book-card {
  background: var(--bg-card);
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  border: 1px solid var(--border);
  display: flex;
  flex-direction: column;
}

.book-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(187, 134, 252, 0.2);
  border-color: #bb86fc;
}

/* Portada */
.card-cover {
  position: relative;
  aspect-ratio: 2/3;
  background: #252525;
  overflow: hidden;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(45deg, #1a1a1a, #2d2d2d);
}

.placeholder-icon { font-size: 3rem; opacity: 0.5; }

/* Overlay Púrpura */
.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(187, 134, 252, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  opacity: 0;
  transition: opacity 0.3s ease;
  text-align: center;
}

.book-card:hover .card-overlay { opacity: 1; }

.overlay-desc {
  color: #121212;
  font-size: 0.85rem;
  font-weight: 500;
  margin-bottom: 15px;
}

.btn-read {
  background: #121212;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
}

/* Etiquetas y Textos */
.category-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  color: #bb86fc;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.7rem;
  font-weight: bold;
  text-transform: uppercase;
}

.card-info { padding: 15px; }
.book-title {
  margin: 0 0 5px 0;
  font-size: 1rem;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-author {
  color: var(--text-muted);
  font-size: 0.85rem;
  margin: 0;
}

.book-year {
  display: block;
  margin-top: 8px;
  font-size: 0.75rem;
  color: #bb86fc;
  font-weight: bold;
}
</style>