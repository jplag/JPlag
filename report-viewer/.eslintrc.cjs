/* eslint-env node */
require('@rushstack/eslint-patch/modern-module-resolution')

module.exports = {
  root: true,
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended',
    '@vue/eslint-config-typescript',
    '@vue/eslint-config-prettier/skip-formatting'
  ],
  parserOptions: {
    ecmaVersion: 'latest'
  },
  plugins: ['@typescript-eslint', 'vue'],
  rules: {
    "no-console": process.env.NODE_ENV === "production" ? "warn" : "off",
    "no-restricted-exports": ['error', { 'restrictDefaultExports': { 'direct': true } }],
    "vue/no-setup-props-reactivity-loss": "error"
  },
  overrides: [
    {
      files: ['*.config.ts', '*.config.js', '*.d.ts'],
      rules: {
       "no-restricted-exports": "off"
      }
    },
    {
      files: ['*.html'],
      rules: {
        'vue/comment-directive': 'off'
      }
    }
  ]
}
