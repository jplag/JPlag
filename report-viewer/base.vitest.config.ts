import { defineConfig } from 'vite'

export default defineConfig({
  test: {
    coverage: {
      provider: 'v8',
      reporter: ['json', ['text-summary', { file: 'summary.txt' }]],
      reportsDirectory: './coverage',
      exclude: ['**/tests/**']
    }
  }
})
