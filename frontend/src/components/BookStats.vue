<template>
  <section class="stats">
    <div class="card card-blue">
      <small>TOTAL LIBROS</small>
      <div class="value">{{ total }}</div>
    </div>
    <div class="card card-green">
      <small>AUTORES ÚNICOS</small>
      <div class="value">{{ autores }}</div>
    </div>
    <div class="card card-orange">
      <small>ESTADO BACKEND</small>
      <div class="status">📡 MongoDB Conectado</div>
    </div>
  </section>
</template>

<script setup>
defineProps(['total', 'autores']);
</script>

<style scoped>
.stats { display: flex; gap: 20px; margin-bottom: 30px; }
.card { flex: 1; padding: 20px; border-radius: 12px; border-left: 6px solid; background: #1a1a1a; }
.card-blue { border-color: #3f51b5; }
.card-green { border-color: #4caf50; }
.card-orange { border-color: #ff9800; }
.value { font-size: 2.5rem; font-weight: bold; margin-top: 5px; }
.status { margin-top: 10px; font-weight: 500; color: #ff9800; }
</style>