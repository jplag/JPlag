import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import { MatchList, OptionComponent } from '../../../widget'
import { BaseCodeMatch, Match } from '@jplag/model'
import { ToolTipComponent } from '../../../base'

describe('MatchList', () => {
  it('display correct popup', () => {
    const wrapper = mount(MatchList, {
      props: {
        matches: [
          buildMatch(
            'firstFile.java',
            'secondFile.java',
            { line: 1, column: 1, tokenListIndex: 1 },
            { line: 2, column: 1, tokenListIndex: 2 },
            { line: 3, column: 1, tokenListIndex: 1 },
            { line: 4, column: 1, tokenListIndex: 20 }
          )
        ],
        displayName1: 'Test1',
        displayName2: 'Test2'
      }
    })
    // the "Matches:" Bubble and the Match
    expect(wrapper.findAllComponents(OptionComponent).length).toBe(2)
    expect(wrapper.text()).toContain('firstFile.java - secondFile.java:2')
  })

  it('display tooltip information', () => {
    const wrapper = mount(MatchList, {
      props: {
        matches: [
          buildMatch(
            'firstFile.java',
            'secondFile.java',
            { line: 1, column: 1, tokenListIndex: 9 },
            { line: 2, column: 1, tokenListIndex: 10 },
            { line: 3, column: 1, tokenListIndex: 1 },
            { line: 4, column: 1, tokenListIndex: 20 }
          )
        ],
        displayName1: 'Test1',
        displayName2: 'Test2'
      }
    })
    const matchTooltip = wrapper.findAllComponents(ToolTipComponent)[1]
    expect(matchTooltip.exists()).toBeTruthy()

    // check for line numbers
    expect(matchTooltip.text()).toContain('1-2')
    expect(matchTooltip.text()).toContain('3-4')

    // check for match lengths
    expect(matchTooltip.text()).toContain('2 tokens')
    expect(matchTooltip.text()).toContain('20 tokens')

    // check for token ranges
    expect(matchTooltip.text()).toContain('9-10')
    expect(matchTooltip.text()).toContain('1-20')
  })

  it('display multiple matches', () => {
    const fileNames: { first: string; second: string }[] = [
      { first: 'file1.java', second: 'file2.java' },
      { first: 'file3.java', second: 'file4.java' },
      { first: 'file5.java', second: 'file6.java' },
      { first: 'file7.java', second: 'file8.java' },
      { first: 'file9.java', second: 'file10.java' }
    ]

    const wrapper = mount(MatchList, {
      props: {
        matches: fileNames.map((files, index) =>
          buildMatch(
            files.first,
            files.second,
            { line: 1, column: 1, tokenListIndex: 10 - index },
            { line: 2, column: 1, tokenListIndex: 15 - index },
            { line: 3, column: 1, tokenListIndex: 23 - index },
            { line: 4, column: 1, tokenListIndex: 30 - index }
          )
        ),
        displayName1: 'Test1',
        displayName2: 'Test2'
      }
    })

    const allBubbles = wrapper.findAllComponents(OptionComponent)
    expect(allBubbles.length).toBe(fileNames.length + 1) // +1 for the "Matches:" bubble
    fileNames.forEach((files, index) => {
      expect(allBubbles[index + 1].text()).toContain(`${files.first} - ${files.second}:`)
    })

    const allToolTips = wrapper.findAllComponents(ToolTipComponent)
    expect(allToolTips.length).toBe(fileNames.length + 1) // +1 for the "Matches:" tooltip
    fileNames.forEach((files, index) => {
      const tooltip = allToolTips[index + 1]
      expect(tooltip.text()).toContain(files.first)
      expect(tooltip.text()).toContain(files.second)
      expect(tooltip.text()).toContain(`${10 - index}-${15 - index}`) // token ranges
      expect(tooltip.text()).toContain(`${23 - index}-${30 - index}`) // token ranges
    })
  })

  it('check background color match', () => {
    const colors = [3, 2, 1, 6, 0, 2]
    const wrapper = mount(MatchList, {
      props: {
        matches: colors.map((color) =>
          buildMatch(
            `file1.java`,
            `file2.java`,
            { line: 1, column: 1, tokenListIndex: 1 },
            { line: 2, column: 1, tokenListIndex: 2 },
            { line: 1, column: 1, tokenListIndex: 1 },
            { line: 2, column: 1, tokenListIndex: 2 },
            color
          )
        ),
        displayName1: 'Test1',
        displayName2: 'Test2'
      }
    })

    const allBubbles = wrapper.findAllComponents(OptionComponent)
    expect(allBubbles.length).toBe(colors.length + 1) // +1 for the "Matches:" bubble
    const foundColors: string[] = []
    colors.forEach((_, index) => {
      const bubble = allBubbles[index + 1]
      // (1|0|(0\.[0-9]+)) matches any number between 0 and 1, including 0 and 1
      const regex = /background: (rgba\([0-9]+, [0-9]+, [0-9]+, (?:1|0|(?:0\.[0-9]+)))\)/
      const style = bubble.attributes('style')
      expect(style).toBeDefined()
      const match = style!.match(regex)
      expect(match).toBeDefined()
      foundColors.push(match![1])
    })

    // verify that execpt for the last color each color only occures once
    const uniqueColors = new Set(foundColors.slice(0, -1))
    expect(uniqueColors.size).toBe(colors.length - 1)
    expect(uniqueColors.has(foundColors[foundColors.length - 1])).toBeTruthy()
  })

  it('display Base Code badget only when present', () => {
    const wrapperWithBaseCodeInFirst = mount(MatchList, {
      props: {
        matches: [],
        displayName1: 'Test1',
        displayName2: 'Test2',
        basecodeInFirst: [
          new BaseCodeMatch(
            'test1',
            { line: 1, column: 1, tokenListIndex: 1 },
            { line: 2, column: 1, tokenListIndex: 2 },
            1
          )
        ],
        basecodeInSecond: []
      }
    })
    expect(wrapperWithBaseCodeInFirst.text()).toContain('Base Code')

    const wrapperWithBaseCodeInSecond = mount(MatchList, {
      props: {
        matches: [],
        displayName1: 'Test1',
        displayName2: 'Test2',
        basecodeInFirst: [],
        basecodeInSecond: [
          new BaseCodeMatch(
            'test2',
            { line: 1, column: 1, tokenListIndex: 1 },
            { line: 2, column: 1, tokenListIndex: 2 },
            1
          )
        ]
      }
    })
    expect(wrapperWithBaseCodeInSecond.text()).toContain('Base Code')

    const wrapperWithBaseCodeInBoth = mount(MatchList, {
      props: {
        matches: [],
        displayName1: 'Test1',
        displayName2: 'Test2',
        basecodeInFirst: [
          new BaseCodeMatch(
            'test1',
            { line: 1, column: 1, tokenListIndex: 1 },
            { line: 2, column: 1, tokenListIndex: 2 },
            1
          )
        ],
        basecodeInSecond: [
          new BaseCodeMatch(
            'test2',
            { line: 1, column: 1, tokenListIndex: 1 },
            { line: 2, column: 1, tokenListIndex: 2 },
            1
          )
        ]
      }
    })
    expect(wrapperWithBaseCodeInBoth.text()).toContain('Base Code')

    const wrapperWithoutBaseCode = mount(MatchList, {
      props: {
        matches: [],
        displayName1: 'Test1',
        displayName2: 'Test2',
        basecodeInFirst: [],
        basecodeInSecond: []
      }
    })
    expect(wrapperWithoutBaseCode.text()).not.toContain('Base Code')
  })
})

function buildMatch(
  firstFileName: string,
  secondFileName: string,
  startInFirst: { line: number; column: number; tokenListIndex: number },
  endInFirst: { line: number; column: number; tokenListIndex: number },
  startInSecond: { line: number; column: number; tokenListIndex: number },
  endInSecond: { line: number; column: number; tokenListIndex: number },
  colorIndex?: number
): Match {
  return {
    firstFileName,
    secondFileName,
    startInFirst: { ...startInFirst },
    endInFirst: { ...endInFirst },
    startInSecond: { ...startInSecond },
    endInSecond: { ...endInSecond },
    lengthOfFirst: endInFirst.tokenListIndex - startInFirst.tokenListIndex + 1,
    lengthOfSecond: endInSecond.tokenListIndex - startInSecond.tokenListIndex + 1,
    colorIndex: colorIndex
  }
}
