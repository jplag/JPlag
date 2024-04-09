import { store } from '@/stores/store'
import { ZipFileHandler } from '@/model/fileHandling/ZipFileHandler'

/**
 * This class provides some basic functionality for the factories.
 */
export class BaseFactory {
  public static zipFileName = 'results.zip'

  /**
   * Returns the content of a file through the stored loading type.
   * @param path - Path to the file
   * @return Content of the file
   * @throws Error if the file could not be found
   */
  protected static async getFile(path: string): Promise<string> {
    if (import.meta.env.MODE == 'demo') {
      await new ZipFileHandler().handleFile(await this.getLocalFile('example.zip'))
      return this.getFileFromStore(path)
    }
    if (store().state.localModeUsed) {
      return await (await this.getLocalFile(`/files/${path}`)).text()
    } else if (store().state.zipModeUsed) {
      return this.getFileFromStore(path)
    } else if (store().state.singleModeUsed) {
      return store().state.singleFillRawContent
    } else if (await this.useLocalZipMode()) {
      await new ZipFileHandler().handleFile(await this.getLocalFile(this.zipFileName))
      store().setLoadingType('zip')
      return this.getFileFromStore(path)
    }
    throw new Error('No loading type specified')
  }

  private static getFileFromStore(path: string): string {
    const index = Object.keys(store().state.files).find((name) => name.endsWith(path))
    if (index == undefined) {
      throw new Error(`Could not find ${path} in zip file.`)
    }
    const file = store().state.files[index]
    if (file == undefined) {
      throw new Error(`Could not load ${path}.`)
    }
    return file
  }

  /**
   * Returns the content of a file from the local files.
   * @param path - Path to the file
   * @return Content of the file
   * @throws Error if the file could not be found
   */
  public static async getLocalFile(path: string): Promise<Blob> {
    const request = await fetch(`${window.location.origin}${import.meta.env.BASE_URL}${path}`)
    if (request.status == 200) {
      const blob = await request.blob()
      // Check that file is not the index.html
      if (blob.type.includes('text/html')) {
        throw new Error(`Could not find ${path} in local files.`)
      }
      return blob
    } else {
      throw new Error(`Could not find ${path} in local files.`)
    }
  }

  public static async useLocalZipMode() {
    try {
      await this.getLocalFile(this.zipFileName)
      return true
    } catch (e) {
      return false
    }
  }
}
