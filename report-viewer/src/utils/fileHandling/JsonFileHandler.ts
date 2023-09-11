import { store } from '@/stores/store'
import { FileHandler } from '../../../FileHandler'

/**
 * Class for handling single json files.
 */
export class JsonFileHandler extends FileHandler {
  public async handleFile(
    file: Blob
  ): Promise<{ fileType: 'overview' } | { fileType: 'comparison'; id1: string; id2: string }> {
    const content = await file.text()
    const json = JSON.parse(content)

    store().setSingleFileRawContent(content)
    if (json['submission_folder_path']) {
      return { fileType: 'overview' }
    } else if (json['id1'] && json['id2']) {
      return { fileType: 'comparison', id1: json['id1'], id2: json['id2'] }
    } else {
      throw new Error(`Invalid JSON: ${json}`)
    }
  }
}
