<!--
  Offers a un-styled scrollable container
-->
<template>
  <div class="overflow-y-auto print:overflow-y-visible">
    <div class="max-h-0 min-h-full print:max-h-none">
      <slot></slot>
    </div>
  </div>
</template>
