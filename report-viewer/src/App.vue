<template>
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <router-view/>
</template>

<style>
@font-face {
  font-family: "Lobster";
  src: url("./fonts/Lobster.ttf");
}

@font-face {
  font-family: "League Spartan";
  src: url("./fonts/LeagueSpartan.ttf");
}

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
  --primary-color: #be1523;
  --primary-color-light: #f8534c;
  --primary-color-dark: #860000;
  --secondary-color: #ff9f8e;
  --secondary-color-light: #ffd1be;
  --secondary-color-dark: #c96f61;
  --tertiary-color: #8eaaf2;
  --tertiary-color-light: #c1dbff;
  --tertiary-color-dark: #5c7bbf;
  --quaternary-color: #f3eed9;
  --quaternary-color-light: #ffffff;
  --quaternary-color-dark: #c0bca8;
  --background-color: #FFF5F2;
  --background-color-accent: #FFFFFF;
  --on-primary-color: #303030;
  --on-primary-color-accent: #f3eed9;
  --on-secondary-color: #000000;
  --on-background-color: #303030;
  --shadow-color: #777777;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

html, body, #app {
  height: 100%;
  width: 100%;
  margin: 0;
}

</style>
