---
prev: false
next: false
aside: false
---

# Publications

This page lists selected publications that present the foundation, application, and obfuscation resilience of JPlag.

<div>
  <ul>
    <PublicationLink v-for="entry in publications" :entry="entry" />
  </ul>
</div>

<script setup>
import { ref } from 'vue';
import { publications } from "./bib.js";
import PublicationLink from './PublicationLink.vue'
</script>
