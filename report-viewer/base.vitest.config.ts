import { defineConfig } from 'vite'

export default defineConfig({
  test: {
    coverage: {
      provider: 'v8',
      reporter: ['text-summary', ['lcovonly', { file: 'coverage.lcov' }]],
      reportsDirectory: './coverage',
      exclude: ['**/tests/**']
    }
  }
})
