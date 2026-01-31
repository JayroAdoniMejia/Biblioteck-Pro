<template>
  <div v-if="libro" class="modal-overlay" @click.self="$emit('cerrar')">
    <div class="modal-content">
      <button class="close-btn" @click="$emit('cerrar')" title="Cerrar">&times;</button>
      
      <div class="modal-body">
        <div class="info-section">
          <h2 class="modal-title">{{ libro.title }}</h2>
          <p class="author-tag">Por: {{ libro.author }} ({{ libro.year }})</p>
          <span class="category-badge">{{ libro.category }}</span>
          
          <div class="description-box">
            <h4>Descripción</h4>
            <p>{{ libro.description || 'Sin descripción disponible.' }}</p>
          </div>

          <div class="progress-setter">
            <div class="setter-header">
              <h4>Marcador de avance</h4>
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
            <p class="setter-hint">Haz clic en un hito o usa la barra para ajustar</p>
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
          <iframe 
            :src="`${baseUrl}/download/${libro.pdfUrl}#toolbar=1`" 
            width="100%" 
            height="550px"
            frameborder="0">
            <p>Tu navegador no soporta visualización de PDFs. 
               <a :href="`${baseUrl}/download/${libro.pdfUrl}`">Haz clic aquí para descargar.</a>
            </p>
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

// Puntos de control para el usuario
const hitos = [
  { label: 'Inicio', val: 10 },
  { label: '25%', val: 25 },
  { label: '50%', val: 50 },
  { label: '75%', val: 75 },
  { label: '¡Fin!', val: 100 }
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
/* --- ESTILOS MEJORADOS PARA HITOS --- */
.progress-setter {
  margin-top: 25px;
  padding: 18px;
  background: var(--bg-input);
  border-radius: 15px;
  border: 1px solid var(--border);
}

.setter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.setter-header h4 { margin: 0; font-size: 0.9rem; color: var(--text-bright); }

.perc-badge { 
  background: var(--accent); 
  color: white; 
  padding: 3px 10px; 
  border-radius: 8px; 
  font-size: 0.75rem; 
  font-weight: 800; 
  transition: all 0.3s ease;
}

.perc-badge.completed {
  background: #2ecc71;
  box-shadow: 0 0 12px rgba(46, 204, 113, 0.3);
}

.hitos-grid {
  display: flex;
  gap: 6px;
  margin-bottom: 15px;
}

.hito-btn {
  flex: 1;
  background: var(--bg-card);
  border: 1px solid var(--border);
  color: var(--text-muted);
  padding: 6px 2px;
  border-radius: 8px;
  font-size: 0.65rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.hito-btn:hover {
  border-color: var(--accent);
  color: var(--text-bright);
}

.hito-btn.active {
  background: rgba(137, 87, 229, 0.15);
  border-color: var(--accent);
  color: var(--accent);
}

.range-input {
  width: 100%;
  accent-color: var(--accent);
  cursor: pointer;
  height: 4px;
}

.setter-hint {
  font-size: 0.65rem;
  color: var(--text-muted);
  margin-top: 10px;
  text-align: center;
}

/* --- TUS ESTILOS ORIGINALES --- */
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(1, 4, 9, 0.85); 
  display: flex; align-items: center; justify-content: center; z-index: 2000;
  backdrop-filter: blur(8px);
}
.modal-content {
  background: var(--bg-card); 
  width: 95%; max-width: 1150px; border-radius: 20px;
  position: relative; padding: 45px 35px 35px; 
  border: 1px solid var(--border); 
  color: var(--text-main);
  box-shadow: 0 20px 60px rgba(0,0,0,0.8);
  animation: modalSlideIn 0.3s ease-out;
}
@keyframes modalSlideIn {
  from { transform: translateY(30px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
.modal-body { display: grid; grid-template-columns: 1fr 2fr; gap: 40px; }
.modal-title { color: var(--text-bright); font-size: 1.8rem; font-weight: 800; margin-bottom: 5px; }
.close-btn {
  position: absolute; top: 15px; right: 25px; background: none; border: none;
  color: var(--text-main); font-size: 2rem; cursor: pointer; transition: 0.3s;
}
.close-btn:hover { color: var(--accent); transform: rotate(90deg); }
.author-tag { color: var(--accent); font-weight: 600; margin-bottom: 12px; }
.category-badge {
  background: var(--bg-input); color: var(--accent); border: 1px solid var(--accent);
  padding: 5px 16px; border-radius: 25px; font-size: 0.75rem; font-weight: 700;
  text-transform: uppercase;
}
.description-box { margin-top: 30px; line-height: 1.7; color: var(--text-main); }
.description-box h4 { 
  color: var(--text-bright); border-bottom: 1px solid var(--border); 
  padding-bottom: 8px; margin-bottom: 15px; font-size: 1rem;
}
.modal-footer { margin-top: 40px; }
.btn-download {
  display: flex; align-items: center; justify-content: center;
  gap: 10px; padding: 12px 24px; background: var(--accent); color: white;
  text-decoration: none; border-radius: 10px; font-weight: 700;
  transition: all 0.3s; box-shadow: 0 4px 15px rgba(137, 87, 229, 0.3);
}
.btn-download:hover { 
  background: var(--accent-hover); transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(137, 87, 229, 0.4);
}
.preview-section { 
  border-radius: 12px; overflow: hidden; background: var(--bg-main); 
  border: 1px solid var(--border); min-height: 550px;
  box-shadow: inset 0 0 20px rgba(0,0,0,0.5);
}
@media (max-width: 900px) {
  .modal-body { grid-template-columns: 1fr; gap: 20px; }
  .preview-section { height: 450px; min-height: 450px; }
  .modal-content { padding: 40px 20px 20px; }
}
</style>