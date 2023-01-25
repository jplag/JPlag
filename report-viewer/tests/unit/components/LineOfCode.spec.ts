import { mount } from '@vue/test-utils'
import LineOfCode from '@/components/LineOfCode.vue'

test("loc", () => {
    const wrapper = mount(LineOfCode, {
        props: {
            visible: true,
            text: "Hello World",
            lineNumber: 3,
            color: "white"
        }
    })

    expect(wrapper.text()).toContain('Hello World');
})