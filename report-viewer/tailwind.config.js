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
          dark: '#ffffff'
        },
        backgorund: {
          light: colors.slate[300],
          dark: '#010409'
        },
        container: {
          light: colors.slate[200],
          dark: '#161B22',
          border: {
            light: colors.slate[400],
            dark: '#363B42'
          }
        },
        interactable: {
          light: colors.slate[50],
          dark: '#30363D',
          border: {
            light: colors.slate[400],
            dark: '#505A66'
          }
        },
        scrollbar: {
          backgorund: { 
            light: colors.slate[100],
            dark: '#30363D'
          },
          thumb: {
            light: colors.slate[400],
            dark: '#505A66'
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

