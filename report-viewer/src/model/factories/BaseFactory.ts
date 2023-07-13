import store from '@/stores/store'

/**
 * This class provides some basic functionality for the factories.
 */
export class BaseFactory {
  /**
   * Returns the content of a file through the stored loading type.
   * @param path - Path to the file
   * @return Content of the file
   * @throws Error if the file could not be found
   */
  protected static getFile(path: string): string {
    if (store().state.localModeUsed) {
      return this.getLocalFile(path)
    } else if (store().state.zipModeUsed) {
      const index = Object.keys(store().state.files).find((name) => name.endsWith(path))
      if (index != undefined) {
        const file = store().state.files[index]
        if (file != undefined) {
          return file
        }
      }
      throw new Error(`Could not find ${path} in zip file.`)
    } else if (store().state.singleModeUsed) {
      return store().state.singleFillRawContent
    }

    throw new Error('No loading type specified')
  }

  /**
   * Returns the content of a file from the local files.
   * @param path - Path to the file
   * @return Content of the file
   * @throws Error if the file could not be found
   */
  protected static getLocalFile(path: string): string {
    const request = new XMLHttpRequest()
    request.open('GET', `/files/${path}`, false)
    request.send()

    if (request.status == 200) {
      return request.response
    } else {
      throw new Error(`Could not find ${path} in local files.`)
    }
  }
}
