<template>
  <h5>
    <router-link :to="to" class="back">{{routerInfo}}</router-link>
  </h5>
</template>

<script>
export default {
  name: "ErrorRouter",
  props: {
    /**
     * Router of error.
     */
    to: {
      type: String,
      required: true,
    },
    /**
     * Info of router-link.
     */
    routerInfo: {
      type: String,
      required: true,
    },
  },
}
</script>

<style scoped>
.back {
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