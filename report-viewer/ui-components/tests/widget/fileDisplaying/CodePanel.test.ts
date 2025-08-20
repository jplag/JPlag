import { beforeAll, describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import CodePanel from '../../../widget/fileDisplaying/CodePanel.vue'
import { MatchInSingleFile, ParserLanguage } from '@jplag/model'
import CodeLine from '../../../widget/fileDisplaying/CodeLine.vue'

describe('CodePanel', () => {
  beforeAll(() => {
    // simulate relevant CSS styles for visibility from tailwind
    const style = document.createElement('style')
    style.innerHTML = `
      .hidden {
        display: none;
      }
    `
    document.head.appendChild(style)
  })

  it('Test collapsing', async () => {
    const wrapper = buildWrapper()

    const header = wrapper.findAll('span').find((span) => span.text().includes('TestFile.java'))
    expect(header).toBeDefined()
    if (!header) return

    const codepart = wrapper.findComponent(CodeLine)
    expect(codepart.isVisible()).toBe(true)
    await header.trigger('click')
    expect(codepart.isVisible()).toBe(false)
    await header.trigger('click')
    expect(codepart.isVisible()).toBe(true)
  })

  it('Test highlight for Java', () => {
    const wrapper = mount(CodePanel, {
      props: {
        file: {
          fileName: 'TestFile.java',
          data: 'public class TestFile {\n  public static void main(String[] args) {\n    System.out.println("Hello, World!");\n  }\n}',
          submissionId: 'test1',
          displayFileName: 'TestFile.java',
          matchedTokenCount: 20,
          tokenCount: 100
        },
        matches: [
          buildMatch(
            { line: 2, column: 2, tokenListIndex: 3 },
            { line: 4, column: 1, tokenListIndex: 6 },
            'TestFile.java',
            1
          )
        ],
        baseCodeMatches: [],
        highlightLanguage: ParserLanguage.JAVA
      }
    })

    const spans = wrapper
      .findAll('span')
      .filter((span) => span.classes().some((c) => c.includes('hljs')))
    expect(spans.length).toBeGreaterThan(0)

    expect(spans.some((span) => span.text().includes('public'))).toBe(true)
    expect(spans.some((span) => span.text().includes('class'))).toBe(true)
    expect(spans.some((span) => span.text().includes('static'))).toBe(true)
    expect(spans.some((span) => span.text().includes('void'))).toBe(true)
  })
  it('Test highlight for Python', () => {
    const wrapper = mount(CodePanel, {
      props: {
        file: {
          fileName: 'TestFile.py',
          data: 'def main():\n  print("Hello, World!")\n\nif __name__ == "__main__":\n  main()',
          submissionId: 'test2',
          displayFileName: 'TestFile.py',
          matchedTokenCount: 20,
          tokenCount: 100
        },
        matches: [
          buildMatch(
            { line: 2, column: 2, tokenListIndex: 3 },
            { line: 2, column: 24, tokenListIndex: 6 },
            'TestFile.py',
            1
          )
        ],
        baseCodeMatches: [],
        highlightLanguage: ParserLanguage.PYTHON
      }
    })

    const spans = wrapper
      .findAll('span')
      .filter((span) => span.classes().some((c) => c.includes('hljs')))
    expect(spans.length).toBeGreaterThan(0)

    expect(spans.some((span) => span.text().includes('def'))).toBe(true)
    expect(spans.some((span) => span.text().includes('print'))).toBe(true)
    expect(spans.some((span) => span.text().includes('if'))).toBe(true)
  })
})

function buildWrapper() {
  return mount(CodePanel, {
    props: {
      file: {
        fileName: 'TestFile.java',
        data: buildText(),
        submissionId: 'test1',
        displayFileName: 'TestFile.java',
        matchedTokenCount: 20,
        tokenCount: 100
      },
      matches: [],
      baseCodeMatches: [],
      highlightLanguage: ParserLanguage.TEXT
    },
    attachTo: document.body
  })
}

function buildText() {
  const line1 = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.'
  const line2 = 'Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.'
  const line3 =
    'Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.'
  const line4 =
    'Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.'
  const line5 =
    'Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.'
  return [line1, line2, line3, line4, line5].join('\n')
}

function buildMatch(
  start: { line: number; column: number; tokenListIndex: number },
  end: { line: number; column: number; tokenListIndex: number },
  fileName: string,
  color: number
): MatchInSingleFile {
  return new MatchInSingleFile(
    {
      firstFileName: fileName,
      secondFileName: fileName,
      startInFirst: start,
      endInFirst: end,
      startInSecond: start,
      endInSecond: end,
      colorIndex: color,
      lengthOfFirst: end.tokenListIndex - start.tokenListIndex + 1,
      lengthOfSecond: end.tokenListIndex - start.tokenListIndex + 1
    },
    1
  )
}
