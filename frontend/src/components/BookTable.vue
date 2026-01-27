<template>
  <section class="table-section">
    <table class="minimal-table">
      <thead>
        <tr>
          <th>Libro / Descripción</th>
          <th>Autor</th>
          <th>Año</th>
          <th class="text-right">{{ isAdmin ? 'Gestión' : 'Opciones' }}</th>
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
          <td class="td-author">{{ libro.author }}</td>
          <td class="td-year">{{ libro.year }}</td>
          <td class="actions justify-right">
            <button @click="$emit('ver', libro)" class="btn-action btn-view" title="Ver detalles y PDF">
              👁️ Ver
            </button>
            
            <button 
              v-if="isAdmin" 
              @click="$emit('eliminar', libro.id || libro._id)" 
              class="btn-action btn-del" 
              title="Eliminar de MongoDB"
            >
              🗑️
            </button>
          </td>
        </tr>
        
        <tr v-if="libros.length === 0">
          <td colspan="4">
            <div class="empty-state">
              <span class="empty-icon">📭</span>
              <p>No hay libros disponibles en este momento.</p>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<script setup>
// Añadimos 'isAdmin' a las props del componente
const props = defineProps(['libros', 'baseUrl', 'isAdmin']);
defineEmits(['eliminar', 'actualizar', 'ver']);

const obtenerIcono = (categoria) => {
  const cat = categoria?.toLowerCase() || '';
  if (cat.includes('programación') || cat.includes('java')) return '💻';
  if (cat.includes('medicina') || cat.includes('salud')) return '💊';
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
/* Mantienes tus estilos originales que ya están optimizados para el tema oscuro */
.table-section {
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border);
  overflow: hidden;
  box-shadow: var(--shadow);
}

.minimal-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.minimal-table th {
  text-align: left;
  padding: 16px 20px;
  background: var(--bg-header); 
  color: var(--accent);
  font-weight: 700;
  text-transform: uppercase;
  font-size: 0.7rem;
  letter-spacing: 0.1em;
  border-bottom: 1px solid var(--border);
}

.table-row {
  transition: background 0.2s ease;
  border-bottom: 1px solid var(--border);
  background: var(--bg-card);
}

.table-row:hover {
  background: var(--bg-input);
}

.minimal-table td {
  padding: 14px 20px;
  vertical-align: middle;
  color: var(--text-main);
}

.book-info-wrapper {
  display: flex;
  align-items: center;
  gap: 15px;
}

.category-icon {
  width: 40px;
  height: 40px;
  background: var(--bg-main);
  border: 1px solid var(--border);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  flex-shrink: 0;
  box-shadow: inset 0 2px 4px rgba(0,0,0,0.2);
}

.book-info { display: flex; flex-direction: column; gap: 2px; }
.book-title { color: var(--text-bright); font-weight: 600; font-size: 0.95rem; }
.desc-text { color: var(--text-main); opacity: 0.7; font-size: 0.8rem; }

.td-author, .td-year {
  font-weight: 500;
  opacity: 0.9;
}

.actions { display: flex; gap: 10px; }
.justify-right { justify-content: flex-end; }
.text-right { text-align: right !important; }

.btn-action {
  border: 1px solid var(--border);
  padding: 7px 14px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.8rem;
  font-weight: 600;
  transition: all 0.2s;
  background: var(--bg-main);
  color: var(--text-main);
  display: flex;
  align-items: center;
  gap: 5px;
}

.btn-view:hover {
  background: var(--accent);
  border-color: var(--accent);
  color: white;
  transform: translateY(-2px);
}

.btn-del:hover {
  background: #cf222e;
  border-color: #cf222e;
  color: white;
  transform: translateY(-2px);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 0;
  color: var(--text-main);
  opacity: 0.5;
}

.empty-icon { font-size: 3rem; margin-bottom: 10px; }
</style>
