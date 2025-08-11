import pluginVue from 'eslint-plugin-vue'
import js from '@eslint/js'
import tseslint from 'typescript-eslint'
import vueParser from 'vue-eslint-parser'
import globals from 'globals'
import eslintConfigPrettier from 'eslint-config-prettier'

export default [
  js.configs.recommended,
  ...pluginVue.configs['flat/recommended'],
  ...tseslint.configs.recommended,
  eslintConfigPrettier,
  {
    ignores: ['**/*.config.ts', '**/node_modules/**', '**/dist/**', '**/playwright-report/**']
  },
  {
    files: ['**/*.js', '**/*.jsx', '**/*.ts', '**/*.tsx', '**/*.vue'],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      parser: vueParser,
      parserOptions: {
        parser: tseslint.parser
      },
      globals: {
        ...globals.browser
      }
    },
    rules: {
      'no-console': ['error', { allow: ['warn', 'error', 'info'] }],
      'no-restricted-exports': ['error', { restrictDefaultExports: { direct: true } }],
      'vue/no-setup-props-reactivity-loss': 'error'
    }
  },
  {
    files: ['**/*.config.ts', '**/*.config.js', '**/*.d.ts'],
    rules: {
      'no-restricted-exports': 'off'
    }
  },
  {
    files: ['**/*.test.ts'],
    rules: {
      '@typescript-eslint/no-unused-expressions': 'off'
    }
  }
]
