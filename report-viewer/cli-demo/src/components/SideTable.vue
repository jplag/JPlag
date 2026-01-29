<template>
  <div
    class="border-container-border-light dark:border-container-border-dark bg-container-light dark:bg-container-dark flex overflow-hidden border-l"
  >
    <div
      class="flex h-full w-8 cursor-pointer items-center justify-center text-center align-middle text-2xl font-bold select-none"
      @click="expanded = !expanded"
    >
      {{ expanded ? '>' : '<' }}
    </div>

    <div v-if="expanded" class="max-h-full flex-1 overflow-hidden py-5 pr-5">
      <div class="row bg-transparent! font-bold">
        <div>#</div>
        <div>Name</div>
        <div>Languages</div>
        <div>LOC</div>
        <div>Encoding</div>
        <div># Tokens</div>
        <div>Status</div>
      </div>
      <div class="max-h-full overflow-auto">
        <div v-for="(submission, idx) in submissions" :key="submission.name" class="row">
          <div>{{ idx }}</div>
          <div>{{ submission.name }}</div>
          <div>
            <OptionComponent
              v-for="lang in submission.languages || []"
              :key="lang"
              class="my-1"
              :selected="true"
              >{{ lang }}</OptionComponent
            >
          </div>
          <div>{{ submission.linesOfCode || '' }}</div>
          <div>{{ submission.encoding || '' }}</div>
          <div>{{ submission.tokenCount || '' }}</div>
          <div>{{ submission.state || '' }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { LoadedSubmission } from '@/model/Submission'
import { OptionComponent } from '@jplag/ui-components/widget'

defineProps({
  submissions: {
    type: Array<LoadedSubmission>,
    required: true
  }
})

const expanded = defineModel('expanded', { type: Boolean, default: false })
</script>

<style scoped>
.row {
  display: flex;
  column-gap: 12px;
}
.row:nth-child(odd) {
  background-color: color-mix(in srgb, var(--color-container-border-light) 50%, #fff 50%);
}
.row > *:nth-child(1) {
  width: 45px;
  text-align: right;
}
.row > *:nth-child(2) {
  flex: 1;
  text-align: left;
  padding-left: 8px;
}
.row > *:nth-child(3) {
  width: 100px;
  text-align: center;
}
.row > *:nth-child(4) {
  width: 60px;
  text-align: center;
}
.row > *:nth-child(5) {
  width: 100px;
  text-align: center;
}
.row > *:nth-child(6) {
  width: 80px;
  text-align: center;
}
.row > *:nth-child(7) {
  width: 200px;
  text-align: left;
}
</style>
