<!--
  Table which display all of the comparisons with their participating ids and similarity percentage for the selected metric.
-->
<template>
  <table>
    <tr class="head-row">
      <th>No.</th>
      <th>Submission 1</th>
      <th></th>
      <th>Submission 2</th>
      <th>Match %</th>
    </tr>
  </table>

  <DynamicScroller
      v-if="topComparisons.length>0"
      class="scroller"
      :items="topComparisons"
      :min-item-size="48"
  >
    <template v-slot="{ item, index, active }">
      <DynamicScrollerItem
          :item="item"
          :active="active"
          :size-dependencies="[
          item.firstSubmissionId,
          item.secondSubmissionId,
        ]"
          :data-index="index"
      >
        <table class="inside-table">
          <tr :class="{'selectableEven': item.id % 2 === 0, 'selectableOdd': item.id % 2 !== 0}">
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
            <td class="td6">
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
      </DynamicScrollerItem>
    </template>
  </DynamicScroller>
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
.scroller{
  height: 650px;
}

table {
  table-layout: fixed;
  border-collapse: collapse;
  font-size: larger;
  text-align: center;
}

.inside-table {
  width: 100%;
}

th {
  margin: 0;
  padding-top: 2%;
  padding-bottom: 2%;
  color: var(--on-primary-color);
}

td {
  overflow-wrap: break-word;
  padding-top: 3%;
  padding-bottom: 3%;
}

.td1 {
  width: 3%;
}

.td2 {
  width: 18%;
  padding-left: 5%;
 }

.td3 {
   width: 3%;
 }

.td4 {
   width: 18%;
   padding-right: 5%;
 }

.td5 {
   width: 8%;
 }

.td6 {
  width: 2%;
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
