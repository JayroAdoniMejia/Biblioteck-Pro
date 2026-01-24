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
        <p class="overlay-desc">{{ truncarTexto(libro.description, 100) }}</p>
        <button class="btn-read-pill">📖 Leer más</button>
      </div>
    </div>

    <div class="card-info">
      <h4 class="book-title">{{ libro.title }}</h4>
      <p class="book-author">{{ libro.author }}</p>
      <div class="card-footer">
        <span class="book-year">{{ libro.year }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps(['libro']);
defineEmits(['ver']);

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
.book-card {
  background: var(--bg-card);
  border-radius: 16px; /* Bordes más redondeados y modernos */
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  height: 100%;
  box-shadow: var(--shadow);
}

.book-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
  border-color: var(--accent);
}

.card-cover {
  position: relative;
  aspect-ratio: 2/3;
  background: var(--bg-input);
  overflow: hidden;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: relative;
  z-index: 2;
  transition: transform 0.5s ease;
}

.book-card:hover .cover-img {
  transform: scale(1.1); /* Efecto de zoom sutil */
}

.cover-placeholder {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, var(--bg-input) 0%, var(--border) 100%);
  z-index: 1;
}

.placeholder-icon { font-size: 3rem; opacity: 0.5; }

/* Overlay mejorado con desenfoque de fondo */
.card-overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(137, 87, 229, 0.85); /* Usamos el color de acento con transparencia */
  backdrop-filter: blur(4px);
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  padding: 25px;
  opacity: 0;
  transition: all 0.3s ease;
  z-index: 10;
}

.book-card:hover .card-overlay { opacity: 1; }

.overlay-desc { 
  color: white; 
  font-size: 0.85rem; 
  font-weight: 500; 
  margin-bottom: 20px; 
  text-align: center;
  line-height: 1.4;
}

/* Botón estilo pastilla profesional */
.btn-read-pill {
  background: white;
  color: var(--accent);
  border: none;
  padding: 10px 24px;
  border-radius: 30px;
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}

.btn-read-pill:hover { transform: scale(1.1); }

.category-badge {
  position: absolute;
  top: 15px; right: 15px;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(5px);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.7rem;
  font-weight: 700;
  z-index: 5;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.card-info { 
  padding: 15px; 
  background: var(--bg-card); 
  flex-grow: 1; 
  display: flex;
  flex-direction: column;
}

.book-title { 
  margin: 0; 
  font-size: 1rem; 
  color: var(--text-bright); 
  font-weight: 700; 
  line-height: 1.2;
}

.book-author { 
  color: var(--text-muted); 
  font-size: 0.85rem; 
  margin: 6px 0 auto 0; 
}

.card-footer { margin-top: 10px; }

.book-year { 
  color: var(--accent); 
  font-size: 0.8rem; 
  font-weight: 800;
  background: var(--bg-input);
  padding: 2px 8px;
  border-radius: 6px;
}
</style>