<template>
  <div class="container">
    <img alt="JPlag" src="@/assets/logo-nobg.png" />
    <error-message :message="message"/>
    <error-router :to="to" :routerInfo="routerInfo"/>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import ErrorMessage from "@/components/ErrorMessage.vue";
import ErrorRouter from "@/components/ErrorRouter.vue";

export default defineComponent({
  name: "OverviewErrorView",
  components: {ErrorRouter, ErrorMessage},
  setup() {
    const message = history.state.message;
    const to = history.state.to;
    const routerInfo =history.state.routerInfo;
    return{
      message,
      to,
      routerInfo
    }
  },
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
</style>