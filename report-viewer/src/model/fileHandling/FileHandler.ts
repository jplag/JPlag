/**
 * Abstract base class for handling files.
 */
export abstract class FileHandler {
  public async handleFile(file: Blob): Promise<void> {
    await this.extractContent(file)
  }

  /**
   * Loads the content of the given file and stores it in the store.
   * @param file File to handle
   */
  protected abstract extractContent(file: Blob): Promise<void>
}
