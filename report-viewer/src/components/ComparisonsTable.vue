<!--
  Table which display all of the comparisons with their participating ids and similarity percentage for the selected metric.
-->
<template>
  <table class="table">
    <tr class="head-row">
      <th>No.</th>
      <th>Submission 1</th>
      <th></th>
      <th>Submission 2</th>
      <th>Match %</th>
    </tr>
  </table>

  <RecycleScroller
      style="height: 650px; overflow: auto;"
      class="scroller"
      :items="topComparisons"
      :item-size="48"
      key-field="id"
      v-slot="{ item }"
  >
    <table class="table" style="width: 100%">
      <tr v-if="item.id % 2 === 0" class="selectableEven">
      <td
        @click="
          navigateToComparisonView(
            item.firstSubmissionId,
            item.secondSubmissionId
          )
        "
        class="td1"
      >
        {{ item.id }}.
      </td>
      <td
        @click="
          navigateToComparisonView(
            item.firstSubmissionId,
            item.secondSubmissionId
          )
        "
        :class="{
          'anonymous-style': isAnonymous(item.firstSubmissionId),
        }"
        class="td2"
      >
        {{
          isAnonymous(item.firstSubmissionId)
            ? "Hidden"
            : displayName(item.firstSubmissionId)
        }}
      </td>
      <td
        @click="
          navigateToComparisonView(
            item.firstSubmissionId,
            item.secondSubmissionId
          )
        "
        class="td3"
      >
        <img alt=">>" src="@/assets/double_arrow_black_18dp.svg" />
      </td>
      <td
        @click="
          navigateToComparisonView(
            item.firstSubmissionId,
            item.secondSubmissionId
          )
        "
        :class="{
          'anonymous-style': isAnonymous(item.secondSubmissionId),
        }"
        class="td4"
      >
        {{
          isAnonymous(item.secondSubmissionId)
            ? "Hidden"
            : displayName(item.secondSubmissionId)
        }}
      </td>
      <td
        @click="
          navigateToComparisonView(
            item.firstSubmissionId,
            item.secondSubmissionId
          )
        "
        class="td5"
      >
        {{ formattedMatchPercentage(item.similarity) }}
      </td>
      <td style="width: 5%">
        <img
          v-if="
            isInCluster(
              item.firstSubmissionId,
              item.secondSubmissionId
            )
          "
          alt=">>"
          src="@/assets/keyboard_double_arrow_down_black_18dp.svg"
          @click="toggleDialog(item.id-1)"
        />
      </td>
      <GDialog
        v-if="
          isInCluster(
            item.firstSubmissionId,
            item.secondSubmissionId
          )
        "
        v-model="dialog[item.id-1]"
      >
        <ClustersList
          :clusters="
            getClustersFor(
              item.firstSubmissionId,
              item.secondSubmissionId
            )
          "
          :comparison="item"
        />
      </GDialog>
      </tr>
      <tr v-else class="selectableOdd">
        <td
            @click="
          navigateToComparisonView(
            item.firstSubmissionId,
            item.secondSubmissionId
          )
        "
            class="td1"
        >
          {{ item.id }}.
        </td>
        <td
            @click="
          navigateToComparisonView(
            item.firstSubmissionId,
            item.secondSubmissionId
          )
        "
            :class="{
          'anonymous-style': isAnonymous(item.firstSubmissionId),
        }"
            class="td2"
        >
          {{
            isAnonymous(item.firstSubmissionId)
                ? "Hidden"
                : displayName(item.firstSubmissionId)
          }}
        </td>
        <td
            @click="
          navigateToComparisonView(
            item.firstSubmissionId,
            item.secondSubmissionId
          )
        "
            class="td3"
        >
          <img alt=">>" src="@/assets/double_arrow_black_18dp.svg" />
        </td>
        <td
            @click="
          navigateToComparisonView(
            item.firstSubmissionId,
            item.secondSubmissionId
          )
        "
            :class="{
          'anonymous-style': isAnonymous(item.secondSubmissionId),
        }"
            class="td4"
        >
          {{
            isAnonymous(item.secondSubmissionId)
                ? "Hidden"
                : displayName(item.secondSubmissionId)
          }}
        </td>
        <td
            @click="
          navigateToComparisonView(
            item.firstSubmissionId,
            item.secondSubmissionId
          )
        "
            class="td5"
        >
          {{ formattedMatchPercentage(item.similarity) }}
        </td>
        <td style="width: 5%">
          <img
              v-if="
            isInCluster(
              item.firstSubmissionId,
              item.secondSubmissionId
            )
          "
              alt=">>"
              src="@/assets/keyboard_double_arrow_down_black_18dp.svg"
              @click="toggleDialog(item.id-1)"
          />
        </td>
        <GDialog
            v-if="
          isInCluster(
            item.firstSubmissionId,
            item.secondSubmissionId
          )
        "
            v-model="dialog[item.id-1]"
        >
          <ClustersList
              :clusters="
            getClustersFor(
              item.firstSubmissionId,
              item.secondSubmissionId
            )
          "
              :comparison="item"
          />
        </GDialog>
      </tr>
    </table>
  </RecycleScroller>
