<template>
  <div class="wrapper">
    <div class="label">
      <p class="label-text">{{ label }}</p>
      <div class="additional-info" v-if="hasAdditionalInfo">
        <p class="additional-label">{{ additionalInfoLabel }}</p>
        <img alt="icon" src="@/assets/double_arrow_white_18dp.svg">
      </div>
    </div>
    <p class="value-text">{{ value }}</p>
    <hr>
  </div>
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
    additionalInfoLabel : String,
    additionalInfo : [String]
  },
  setup() {

    return {

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
  flex-direction: column;
  align-items: stretch;
}

.label {
  display: flex;
  justify-content: space-between;
}

.label-text {
  font-weight: lighter;
  text-align: left;
  color: #ECECEC;
}

.value-text {
  font-weight: bold;
  text-align: left;
  color: white;
}

.additional-info {
  display: flex;
  justify-content: flex-end;
}

.additional-label {
  font-weight: lighter;
  font-size: smaller;
  white-space: nowrap;
  color: #ECECEC;
  margin: 4% 0 0 0;
}

</style>