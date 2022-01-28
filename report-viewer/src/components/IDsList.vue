<template>
<div class="container">
  <div class="column-list">
    <p v-for="(id, index) in ids" :key="index" @click="$emit('idSent', id)">
      {{ id }}
    </p>
  </div>
  <button @click="$emit('idSent', ...ids)">Hide/Show all</button>
</div>
</template>

<script>
import { defineComponent } from "vue";

export default defineComponent({
  name: "IDsList",
  props: {
    ids: {
      type: Array,
      required: true
    }
  },
  setup(props) {
    // eslint-disable-next-line vue/no-setup-props-destructure
    const savedIds = props.ids
    return {
      savedIds
    }
  }
})
</script>

<style scoped>
button {
  height: 25%;
  background: inherit;
  font-family: "JetBrains Mono", serif;
  border: none;
  border-radius: 10px;
}

button:hover {
  background: var(--primary-color-dark);
  cursor: pointer;
}

.container {
  display: flex;
  justify-content: space-between;
}

.column-list {
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
}

.column-list > p:hover {
  background: var(--primary-color-dark);
  cursor: pointer;
}
</style>