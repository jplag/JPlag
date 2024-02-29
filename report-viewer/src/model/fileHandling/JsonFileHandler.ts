import { store } from '@/stores/store'
import { FileHandler } from './FileHandler'

/**
 * Class for handling single json files.
 */
export class JsonFileHandler extends FileHandler {
  public async handleFile(file: Blob) {
    const content = await file.text()
    const json = JSON.parse(content)

    store().setSingleFileRawContent(content)
    if (!json['submission_folder_path']) {
      throw new Error(`Invalid JSON: File is not an overview file.`)
    }
  }
}
