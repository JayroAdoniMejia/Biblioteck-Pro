<template>
  <router-view />
</template>

<script setup>
// Nota: La lógica de aplicarTema se movió aquí para que afecte a toda la app
import { onMounted } from 'vue';

onMounted(() => {
  const temaGuardado = localStorage.getItem('theme') || 'dark';
  document.documentElement.setAttribute('data-theme', temaGuardado);
});
</script>

<style>
/* ESTILOS GLOBALES: Mantenemos las variables y el body aquí */
:root {
  --bg-main: #010409;
  --bg-header: #0d1117;
  --bg-card: #0d1117;
  --bg-input: #010409;
  --text-main: #8b949e;
  --text-bright: #f0f6fc;
  --accent: #8957e5;
  --accent-hover: #a371f7;
  --border: #30363d;
  --shadow: 0 8px 24px rgba(0,0,0,0.5);
}

[data-theme="light"] {
  --bg-main: #f6f8fa;
  --bg-header: #ffffff;
  --bg-card: #ffffff;
  --bg-input: #f3f4f6;
  --text-main: #24292f;
  --text-bright: #000000;
  --accent: #0969da;
  --accent-hover: #0a76f1;
  --border: #d0d7de;
  --shadow: 0 4px 12px rgba(0,0,0,0.05);
}

body {
  margin: 0;
  background-color: var(--bg-main);
  transition: background-color 0.3s ease;
  font-family: 'Inter', -apple-system, sans-serif;
}
</style>