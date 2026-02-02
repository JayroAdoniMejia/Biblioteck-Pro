<script setup>
import { computed } from 'vue';

const props = defineProps({
  isDark: {
    type: Boolean,
    default: true
  }
});

// Generamos un array fijo para las partículas para evitar re-renders innecesarios
const particles = Array.from({ length: 20 });
</script>

<template>
  <div class="fx-container" :class="{ 'mode-dark': isDark, 'mode-light': !isDark }">
    <template v-if="isDark">
      <div v-for="n in 3" :key="'layer-'+n" :class="'stars layer-' + n"></div>
    </template>

    <template v-else>
      <div v-for="i in particles" :key="'p-'+i" class="confetti"></div>
    </template>
  </div>
</template>

<style scoped>
.fx-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0; /* Por detrás de todo */
  pointer-events: none;
  overflow: hidden;
  transition: background 0.5s ease;
}

/* --- MODO OSCURO: POLVO ESTELAR --- */
.mode-dark {
  background: radial-gradient(ellipse at bottom, #1B2735 0%, #090A0F 100%);
}

.stars {
  position: absolute;
  top: 0;
  left: 0;
  width: 200%;
  height: 200%;
}

.layer-1 {
  background: radial-gradient(circle, #ffffff 1px, transparent 1px);
  background-size: 100px 100px;
  animation: moveUp 100s linear infinite;
  opacity: 0.3;
}

.layer-2 {
  background: radial-gradient(circle, #e0a82e 1.5px, transparent 1.5px);
  background-size: 150px 150px;
  animation: moveUp 70s linear infinite;
  opacity: 0.2;
}

@keyframes moveUp {
  from { transform: translateY(0); }
  to { transform: translateY(-50%); }
}

/* --- MODO CLARO: CONFETI --- */
.mode-light {
  background: #f8fafc;
}

.confetti {
  position: absolute;
  width: 8px;
  height: 8px;
  background: var(--accent, #8957e5);
  top: -10px;
  border-radius: 2px;
  animation: fall var(--d) linear infinite;
  opacity: 0.6;
  left: var(--l);
}

/* Generamos variaciones aleatorias para el confeti */
.confetti:nth-child(4n) { background: #2ecc71; --d: 12s; --l: 10%; }
.confetti:nth-child(4n+1) { background: #3498db; --d: 15s; --l: 35%; }
.confetti:nth-child(4n+2) { background: #e74c3c; --d: 10s; --l: 65%; }
.confetti:nth-child(4n+3) { background: #f1c40f; --d: 18s; --l: 85%; }

@keyframes fall {
  to {
    transform: translateY(105vh) rotate(360deg);
  }
}
</style>