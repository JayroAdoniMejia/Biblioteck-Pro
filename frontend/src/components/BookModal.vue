<template>
  <div v-if="libro" class="modal-overlay" @click.self="$emit('cerrar')">
    <div class="modal-content">
      <button class="close-btn" @click="$emit('cerrar')" title="Cerrar">&times;</button>
      
      <div class="modal-body">
        <div class="info-section">
          <h2>{{ libro.title }}</h2>
          <p class="author-tag">Por: {{ libro.author }} ({{ libro.year }})</p>
          <span class="category-badge">{{ libro.category }}</span>
          
          <div class="description-box">
            <h4>Descripción</h4>
            <p>{{ libro.description || 'Sin descripción disponible.' }}</p>
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
defineProps(['libro', 'baseUrl']);
defineEmits(['cerrar']);
</script>

<style scoped>
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.9); /* Más oscuro para resaltar el contenido */
  display: flex; align-items: center; justify-content: center; z-index: 1000;
  backdrop-filter: blur(5px); /* Efecto de desenfoque al fondo */
}

.modal-content {
  background: #1a1a1a; width: 95%; max-width: 1100px; border-radius: 15px;
  position: relative; padding: 40px 30px 30px; border: 1px solid #444; color: white;
  box-shadow: 0 10px 50px rgba(0,0,0,0.5);
}

.modal-body { display: grid; grid-template-columns: 1fr 2fr; gap: 30px; }

.close-btn {
  position: absolute; top: 10px; right: 20px; background: none; border: none;
  color: #fff; font-size: 2.5rem; cursor: pointer; transition: 0.3s;
}
.close-btn:hover { color: #bb86fc; transform: rotate(90deg); }

.author-tag { color: #bb86fc; font-weight: bold; margin-bottom: 10px; }

.category-badge {
  background: #bb86fc; color: #000; padding: 4px 15px; border-radius: 20px; 
  font-size: 0.8rem; font-weight: bold;
}

.description-box { margin-top: 25px; line-height: 1.6; color: #eee; }
.description-box h4 { border-bottom: 1px solid #333; padding-bottom: 5px; margin-bottom: 10px; }

.modal-footer { margin-top: 30px; }

.btn-download {
  display: inline-block; padding: 10px 20px; background: #333; color: white;
  text-decoration: none; border-radius: 8px; border: 1px solid #555; transition: 0.3s;
}
.btn-download:hover { background: #444; border-color: #bb86fc; }

.preview-section { 
  border-radius: 8px; overflow: hidden; background: #222; 
  border: 1px solid #333; min-height: 550px;
}

@media (max-width: 900px) {
  .modal-body { grid-template-columns: 1fr; }
  .preview-section { height: 400px; }
}
</style>