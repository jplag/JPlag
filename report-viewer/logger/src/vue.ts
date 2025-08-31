import type { App } from 'Vue'
import { Logger } from '.'

export function loggerInstaller(app: App) {
  Logger.debug('Installing logger into Vue App')
  app.config.errorHandler = (err: unknown, _: unknown, info: string) => {
    Logger.error(err, info)
    alert('An unhandled error occurred. Please check the console for more details.')
  }
}
