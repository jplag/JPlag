import colors from 'tailwindcss/colors'

/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: [
    "./index.html",
    "./src/**/*.{js,ts,vue}"
  ],
  theme: {
    extend: {
      colors: {
        font: {
          light: '#000000',
          dark: colors.amber[50]
        },
        backgorund: {
          light: colors.slate[300],
          dark: '#010409'
        },
        container: {
          light: colors.slate[200],
          dark: colors.slate[800],
          border: {
            light: colors.slate[400],
            dark: colors.slate[700]
          }
        },
        interactable: {
          light: colors.slate[50],
          dark: colors.slate[900],
          border: {
            light: colors.slate[400],
            dark: '#bbbbbb'
          }
        },
        accent: {
          DEFAULT: '#be1622',
          dark: '#7F0F18'
        },

      }
    },
  },
  plugins: [],
}

