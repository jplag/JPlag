<template>
  <h2>{{message}}</h2>
</template>

<script lang="ts">
export default {
  name: "ErrorMessage",
  props: {
    /**
     * Message of error.
     */
    message: {
      type: String,
      required: true,
    },
  },
}
</script>

<style scoped>
h2 {
  font-size: 24px;
  color: #191919;
  padding-bottom: 16px
}
</style>