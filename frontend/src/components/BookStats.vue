<template>
  <section class="stats-container">
    <div class="stat-card card-blue">
      <div class="stat-content">
        <small>TOTAL LIBROS</small>
        <div class="stat-value">{{ total }}</div>
      </div>
      <span class="stat-icon">📚</span>
    </div>

    <div class="stat-card card-green">
      <div class="stat-content">
        <small>AUTORES ÚNICOS</small>
        <div class="stat-value">{{ autores }}</div>
      </div>
      <span class="stat-icon">✍️</span>
    </div>

    <div v-if="isAdmin" class="stat-card card-orange">
      <div class="stat-content">
        <small>ESTADO BACKEND</small>
        <div class="status-indicator">
          <span class="dot pulse"></span>
          MongoDB Conectado
        </div>
      </div>
      <span class="stat-icon">📡</span>
    </div>
  </section>
</template>

<script setup>
// Añadimos 'isAdmin' a las props
defineProps(['total', 'autores', 'isAdmin']);
</script>

<style scoped>
.stats-container {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
}

.stat-card {
  flex: 1;
  padding: 20px;
  border-radius: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s ease;
  box-shadow: var(--shadow);
}

.stat-card:hover {
  transform: translateY(-5px);
  border-color: var(--accent);
}

.card-blue { border-left: 5px solid #3f51b5; }
.card-green { border-left: 5px solid #4caf50; }
.card-orange { border-left: 5px solid #ff9800; }

.stat-content small {
  color: var(--text-muted);
  font-weight: 700;
  letter-spacing: 1px;
  font-size: 0.75rem;
  display: block;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 2.2rem;
  font-weight: 800;
  color: var(--text-bright);
}

.stat-icon {
  font-size: 2rem;
  opacity: 0.2;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  font-weight: 600;
  color: #ff9800;
  font-size: 0.9rem;
}

.dot {
  width: 8px;
  height: 8px;
  background: #4caf50;
  border-radius: 50%;
}

.pulse {
  animation: pulse-animation 2s infinite;
}

@keyframes pulse-animation {
  0% { box-shadow: 0 0 0 0px rgba(76, 175, 80, 0.7); }
  100% { box-shadow: 0 0 0 10px rgba(76, 175, 80, 0); }
}

@media (max-width: 768px) {
  .stats-container { flex-direction: column; }
}
</style>