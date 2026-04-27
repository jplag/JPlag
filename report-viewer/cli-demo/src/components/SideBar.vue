<template>
    <div class="side min-w-36">
        <div
            v-for="p of paths"
            :key="p"
            class="border-container-light border-b"
        >
            <RouterLink 
                :to="{name: p}"
                :class="{'font-bold text-accent': router.currentRoute.value.name == p}"
            >
                {{ p }}
            </RouterLink></div>

        <div class="border-b-0!">
            <div class="bg-accent border-accent-dark border rounded-md flex items-center justify-center text-white" @click="run">
                Run
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { router } from '../router';
const paths = ['Submissions', 'Language', 'Comparison', 'Report Viewer']

function run() {
    router.push({ name: 'Report Viewer' })
}
</script>

<style scoped>
.side div {
    padding: 10px 20px 10px 20px;
}
</style>