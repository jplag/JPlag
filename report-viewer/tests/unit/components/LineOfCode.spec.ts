import { shallowMount } from '@vue/test-utils'
import LineOfCode from '@/components/LineOfCode.vue'

test("loc", () => {
    const wrapper = shallowMount(LineOfCode, {
        props: {
            visible: true,
            text: "Hello World",
            lineNumber: 3,
            color: "white"
        }
    });
    const preText = wrapper.find({ ref: 'lineRef'});
    expect(preText.text()).toContain('Hello World');
    expect(preText.text()).toContain('3');

    wrapper.vm.$emit('lineSelected');
    expect(wrapper.emitted().lineSelected).toBeTruthy();
})