import { expect, Page, test } from '@playwright/test'
import { uploadFile } from './TestUtils'

const oldVersionReports = [
  {
    fileName: 'progpedia-report-v5_1_0.zip',
    version: '5.1.0',
    urlPostfix: 'v5'
  },
  {
    fileName: 'progpedia-report-v4_2_0.zip',
    version: '4.2.0',
    urlPostfix: 'v5'
  }
]

for (const oldVersion of oldVersionReports) {
  test(`Test old version redirect for v${oldVersion.version}`, async ({ page }) => {
    await uploadFile(oldVersion.fileName, page, getWaitForOldPageFunction(oldVersion.version))

    const bodyContent = await page.locator('body').textContent()
    expect(bodyContent).toContain(
      'You are trying to open a report created with an older version of JPlag'
    )
    expect(bodyContent).toContain(oldVersion.version)
    expect(bodyContent).toContain('You can still view the old report here:')
    expect(bodyContent).toContain('Open with old report viewer')

    const oldVersionLinkElement = await page.locator('a').first()
    const oldVersionURL = await oldVersionLinkElement.getAttribute('href')
    expect(oldVersionURL!.endsWith('/v5/')).toBeTruthy()
  })
}

test('Test unsupported old version', async ({ page }) => {
  await uploadFile('progpedia-report-v4_0_0.zip', page, getWaitForOldPageFunction('4.0.0'))

  const bodyContent = await page.locator('body').textContent()
  expect(bodyContent).toContain(
    'You are trying to open a report created with an older version of JPlag'
  )
  expect(bodyContent).not.toContain('You can still view the old report here:')
  expect(bodyContent).not.toContain('Open with old report viewer')
  expect(bodyContent).toContain(
    'Opening reports generated with version 4.0.0 is not supported by this report viewer.'
  )
})

function getWaitForOldPageFunction(version: string) {
  return async (page: Page) => {
    await page.waitForURL(`/old/${version}`)
  }
}
