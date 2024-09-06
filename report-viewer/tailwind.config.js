import colors from 'tailwindcss/colors'

/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{js,ts,vue}'],
  theme: {
    extend: {
      colors: {
        font: {
          light: '#000000',
          dark: '#ffffff'
        },
        background: {
          light: 'hsl(0, 0%, 97%)',
          dark: 'hsl(230, 10%, 8%)'
        },
        container: {
          light: 'hsl(0, 0%, 98%)',
          dark: 'hsl(250, 10%, 15%)',
          border: {
            light: 'hsl(0, 0%, 80%)',
            dark: 'hsl(0, 0%, 25%)'
          },
          secondary: {
            light: 'hsl(0, 0%, 95%)',
            dark: 'hsl(250, 10%, 20%)'
          }
        },
        interactable: {
          light: 'hsl(0, 0%, 100%)',
          dark: 'hsl(250, 10%, 20%)',
          border: {
            light: 'hsl(0, 0%, 75%)',
            dark: 'hsl(0, 0%, 25%)'
          }
        },
        scrollbar: {
          background: {
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
        link: {
          DEFAULT: '#0070f3',
          dark: '#00c'
        },
        error: '#dc322f',
        tooltip: 'rgba(0,0,0,0.8)'
      },
      borderWidth: {
        1: '1px'
      }
    }
  },
  plugins: []
}
