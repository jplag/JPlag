import pluginVue from 'eslint-plugin-vue'
import js from '@eslint/js'
import tseslint from 'typescript-eslint';
import vueParser from 'vue-eslint-parser'
import eslintConfigPrettier from "eslint-config-prettier";
//import vueTypescript from '@vue/eslint-config-typescript'
//import vueSkipFormatting, { ignores } from '@vue/eslint-config-prettier/skip-formatting'
//import vue3Essential from 'eslint-plugin-vue/lib/configs/vue3-essential.js'

export default [
   js.configs.recommended,
   ...pluginVue.configs['flat/recommended'],
  ...tseslint.configs.recommended,
  eslintConfigPrettier,
  {
    ignores: ['**/*.config.ts', 'node_modules/**' , 'dist/**', '**/playwright-report/**'],
  },
  {
    files: ['**/*.js', '**/*.ts', '**/*.tsx', '**/*.vue'],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      parser: vueParser,
      parserOptions: {
        parser: tseslint.parser
      }
    },
    rules: {
      'no-console': ['error', { allow: ['warn', 'error', 'info'] }],
      'no-restricted-exports': ['error', { restrictDefaultExports: { direct: true } }],
      'vue/no-setup-props-reactivity-loss': 'error',
    },
  },
  {
    files: ['**/*.config.ts', '**/*.config.js', '**/*.d.ts'],
    rules: {
      'no-restricted-exports': 'off',
    },
  },
  {
    files: ['**/*.test.ts'],
    rules: {
        '@typescript-eslint/no-unused-expressions': 'off'
    }
  }
]
