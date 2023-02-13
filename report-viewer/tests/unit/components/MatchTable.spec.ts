import {shallowMount} from '@vue/test-utils'
import MatchTable from "@/components/MatchTable.vue";
import store from "@/store/store";

beforeEach(()=>{
    let fileIdToDisplayNameMap = store.state.fileIdToDisplayName;
    fileIdToDisplayNameMap.set("A","A");
    fileIdToDisplayNameMap.set("C","C");
})

test("MT", () => {
    const wrapper = shallowMount(MatchTable,{
        props: {
            id1: "A",
            id2: "C",
            matches: [{firstFile:"A\\GSTiling.java",secondFile:"C\\GSTiling.java",startInFirst:6,endInFirst:247,startInSecond:13,endInSecond:254,tokens:345,color:"\\hsla(0, 80%, 50%, 0.3)"}],
        },
    });
    const matchesInfo = wrapper.findAll(".td-content p");
    expect(matchesInfo[0].text()).toContain("A\\GSTiling.java");
    expect(matchesInfo[1].text()).toContain("(6 - 247)");
    expect(matchesInfo[2].text()).toContain("C\\GSTiling.java");
    expect(matchesInfo[3].text()).toContain("(13 - 254)");
    const matchToken = wrapper.findAll("td");
    expect(parseInt(matchToken[2].text())).toBe(345);
})