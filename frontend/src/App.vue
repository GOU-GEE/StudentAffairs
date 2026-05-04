<template>
  <router-view />
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'

// 鼠标跟踪 - 更新 CSS 变量，驱动 body::before 光晕效果
const handleMouseMove = (e) => {
  document.documentElement.style.setProperty('--cursor-x', e.clientX + 'px')
  document.documentElement.style.setProperty('--cursor-y', e.clientY + 'px')
}

onMounted(() => window.addEventListener('mousemove', handleMouseMove))
onUnmounted(() => window.removeEventListener('mousemove', handleMouseMove))
</script>

<style>
/* 让 body::before 读取 :root 的 CSS 变量 */
:root {
  --cursor-x: 50%;
  --cursor-y: 50%;
}
</style>
