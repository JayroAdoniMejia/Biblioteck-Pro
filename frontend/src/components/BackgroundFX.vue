<template>
  <div class="fx-container" :class="isDark ? 'dark-particles' : 'light-confetti'">
    <div v-for="n in 30" :key="n" class="particle"></div>
  </div>
</template>

<script setup>
defineProps(['isDark']);
</script>

<style scoped>
.fx-container {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  pointer-events: none;
  z-index: 0; /* Por detrás de las cards pero sobre el fondo base */
  overflow: hidden;
}

.particle {
  position: absolute;
  top: -20px;
  width: 6px; height: 6px;
  animation: fall linear infinite;
}

/* --- MODO DARK: Partículas Blancas --- */
.dark-particles .particle {
  background: white;
  border-radius: 50%;
  opacity: 0.4;
  box-shadow: 0 0 8px white;
}

/* --- MODO LIGHT: Confeti --- */
.light-confetti .particle {
  border-radius: 2px;
}
.light-confetti .particle:nth-child(3n) { background: #ff7675; }
.light-confetti .particle:nth-child(3n+1) { background: #74b9ff; }
.light-confetti .particle:nth-child(3n+2) { background: #55efc4; }

@keyframes fall {
  0% { transform: translateY(0vh) rotate(0deg); opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { transform: translateY(100vh) rotate(360deg); opacity: 0; }
}

/* Distribución aleatoria simple */
.particle:nth-child(n) { left: calc(10% * var(--i, 1)); animation-duration: calc(5s + var(--i, 1) * 1s); }
/* Para no usar JS, distribuimos manualmente algunos */
.particle:nth-child(1) { left: 5%; animation-delay: 0s; }
.particle:nth-child(2) { left: 15%; animation-delay: 2s; }
.particle:nth-child(3) { left: 25%; animation-delay: 4s; }
.particle:nth-child(4) { left: 40%; animation-delay: 1s; }
.particle:nth-child(5) { left: 60%; animation-delay: 3s; }
.particle:nth-child(6) { left: 85%; animation-delay: 5s; }
/* ... añade más si quieres más densidad ... */
</style>