</template>

<script lang="ts">
import { defineComponent, Ref, ref } from "vue";
import router from "@/router";
import { GDialog } from "gitart-vue-dialog";
import ClustersList from "@/components/ClustersList.vue";
import { useStore } from "vuex";
import { Cluster } from "@/model/Cluster";
import { ComparisonListElement } from "@/model/ComparisonListElement";
import { ClusterListElement } from "@/model/ClusterListElement";

export default defineComponent({
  name: "ComparisonsTable",
  components: { ClustersList, GDialog },
  props: {
    topComparisons: {
      type: Array<ComparisonListElement>,
      required: true,
    },
    clusters: {
      type: Array<Cluster>,
      required: true,
    },
  },
  setup(props) {
    const store = useStore();
    let formattedMatchPercentage = (num: number) => (num * 100).toFixed(2);
    const dialog: Ref<Array<boolean>> = ref([]);
    props.topComparisons.forEach(() => dialog.value.push(false));
    const displayName = (submissionId: string) =>
      store.getters.submissionDisplayName(submissionId);

    const toggleDialog = (index: number) => {
      dialog.value[index] = true;
    };

    const navigateToComparisonView = (firstId : string, secondId: string) => {
      if (!store.state.single) {
        router.push({
          name: "ComparisonView",
          params: { firstId, secondId },
        });
      }
    };

    const isInCluster = (id1: string, id2: string) => {
      return props.clusters.some(
        (c: Cluster) => c.members.includes(id1) && c.members.includes(id2)
      );
    };

    const isAnonymous = (id: string) => {
      return store.state.anonymous.has(id);
    };

    const getParticipatingMatchesForId = (
      id: string,
      others: Array<string>
    ) => {
      let matches: Array<{ matchedWith: string; percentage: number }> = [];
      props.topComparisons.forEach((comparison) => {
        if (
          comparison.firstSubmissionId.includes(id) &&
          others.includes(comparison.secondSubmissionId)
        ) {
          matches.push({
            matchedWith: comparison.secondSubmissionId,
            percentage: comparison.similarity,
          });
        } else if (
          comparison.secondSubmissionId.includes(id) &&
          others.includes(comparison.firstSubmissionId)
        ) {
          matches.push({
            matchedWith: comparison.firstSubmissionId,
            percentage: comparison.similarity,
          });
        }
      });
      return matches;
    };

    const clustersWithParticipatingMatches: Array<ClusterListElement> =
      props.clusters.map((cluster) => {
        let membersArray = new Map<
          string,
          Array<{ matchedWith: string; percentage: number }>
        >();
        cluster.members.forEach((member: string) => {
          let others = cluster.members.filter((m) => !m.includes(member));
          membersArray.set(
            member,
            getParticipatingMatchesForId(member, others)
          );
        });

        return {
          averageSimilarity: cluster.averageSimilarity,
          strength: cluster.strength,
          members: membersArray,
        };
      });

    const getClustersFor = (
      id1: string,
      id2: string
    ): Array<ClusterListElement> => {
      return clustersWithParticipatingMatches.filter(
        (c) => c.members.has(id1) && c.members.has(id2)
      );
    };

    return {
      clustersWithParticipatingMatches,
      dialog,

      displayName,
      isAnonymous,
      getClustersFor,
      toggleDialog,
      formattedMatchPercentage,
      navigateToComparisonView,
      isInCluster,
    };
  },
});
</script>

<style scoped>
.table {
  table-layout: fixed;
  border-collapse: collapse;
  font-size: larger;
  text-align: center;
}

th {
  margin: 0;
  padding-top: 2%;
  padding-bottom: 2%;
  color: var(--on-primary-color);
}

.td1 {
  padding-right: 10%;
  padding-top: 3%;
  padding-bottom: 3%;
}

.td2 {
  padding-right: 5%;
   padding-top: 3%;
   padding-bottom: 3%;
 }

.td3 {
  padding-right: 5%;
   padding-top: 3%;
   padding-bottom: 3%;
 }

.td4 {
  padding-right: 5%;
   padding-top: 3%;
   padding-bottom: 3%;
 }

.td5 {
  padding-left: 6%;
   padding-top: 3%;
   padding-bottom: 3%;
 }

.anonymous-style {
  color: #777777;
  filter: blur(1px);
}

.head-row {
  background: var(--primary-color-light);
}

.selectableEven{
  background: var(--primary-color-light);
  cursor: pointer;
}

.selectableOdd{
  background: var(--secondary-color);
  cursor: pointer;
}

.selectableEven:hover {
  background: var(--primary-color-dark) !important;
  cursor: pointer;
}

.selectableOdd:hover {
  background: var(--primary-color-dark) !important;
  cursor: pointer;
}
</style>
