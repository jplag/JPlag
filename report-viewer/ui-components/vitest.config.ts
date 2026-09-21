import { fileURLToPath } from 'node:url'
import { configDefaults, defineConfig, mergeConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import baseVitestConfig from '../base.vitest.config'

export default mergeConfig(
  baseVitestConfig,
  defineConfig({
    plugins: [vue()],
    test: {
      environment: 'jsdom',
      exclude: [...configDefaults.exclude],
      root: fileURLToPath(new URL('./', import.meta.url)),
      coverage: {
        provider: 'v8',
        reporter: ['json'],
        reportsDirectory: './coverage'
      }
    }
  })
)
