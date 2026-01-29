<template>
    <div class="grid grid-cols-2 gap-5">
        <FolderSelector header="New Submission" />
        <FolderSelector header="New Submission" />
    </div>
</template>

<script setup lang="ts">
import FolderSelector from '../components/FolderSelector.vue';
import ContainerComponent from '@jplag/ui-components/base/ContainerComponent.vue';

</script>