<!--
  List containing all the clusters in which both comparison submissions participate.
-->
<template>
  <div class="wrapper">
    <h1>Clusters for comparison {{ comparison.firstSubmissionId }} > {{ comparison.secondSubmissionId }}</h1>
    <p v-for="( cluster, index ) in clusters" :key="index" @click="toggleDialog">
      {{ index }} - {{ cluster.averageSimilarity }}
      <GDialog v-model="dialog" fullscreen>
        <button @click="toggleDialog">Close</button>
        <ClusterRadarChart :cluster="cluster"></ClusterRadarChart>
      </GDialog>
    </p>
  </div>
</template>

<script>
import {defineComponent, ref} from "vue";
import {GDialog} from "gitart-vue-dialog";
import ClusterRadarChart from "@/components/ClusterRadarChart";

export default defineComponent({
  name: "ClustersList",
  components: {ClusterRadarChart, GDialog},
  props: {
    comparison: {},
    clusters: Array
  },
  setup() {
    const dialog = ref(false)
    const toggleDialog = () => dialog.value = !dialog.value
    return {
      dialog,
      toggleDialog
    }
  }
})
</script>

<style scoped>
.wrapper {
  display: flex;
  flex-direction: column;
  padding: 1%;
  background: var(--background-color);
  font-family: Avenir, Helvetica, Arial, sans-serif;
  color: var(--on-primary-color);
}

p {
  font-family: inherit;
  font-weight: bold;
  border-radius: 10px;
  background: var(--primary-color-light);
  box-shadow: var(--shadow-color) 3px 3px 2px;
  padding: 1%;
}

p:hover {
  background: var(--primary-color-dark);
  cursor: pointer;
}

</style>