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
          light: 'hsl(0, 0%, 97%)',
          dark: 'hsl(0, 0%, 3%)'
        },
        container: {
          light: 'hsl(200, 5%, 98%)',
          dark: 'hsl(0, 0%, 8%)',
          border: {
            light: 'hsl(0, 0%, 85%)',
            dark: 'hsl(0, 0%, 13%)'
          }
        },
        interactable: {
          light: 'hsl(0, 0%, 100%)',
          dark: 'hsl(0, 0%, 15%)',
          border: {
            light: 'hsl(0, 0%, 85%)',
            dark: 'hsl(0, 0%, 18%)'
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

      },
      borderWidth: {
        1: '1px',
      }
    },
  },
  plugins: [],
}

