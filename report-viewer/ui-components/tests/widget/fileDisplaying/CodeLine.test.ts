import { describe, expect, it } from 'vitest'
import { DOMWrapper, mount, VueWrapper } from '@vue/test-utils'
import CodeLine from '../../../widget/fileDisplaying/CodeLine.vue'
import { CodePosition, Match, MatchInSingleFile } from '@jplag/model'

const startMatch = buildMatch(
  { line: 3, column: 2, tokenListIndex: 1 },
  { line: 4, column: 5, tokenListIndex: 2 },
  0
)
const inlineMatch = buildMatch(
  { line: 4, column: 6, tokenListIndex: 3 },
  { line: 4, column: 25, tokenListIndex: 6 },
  2
)
const endMatch = buildMatch(
  { line: 4, column: 40, tokenListIndex: 8 },
  { line: 5, column: 42, tokenListIndex: 11 },
  3
)
const wrappingMatch = buildMatch(
  { line: 2, column: 100, tokenListIndex: 0 },
  { line: 5, column: 777, tokenListIndex: 12 },
  1
)

describe('CodeLine', () => {
  it('Test inline Match', () => {
    testCorrectPreCalculation(
      buildWrapper([inlineMatch]),
      ['Lorem ', 'ipsum dolor sit amet', ', consectetur adipiscing elit.'],
      [false, true, false]
    )
  })

  it('Test wrapping Match', () => {
    testCorrectPreCalculation(
      buildWrapper([wrappingMatch]),
      ['Lorem ipsum dolor sit amet, consectetur adipiscing elit.'],
      [true]
    )
  })

  it('Test start Match', () => {
    testCorrectPreCalculation(
      buildWrapper([startMatch]),
      ['Lorem ', 'ipsum dolor sit amet, consectetur adipiscing elit.'],
      [true, false]
    )
  })

  it('Test end Match', () => {
    testCorrectPreCalculation(
      buildWrapper([endMatch]),
      ['Lorem ipsum dolor sit amet, consectetur ', 'adipiscing elit.'],
      [false, true]
    )
  })

  it('Test multiple Matches', () => {
    const wrapper = buildWrapper([startMatch, inlineMatch, endMatch])
    testCorrectPreCalculation(
      wrapper,
      ['Lorem ', 'ipsum dolor sit amet', ', consectetur ', 'adipiscing elit.'],
      [true, true, false, true]
    )

    const pres = wrapper.findAll('pre')
    const colors = pres.map((pre) => getPreBackground(pre))
    const coloredPres = colors.filter((color) => isColored(color))
    expect(coloredPres).toHaveLength(3)
    const uniqueColors = new Set(coloredPres)
    expect(uniqueColors.size).toBe(3)
  })

  it('Test no Matches', () => {
    testCorrectPreCalculation(
      buildWrapper([]),
      ['Lorem ipsum dolor sit amet, consectetur adipiscing elit.'],
      [false]
    )
  })

  it('Test on click', async () => {
    const wrapper = mount(CodeLine, {
      props: {
        lineNumber: 4,
        line: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.',
        matches: [startMatch, inlineMatch, endMatch]
      }
    })

    const pres = wrapper.findAll('pre')
    expect(pres).toHaveLength(4)

    await pres[0].trigger('click')
    expect(wrapper.emitted('matchSelected')).toBeDefined()
    expect(wrapper.emitted('matchSelected')!.length).toBe(1)
    const emit1 = wrapper.emitted('matchSelected')![0][0] as Match | undefined
    expect(emit1).toBeDefined()
    expect(emit1!.startInFirst.line).toBe(3)
    expect(emit1!.endInFirst.line).toBe(4)

    await pres[1].trigger('click')
    expect(wrapper.emitted('matchSelected')!.length).toBe(2)
    const emit2 = wrapper.emitted('matchSelected')![1][0] as Match | undefined
    expect(emit2).toBeDefined()
    expect(emit2!.startInFirst.line).toBe(4)
    expect(emit2!.startInFirst.column).toBe(6)
    expect(emit2!.endInFirst.line).toBe(4)

    await pres[2].trigger('click')
    // no change to previous
    expect(wrapper.emitted('matchSelected')!.length).toBe(2)

    await pres[3].trigger('click')
    expect(wrapper.emitted('matchSelected')!.length).toBe(3)
    const emit4 = wrapper.emitted('matchSelected')![2][0] as Match | undefined
    expect(emit4).toBeDefined()
    expect(emit4!.startInFirst.line).toBe(4)
    expect(emit4!.startInFirst.column).toBe(40)
    expect(emit4!.endInFirst.line).toBe(5)
  })
})

function testCorrectPreCalculation(
  wrapper: VueWrapper,
  preTexts: string[],
  isMatchColored: boolean[]
) {
  expect(wrapper.text()).toContain('Lorem ipsum dolor sit amet, consectetur adipiscing elit.')

  const pres = wrapper.findAll('pre')
  expect(pres).toHaveLength(preTexts.length)

  preTexts.forEach((text, index) => {
    expect(getText(pres[index])).toEqual(text)
    expect(isColored(getPreBackground(pres[index]))).toBe(isMatchColored[index])
  })

  return pres
}

function buildWrapper(matches: MatchInSingleFile[]) {
  return mount(CodeLine, {
    props: {
      lineNumber: 4,
      line: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.',
      matches
    }
  })
}

function buildMatch(start: CodePosition, end: CodePosition, color: number): MatchInSingleFile {
  return new MatchInSingleFile(
    {
      startInFirst: start,
      endInFirst: end,
      startInSecond: start,
      endInSecond: end,
      firstFileName: 'file1',
      secondFileName: 'file2',
      colorIndex: color,
      lengthOfFirst: end.line - start.line + 1,
      lengthOfSecond: end.line - start.line + 1
    },
    1
  )
}

function getText(pre: DOMWrapper<HTMLPreElement>): string {
  const html = pre.html()
  const start = html.indexOf('>') + 1
  const end = html.indexOf('</pre>')
  return html.substring(start, end).replace(/<[^>]*>/g, '')
}

function getPreBackground(pre: DOMWrapper<HTMLPreElement>): string {
  const parent = pre.element.parentElement
  if (!parent) {
    throw new Error('Parent element not found')
  }
  const style = parent.getAttribute('style')
  if (!style) {
    throw new Error('Style attribute not found')
  }
  const match = style.match(/background:\s*([^;]+)/)
  if (!match) {
    throw new Error('Background color not found in style attribute')
  }
  return match[1]
}

function isColored(color: string): boolean {
  return !(color === 'rgba(0, 0, 0, 0)' || color === 'transparent')
}
