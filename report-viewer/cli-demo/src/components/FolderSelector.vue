<template>
    <ContainerComponent>
        <div class="flex flex-col gap-2">
            <div class="flex items-center gap-2">
                <h1 class="text-xl flex-1">{{ header }}</h1>
                <ButtonComponent @click="fileChooser()"><FontAwesomeIcon :icon="faPlus" /> Add Submission Folder</ButtonComponent>
                <ButtonComponent @click="fileChooser()"><FontAwesomeIcon :icon="faPlus" /> Add Single Submission</ButtonComponent>
            </div>
            <div>
                <div
                    v-for="l of list"
                    :key="l"
                >
                    <span>{{ l }}</span>
                    <FontAwesomeIcon class="float-right" :icon="faTrash" @click="removeItem(l)" />
                </div>
            </div>
        </div>
    </ContainerComponent>
</template>

<script setup lang="ts">
import ButtonComponent from '@jplag/ui-components/base/ButtonComponent.vue';
import ContainerComponent from '@jplag/ui-components/base/ContainerComponent.vue';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faPlus, faTrash } from '@fortawesome/free-solid-svg-icons'
import { ref } from 'vue';

defineProps({
    header: {
        type: String,
        required: true
    }
})

const list = ref<string[]>([])

function removeItem(item: string) {
    list.value = list.value.filter(i => i !== item);
}

function fileChooser() {
    selectFolders().then((folders: string[]) => {
        list.value.push(...folders);
    });
    
}

function selectFolders() {
  return new Promise<string[]>((resolve) => {
    const input = document.createElement("input");

    input.type = "file";
    input.multiple = true;
    input.webkitdirectory = true;

    input.addEventListener("change", () => {
        if (!input.files) {
            resolve([]);
            return;
        }
      const files = Array.from(input.files);

      const folderNames = [
        ...new Set(
          files.map(file => file.webkitRelativePath.split("/")[0])
        )
      ];

      resolve(folderNames);
    });

    // Must be triggered by a user gesture (click, key press, etc.)
    input.click();
  });
}

</script>