<template>
  <Transition name="toast">
    <div v-if="visible" class="toast-notification" :class="tipo">
      <span class="toast-icon">{{ icono }}</span>
      <span class="toast-text">{{ mensaje }}</span>
    </div>
  </Transition>
</template>

<script setup>
defineProps(['visible', 'mensaje', 'tipo', 'icono']);
</script>

<style scoped>
.toast-notification {
  position: fixed;
  bottom: 30px;
  right: 30px;
  background: var(--bg-card);
  border: 1px solid var(--accent);
  padding: 12px 24px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  z-index: 9999;
  box-shadow: 0 10px 30px rgba(0,0,0,0.5);
  backdrop-filter: blur(10px);
}

.success { border-color: var(--accent); color: var(--text-bright); }

.toast-enter-active { animation: slideUp 0.3s ease-out; }
.toast-leave-active { animation: slideUp 0.2s reverse ease-in; }

@keyframes slideUp {
  from { transform: translateY(100px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
</style>