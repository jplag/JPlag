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

## Jar Download

You can download the JPlag jar file directly from the GitHub releases page:

<VPButton v-if="latestReleaseVersion" :text="`Version ${latestReleaseVersion.substring(1)}`" href="https://www.github.com/jplag/JPlag/releases/latest" />
<VPButton text="Older Versions" href="https://www.github.com/jplag/JPlag/releases" theme="alt" />

<script setup>
import { ref } from 'vue'
import { VPButton } from 'vitepress/theme'

const releaseInfo = ref('The latest released version is available on GitHub.')
const latestReleaseVersion = ref(undefined);
const url = 'https://api.github.com/repos/JPlag/JPlag/releases/latest';

fetch(url).then(response => {
    response.json().then(data => {
    latestReleaseVersion.value = data.tag_name;

    const rawDate = new Date(data.published_at);
    const latestReleaseDate = rawDate.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'});

    releaseInfo.value = `The latest released version ${latestReleaseVersion.value} was released on ${latestReleaseDate}.`
})});
</script>

<style module>
a[class*="VPButton"] {
    text-decoration: none !important;
}
</style>