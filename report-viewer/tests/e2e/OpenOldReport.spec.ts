import { expect, test } from '@playwright/test'
import { uploadFile } from './TestUtils'

const oldVersionZips = [
  {
    zipName: 'progpedia-report-v5_1_0.zip',
    version: '5.1.0',
    urlPostfix: 'v5'
  },
  {
    zipName: 'progpedia-report-v4_2_0.zip',
    version: '4.2.0',
    urlPostfix: 'v5'
  }
]

for (const oldVersion of oldVersionZips) {
  test(`Test old version redirect for v${oldVersion.version}`, async ({ page }) => {
    await page.goto('/')
    await uploadFile(oldVersion.zipName, page, '/old/' + oldVersion.version)

    const bodyContent = await page.locator('body').textContent()
    expect(bodyContent).toContain(
      'You are trying to open a report created with an older version of JPlag'
    )
    expect(bodyContent).toContain(oldVersion.version)
    expect(bodyContent).toContain('You can still view the old report here:')
    expect(bodyContent).toContain('Open with old report viewer')

    const oldVersionLinkElement = await page.locator('a').first()
    const oldVersionURL = await oldVersionLinkElement.getAttribute('href')
    expect(oldVersionURL.endsWith('/v5/')).toBeTruthy()
  })
}
