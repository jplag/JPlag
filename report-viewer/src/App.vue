<template>
  <meta content="width=device-width, initial-scale=1, shrink-to-fit=no" name="viewport" />
  <div :class="{ dark: isDark }">
    <div class="min-h-screen max-h-fit max-w-screen text-black dark:text-amber-50">
      <RouterView />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { RouterView } from 'vue-router'

const isDark = ref(false)
</script>

<style>
@font-face {
  font-family: 'JetBrains Mono NL';
  src: url('./fonts/jetbrains-mono/JetBrainsMonoNL-Light.ttf');
}

@font-face {
  font-family: 'JetBrains Mono NL';
  src: url('./fonts/jetbrains-mono/JetBrainsMonoNL-Bold.ttf');
  font-weight: bold;
}

:root {
  --primary-color: #e2eafc;
  --primary-color-light: #edf2fb;
  --primary-color-dark: #d7e3fc;
  --secondary-color: #b6ccfe;
  --secondary-color-light: #c1d3fe;
  --secondary-color-dark: #abc4ff;

  --background-color: #ffffff;
  --on-primary-color: #303030;
  --on-secondary-color: #000000;
  --on-background-color: #303030;
  --shadow-color: #777777;
}
</style>
