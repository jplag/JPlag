import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "JPlag",
  head: [['link', { rel: 'icon', href: '/favicon.ico' }]],
  description: "JPlag – Detecting Source Code Plagiarism",
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Download', link: '/download/' },
      { text: 'Publications', link: '/publications/'},
      { text: 'Documentation', link: '/wiki/'}
    ],

    sidebar: [
      {
        text: 'Quick Links',
        items: [
          {
            text: 'Download',
            link: '/download/'
          },
          {
            text: 'Publications',
            link: '/publications/'
          },
          {
            text: 'JPlag Demo',
            link: 'https://demo.jplag.de/'
          },
          {
            text: 'GitHub Repository',
            link: 'https://github.com/JPlag/JPlag'
          },
          {
            text: 'Maven Central',
            link: 'https://maven-badges.herokuapp.com/maven-central/de.jplag/jplag'
          },
          {
            text: 'Helmholtz RSD',
            link: 'http://helmholtz.software/software/jplag'
          }
        ]
      },
      {
        text: 'Documentation',
        items: [
          {
            text: 'Overview',
            link: '/wiki/'
          },
          {
            text: 'How to Use JPlag',
            link: '/wiki/how-to-use-jplag'
          }, 
          {
            text: 'Supported Languages',
            link: '/wiki/supported-languages'
          },
          {
            text: 'Contributing to JPlag',
            link: '/wiki/contributing-to-jplag'
          },
          {
            text: 'Adding New Languages',
            link: '/wiki/adding-new-languages'
          },
          {
            text: 'End to End Testing',
            link: '/wiki/end-to-end-testing'
          },
          {
            text: 'Report File Generation',
            link: '/wiki/report-file-generation'
          },
          {
            text: 'Report Viewer',
            link: '/wiki/report-viewer'
          },
          {
            text: 'Clustering of Submissions',
            link: '/wiki/clustering-of-submissions'
          }
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/JPlag' }
    ],

    notFound: {
      quote: "No plagiarism detected here. However, we could also not find the page you were looking for. Let's go back and try again.",
    },

    footer: {
      message: 'JPlag – Detecting Source Code Plagiarism. <a href="https://www.kit.edu/impressum.php">Imprint</a>, <a href="https://www.kit.edu/legals.php">Legals</a>, <a href="https://www.kit.edu/privacypolicy.php">Privacy Policy</a>.',
    }
  }
})
