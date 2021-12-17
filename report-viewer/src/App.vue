<template>
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <router-view/>
</template>

<style>
@font-face {
  font-family: "JetBrains Mono";
  src: url("./fonts/jetbrains-mono/JetBrainsMono-Light.ttf");
}

@font-face {
  font-family: "JetBrains Mono";
  src: url("./fonts/jetbrains-mono/JetBrainsMono-Bold.ttf");
  font-weight: bold;
}

:root {
  --primary-color: #f8534b;
  --primary-color-light: #ff8777;
  --primary-color-dark: #be1522;
  --secondary-color: #ff9f8d;
  --secondary-color-light: #ffd1bd;
  --secondary-color-dark: #c96f60;
  --tertiary-color: #95a8f1;
  --tertiary-color-light: #c8d9ff;
  --tertiary-color-dark: #6479be;
  --quaternary-color: #f3eed9;
  --quaternary-color-light: #ffffff;
  --quaternary-color-dark: #c0bca8;
  --background-color: #ECECEC;
  --background-color-accent: #FFFFFF;
  --on-primary-color: #303030;
  --on-primary-color-accent: #FFFFFF;
  --on-secondary-color: #000000;
  --shadow-color: #777777;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

html, body, #app {
  height: 100%;
  width: 100%;
  margin: 0;
}
</style>
