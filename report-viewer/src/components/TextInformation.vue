<template>
  <div class="wrapper">
      <div class="text-container">
      <p class="label-text">{{ label }}</p>
      <p class="value-text" :title="value">{{ value }}</p>
      </div>
      <button class="collapse-button" :class="{ hidden : !hasAdditionalInfo }" @click="toggleIsCollapsed">
        <img v-if="isCollapsed" src="../assets/keyboard_double_arrow_up_white_18dp.svg" alt="hide info">
        <img v-else src="../assets/keyboard_double_arrow_down_white_18dp.svg" alt="additional info">
      </button>
  </div>
  <div class="additional-info" :class="{ hidden : !isCollapsed }">
    <p class="additional-info-title">{{ additionalInfoTitle }}</p>
    <p v-for="info in additionalInfo" :key="info">{{ info }}</p>
  </div>
  <hr>
</template>

<script>
import {defineComponent, ref} from "vue";

export default defineComponent({
  name: "TextInformation",
  props: {
    label: {
      type: String,
      required: true
    },
    value: {
      required: true
    },
    hasAdditionalInfo: {
      type: Boolean,
      required: true
    },
    additionalInfoTitle: String,
    additionalInfo : [String]
  },
  setup() {
    const isCollapsed = ref(false)

    const toggleIsCollapsed = () => isCollapsed.value = !isCollapsed.value

    return {
      isCollapsed,
      toggleIsCollapsed
    }
  }

})
</script>

<style scoped>
p {
  margin: 0;
  align-items: flex-start;
  font-size: larger;
}

hr {
  border: 0;
  height: 2px;
  background: linear-gradient(to right, #ea4848, transparent, transparent);
  width: 100%;
  box-shadow: #ea4864 0 1px;
}

.wrapper {
  display: flex;
  background: var(--primary-color-light);
  justify-content: space-between;
  border-radius: 10px;
}

.text-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.label {
  display: flex;
  justify-content: space-between;
}

.label-text {
  color: var(--on-primary-color);
  text-align: left;
}

.value-text {
  font-weight: bold;
  text-align: left;
  color: var(--on-primary-color-accent);
}

.collapse-button {
  display: flex;
  justify-content: center;
  align-items: center;
  background: transparent;
  border: none;
}

.collapse-button:hover {
  background: var(--primary-color-dark);
  border-radius: 10px;
}

.additional-info {
  display: flex;
  flex-direction: column;
  align-items: start;
  background: var(--quaternary-color);
  padding: 2%;
  margin: 3% 0;
  box-shadow: inset var(--shadow-color) 0 0 3px;
  border-radius: 10px;
  font-family: "JetBrains Mono";
  font-size: smaller;
}

.additional-info-title {
  margin-bottom: 1%;
}

.hidden {
  display: none !important;
}

</style>