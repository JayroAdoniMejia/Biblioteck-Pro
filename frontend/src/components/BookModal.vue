<template>
  <div v-if="libro" class="modal-overlay" @click.self="$emit('cerrar')">
    <div :class="['modal-content', temaLectura]">
      <button class="close-btn" @click="$emit('cerrar')" title="Cerrar">&times;</button>
      
      <div class="modal-body">
        <div class="info-section">
          <h2 class="modal-title">{{ libro.title }}</h2>
          <p class="author-tag">Por: {{ libro.author }} ({{ libro.year }})</p>
          <span class="category-badge">{{ libro.category }}</span>
          
          <div class="description-box hide-mobile">
            <h4>Descripción</h4>
            <p>{{ libro.description || 'Sin descripción disponible.' }}</p>
          </div>

          <div class="reading-modes">
            <span>🎨 Modo:</span>
            <button @click="temaLectura = 'tema-claro'" :class="{ active: temaLectura === 'tema-claro' }">☀️</button>
            <button @click="temaLectura = 'tema-sepia'" :class="{ active: temaLectura === 'tema-sepia' }">📜</button>
            <button @click="temaLectura = 'tema-noche'" :class="{ active: temaLectura === 'tema-noche' }">🌙</button>
          </div>

          <div class="progress-setter">
            <div class="setter-header">
              <h4>Tu Avance</h4>
              <span class="perc-badge" :class="{ 'completed': progreso === 100 }">
                {{ progreso === 100 ? '✅ Terminado' : progreso + '%' }}
              </span>
            </div>
            
            <div class="hitos-grid">
              <button 
                v-for="hito in hitos" 
                :key="hito.val"
                @click="actualizarProgreso(hito.val)"
                :class="['hito-btn', { 'active': progreso >= hito.val }]"
              >
                {{ hito.label }}
              </button>
            </div>

            <input 
              type="range" 
              v-model="progreso" 
              min="0" max="100" 
              class="range-input"
              @change="actualizarProgreso()"
            />
          </div>

          <div class="modal-footer">
            <a :href="`${baseUrl}/download/${libro.pdfUrl}`" 
               target="_blank" 
               class="btn-download">
               📥 Descargar PDF
            </a>
          </div>
        </div>

        <div class="preview-section">
          <div class="preview-header">
            <span>Visualización Rápida</span>
          </div>
          <iframe 
            :src="`${baseUrl}/download/${libro.pdfUrl}#toolbar=1`" 
            class="pdf-viewer"
            frameborder="0">
          </iframe>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const props = defineProps(['libro', 'baseUrl']);
const emit = defineEmits(['cerrar']);

const progreso = ref(0);
const temaLectura = ref('tema-noche'); // Por defecto oscuro

const hitos = [
  { label: 'Inicio', val: 10 },
  { label: '50%', val: 50 },
  { label: '75%', val: 75 },
  { label: 'Fin', val: 100 }
];

onMounted(() => {
  const lecturas = JSON.parse(localStorage.getItem('lecturas') || '{}');
  const id = props.libro.id || props.libro._id;
  if (lecturas[id] && typeof lecturas[id] === 'object') {
    progreso.value = lecturas[id].progreso || 0;
  }
});

const actualizarProgreso = (nuevoValor = null) => {
  if (nuevoValor !== null) progreso.value = nuevoValor;
  const lecturas = JSON.parse(localStorage.getItem('lecturas') || '{}');
  const id = props.libro.id || props.libro._id;

  if (!lecturas[id] || typeof lecturas[id] !== 'object') {
    lecturas[id] = { veces: 1, fecha: new Date().toISOString(), progreso: 0 };
  }
  lecturas[id].progreso = parseInt(progreso.value);
  lecturas[id].fecha = new Date().toISOString(); 
  localStorage.setItem('lecturas', JSON.stringify(lecturas));
};
</script>

<style scoped>
/* MODOS DE LECTURA (COLORES) */
.tema-claro { --modal-bg: #ffffff; --modal-text: #1a1a1b; --modal-sub: #4f5a69; }
.tema-sepia { --modal-bg: #f4ecd8; --modal-text: #5b4636; --modal-sub: #8d7a65; }
.tema-noche { --modal-bg: #0d1117; --modal-text: #c9d1d9; --modal-sub: #8b949e; }

.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.9);
  display: flex; align-items: center; justify-content: center; z-index: 2000;
  backdrop-filter: blur(10px);
  padding: 10px;
}

.modal-content {
  background: var(--modal-bg);
  color: var(--modal-text);
  width: 95%; max-width: 1200px; max-height: 90vh;
  border-radius: 24px; position: relative;
  padding: 30px; border: 1px solid rgba(255,255,255,0.1);
  overflow-y: auto;
  transition: background 0.4s ease, color 0.4s ease;
}

.modal-body { display: grid; grid-template-columns: 350px 1fr; gap: 30px; }

/* READING MODES */
.reading-modes {
  display: flex; align-items: center; gap: 10px;
  margin: 20px 0; background: rgba(0,0,0,0.05);
  padding: 10px; border-radius: 12px;
}
.reading-modes button {
  border: 1px solid var(--border); background: var(--bg-card);
  padding: 5px 10px; border-radius: 8px; cursor: pointer; transition: 0.3s;
}
.reading-modes button.active { background: var(--accent); color: white; border-color: var(--accent); }

/* PROGRESS */
.progress-setter { background: rgba(0,0,0,0.05); padding: 15px; border-radius: 15px; }
.hitos-grid { display: flex; gap: 5px; margin: 10px 0; }
.hito-btn { flex: 1; font-size: 0.65rem; padding: 6px; cursor: pointer; border-radius: 6px; border: 1px solid var(--border); }
.hito-btn.active { background: var(--accent); color: white; }

/* PDF VIEWER */
.preview-section { display: flex; flex-direction: column; background: #333; border-radius: 12px; overflow: hidden; height: 600px; }
.preview-header { background: #222; color: #fff; padding: 10px; font-size: 0.8rem; text-align: center; }
.pdf-viewer { width: 100%; height: 100%; }

/* CLOSE BUTTON */
.close-btn {
  position: absolute; top: 15px; right: 20px; background: none; border: none;
  color: var(--modal-text); font-size: 2rem; cursor: pointer; z-index: 10;
}

/* RESPONSIVE */
@media (max-width: 1000px) {
  .modal-body { grid-template-columns: 1fr; }
  .info-section { order: 2; }
  .preview-section { order: 1; height: 450px; }
  .hide-mobile { display: none; }
  .modal-content { padding: 40px 15px 15px; width: 100%; height: 100%; max-height: 100vh; border-radius: 0; }
}

.modal-title { font-size: 1.5rem; font-weight: 800; margin-bottom: 5px; color: inherit; }
.author-tag { color: var(--accent); font-weight: 600; }
.category-badge { background: var(--accent); color: white; padding: 4px 12px; border-radius: 20px; font-size: 0.7rem; }
.btn-download { display: block; text-align: center; background: var(--accent); color: white; padding: 12px; border-radius: 10px; text-decoration: none; font-weight: 700; margin-top: 15px; }
</style>