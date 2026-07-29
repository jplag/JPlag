import { defineConfig, mergeConfig } from 'vite'
import { configDefaults } from 'vitest/config'
import baseVitestConfig from '../base.vitest.config'

export default mergeConfig(
  baseVitestConfig,
  defineConfig({
    test: {
      exclude: [...configDefaults.exclude, 'tests/e2e/*', 'src/views', 'src/viewWrapper']
    }
  })
)
