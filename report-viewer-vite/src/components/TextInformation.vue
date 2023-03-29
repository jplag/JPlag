<!--
  A container displaying simple text information. It may include additional information which is being displayed
  in a drop down container.
-->
<template>
  <div class="wrapper">
    <div class="text-container">
      <p class="label-text">{{ label }}</p>
      <p :class="{ 'anonymous' : anonymous }" :title="anonymous ? '' : value" class="value-text">
        {{ anonymous ? "Hidden" : value }}</p>
    </div>
    <button :class="{ hidden : !hasAdditionalInfo }" class="collapse-button" @click="toggleIsCollapsed">
      <img v-if="isCollapsed" alt="hide info" src="../assets/keyboard_double_arrow_up_black_18dp.svg">
      <img v-else alt="additional info" src="../assets/keyboard_double_arrow_down_black_18dp.svg">
    </button>
  </div>
  <div :class="{ hidden : !isCollapsed }" class="additional-info">
    <p class="additional-info-title">{{ additionalInfoTitle }}</p>
    <slot></slot>
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
      default: false
    },
    additionalInfoTitle: {
      type: String,
      default: "",
    },
    /**
     * Indicates whether the value should be hidden.
     */
    anonymous: {
      type: Boolean,
      default: false
    }
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
  background: linear-gradient(to right, #E2EAFC, transparent, transparent);
  width: 100%;
  box-shadow: #D7E3FC 0 1px;
}

.anonymous {
  filter: blur(1px);
  color: #777777;
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

.label-text {
  color: var(--on-primary-color);
  text-align: left;
}

.value-text {
  font-weight: bold;
  text-align: left;
  color: var(--on-primary-color);
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
  background: var(--secondary-color);
  padding: 2%;
  margin: 3% 0;
  box-shadow: inset var(--shadow-color) 0 0 3px;
  border-radius: 10px;
  font-family: "JetBrains  NL", serif;
  font-size: smaller;
}

.additional-info-title {
  margin-bottom: 1%;
}

.hidden {
  display: none !important;
}

</style>