<template>
  <section class="table-section">
    <table class="minimal-table">
      <thead>
        <tr>
          <th>Libro / Descripción</th>
          <th>Autor</th>
          <th>Año</th>
          <th class="text-right">Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="libro in libros" :key="libro.id || libro._id" class="table-row">
          <td>
            <div class="book-info-wrapper">
              <div class="category-icon" :title="libro.category">
                {{ obtenerIcono(libro.category) }}
              </div>
              <div class="book-info">
                <strong class="book-title">{{ libro.title }}</strong>
                <small class="desc-text">{{ truncarTexto(libro.description, 60) }}</small>
              </div>
            </div>
          </td>
          <td class="text-muted">{{ libro.author }}</td>
          <td class="text-muted">{{ libro.year }}</td>
          <td class="actions justify-right">
            <button @click="$emit('ver', libro)" class="btn-action btn-view" title="Ver detalles y PDF">
              👁️ Ver
            </button>
            <button @click="$emit('eliminar', libro.id || libro._id)" class="btn-action btn-del" title="Eliminar de MongoDB">
              🗑️
            </button>
          </td>
        </tr>
        <tr v-if="libros.length === 0">
          <td colspan="4" class="empty-msg">
            <div class="empty-state">
              <span>📭</span>
              <p>No hay libros disponibles en este momento.</p>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<script setup>
const props = defineProps(['libros', 'baseUrl']);
defineEmits(['eliminar', 'actualizar', 'ver']);

// Lógica de iconos dinámicos
const obtenerIcono = (categoria) => {
  const cat = categoria?.toLowerCase() || '';
  if (cat.includes('programación') || cat.includes('java')) return '💻';
  if (cat.includes('medicina') || cat.includes('salud')) return '💊'; // Guiño a FUNDESUR
  if (cat.includes('diseño')) return '🎨';
  if (cat.includes('historia')) return '📜';
  return '📚'; 
};

const truncarTexto = (texto, limite) => {
  if (!texto) return 'Sin descripción';
  return texto.length > limite ? texto.substring(0, limite) + '...' : texto;
};
</script>

<style scoped>
.table-section {
  background: var(--bg-card);
  border-radius: 8px;
  border: 1px solid var(--border);
  overflow: hidden;
}

.minimal-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.minimal-table th {
  text-align: left;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.02);
  color: var(--text-muted);
  font-weight: 500;
  text-transform: uppercase;
  font-size: 0.75rem;
  letter-spacing: 0.05em;
  border-bottom: 1px solid var(--border);
}

.table-row {
  transition: background 0.2s ease;
  border-bottom: 1px solid var(--border);
}

.table-row:hover {
  background: rgba(255, 255, 255, 0.03);
}

.minimal-table td {
  padding: 12px 20px;
  vertical-align: middle;
}

/* Contenedor del Icono + Info */
.book-info-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-icon {
  width: 36px;
  height: 36px;
  background: var(--bg-input);
  border: 1px solid var(--border);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  flex-shrink: 0;
}

.book-info { display: flex; flex-direction: column; }
.book-title { color: var(--text-main); font-weight: 500; }
.desc-text { color: var(--text-muted); font-size: 0.8rem; }
.text-muted { color: var(--text-muted); }

.actions { display: flex; gap: 8px; }
.justify-right { justify-content: flex-end; }
.text-right { text-align: right !important; }

.btn-action {
  border: 1px solid var(--border);
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.8rem;
  transition: all 0.2s;
  background: var(--bg-input);
  color: var(--text-main);
}

.btn-view:hover {
  background: var(--accent);
  border-color: var(--accent);
  color: white;
}

.btn-del:hover {
  background: #ef4444;
  border-color: #ef4444;
  color: white;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0;
  color: var(--text-muted);
}
</style>
