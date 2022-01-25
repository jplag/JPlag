<template>
  <div class="files-container" :id="containerId">
    <h1>Files of {{ filesOwner }}</h1>
    <VueDraggableNext>
      <CodePanel v-for="(file, index) in Object.keys(files)"
                 :lines="files[file].lines"
                 :title="file"
                 :file-index="index"
                 :matches="!matches[file] ? [] : matches[file]"
                 :key="file.concat(index)"
                 :collapse="files[file].collapsed"
                 @toggle-collapse="$emit('toggle-collapse', file)"
                 @line-selected="lineSelected"
                 :panel-id="1"
      />
    </VueDraggableNext>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import CodePanel from "@/components/CodePanel";
import { VueDraggableNext } from "vue-draggable-next"

export default defineComponent({
  name: "FilesContainer",
  components: { CodePanel, VueDraggableNext},
  props: {
    containerId: {
      type: String,
      required: true
    },
    filesOwner: {
      type: String,
      required: true
    },
    files: {
      required: true,

    },
    matches: {
      required: true
    }
  },

  setup(props, { emit }) {

    const lineSelected = (e, index, file, line) => {
      emit('lineSelected', e, index, file, line)
    }
    return {
      lineSelected
    }
  }
})
</script>

<style scoped>
h1 {
  text-align: center;
}
.files-container {
  display: flex;
  flex-wrap: nowrap;
  flex-direction: column;
  padding-top: 1%;
  width: 100%;
  overflow: auto;
}
</style>