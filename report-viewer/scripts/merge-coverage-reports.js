import iCoverage from 'istanbul-lib-coverage'
import iReport from 'istanbul-lib-report'
import { appendFileSync, existsSync, mkdirSync, readFileSync, rmSync } from 'fs'
import reports from 'istanbul-reports'

const coverageMap = iCoverage.createCoverageMap({})

const files = ['parser', 'model', 'ui-components', 'report-viewer'].map(
  (pkg) => `${pkg}/coverage/coverage-final.json`
)

files.forEach((file) => {
  const coverage = JSON.parse(readFileSync(file, 'utf-8'))
  coverageMap.merge(coverage)
})

// remove old coverage report if exists and create new one
if (existsSync('coverage')) {
  rmSync('coverage', { recursive: true, force: true })
}
mkdirSync('coverage')

const context = iReport.createContext({
  dir: 'coverage',
  coverageMap
})

reports.create('text-summary', { file: 'summary.txt' }).execute(context)
reports.create('text', { file: 'detail.txt', maxCols: 200, skipEmpty: true }).execute(context)
reports.create('html', { subdir: 'html' }).execute(context)

// export summary for github actions to allow it to fail
const githubOutput = process.env.GITHUB_OUTPUT
if (githubOutput) {
  appendFileSync(githubOutput, `COVERAGE=${coverageMap.getCoverageSummary().lines.pct}\n`)
}

console.log('Merged Coverage Reports')
