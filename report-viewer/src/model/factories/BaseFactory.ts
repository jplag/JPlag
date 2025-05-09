import { store } from '@/stores/store'
import { ZipFileHandler } from '@/model/fileHandling/ZipFileHandler'

/**
 * This class provides some basic functionality for the factories.
 */
export class BaseFactory {
  public static readonly zipFileName = 'results.jplag'

  /**
   * Returns the content of a file through the stored loading type.
   * @param path - Path to the file
   * @return Content of the file
   * @throws Error if the file could not be found
   */
  protected static async getFile(path: string): Promise<string> {
    let storeFile = this.getFileFromStore(path)
    if (storeFile != undefined) {
      return storeFile
    }

    if (await this.useLocalZipMode()) {
      await new ZipFileHandler().handleFile(await this.getLocalFile(this.zipFileName))
    } else if (import.meta.env.MODE == 'demo' || import.meta.env.MODE == 'dev-demo') {
      await new ZipFileHandler().handleFile(await this.getLocalFile('example.jplag'))
    }

    storeFile = this.getFileFromStore(path)
    if (storeFile != undefined) {
      return storeFile
    }
    throw new Error(`Could not find ${path} report files.`)
  }

  private static getFileFromStore(path: string): string | undefined {
    const index = Object.keys(store().state.files).find((name) => name.endsWith(path))
    if (index == undefined) {
      return undefined
    }
    const file = store().state.files[index]
    if (file == undefined) {
      return undefined
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
      /* eslint-disable @typescript-eslint/no-unused-vars */
    } catch (e) {
      return false
    }
    /* eslint-enable @typescript-eslint/no-unused-vars */
  }
}
