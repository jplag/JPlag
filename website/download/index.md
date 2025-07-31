---
prev: false
next: false
aside: false
---

# Download

We provide multiple ways to download and use JPlag.
The easiest way to get started is downloading a [JPlag release from GitHub](https://github.com/jplag/JPlag/releases/latest).
Alternatively, all major artifacts are available on [Maven Central](https://maven-badges.herokuapp.com/maven-central/de.jplag/jplag).
Afterwards, visit [Getting Started](/wiki/gettingstarted.md) to learn more about using the analysis.

::: info Current Release
{{releaseInfo}}
:::

::: warning
This page is currently under construction.
Until it is ready, please visit [the project wiki](https://github.com/jplag/JPlag/wiki).
:::

<script setup>
import { ref } from 'vue'
import { VPButton } from 'vitepress/theme'

const releaseInfo = ref('The latest released version is available on GitHub.')
const url = 'https://api.github.com/repos/JPlag/JPlag/releases/latest';

fetch(url).then(response => {
    response.json().then(data => {
    const latestReleaseVersion = data.tag_name;

    const rawDate = new Date(data.published_at);
    const latestReleaseDate = rawDate.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'});

    releaseInfo.value = `The latest released version ${latestReleaseVersion} was released on ${latestReleaseDate}.`
})});
</script>

<style module>
a[class*="VPButton"] {
    text-decoration: none !important;
}
</style>