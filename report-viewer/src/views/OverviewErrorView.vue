<template>
  <div class="container">
    <img alt="JPlag" src="@/assets/logo-nobg.png" />
    <h1>404 Error</h1>
    <h2>Overview.json can't be found！</h2>
    <h5>
      <router-link to="/" class="back-to-fileUpload">back to FileUpload page</router-link>
    </h5>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "OverviewErrorView",
});
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: var(--primary-color-light);
}
h2 {
  font-size: 24px;
  color: #191919;
  padding-bottom: 16px
}
.back-to-fileUpload {
  width: 200px;
  height: 38px;
  background: #c9141d;
  line-height: 38px;
  font-size: 16px;
  color: #fff;
  text-align: center;
  display: inline-block;
  border-radius: 2px
}
</style>