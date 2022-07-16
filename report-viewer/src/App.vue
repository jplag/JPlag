<template>
  <meta content="width=device-width, initial-scale=1, shrink-to-fit=no" name="viewport">
  <router-view/>
</template>

<style>
@font-face {
  font-family: "JetBrains Mono NL";
  src: url("./fonts/jetbrains-mono/JetBrainsMonoNL-Light.ttf");
}

@font-face {
  font-family: "JetBrains Mono NL";
  src: url("./fonts/jetbrains-mono/JetBrainsMonoNL-Bold.ttf");
  font-weight: bold;
}

:root {
  --primary-color: #E2EAFC;
  --primary-color-light: #EDF2FB;
  --primary-color-dark: #D7E3FC;
  --secondary-color: #B6CCFE;
  --secondary-color-light: #C1D3FE;
  --secondary-color-dark: #ABC4FF;

  --background-color: #FFFFFF;
  --on-primary-color: #303030;
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
