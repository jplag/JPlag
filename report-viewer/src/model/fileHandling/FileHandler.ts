/**
 * Abstract base class for handling files.
 */
export abstract class FileHandler {
  /**
   * Loads the content of the given file and stores it in the store.
   * @param file File to handle
   */
  public abstract handleFile(file: Blob): Promise<void>
}
