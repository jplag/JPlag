export abstract class ConsoleAndFileMessage {
  abstract getConsoleMessage(): string
  abstract getLogFileMessage(): string
}

export class ConstructedMessage extends ConsoleAndFileMessage {
  constructor(private parts: Message[]) {
    super()
  }

  public getConsoleMessage(): string {
    return this.parts
      .map((part) => {
        if (part instanceof ConsoleAndFileMessage) {
          return part.getConsoleMessage()
        } else {
          return String(part)
        }
      })
      .join('')
  }

  public getLogFileMessage(): string {
    return this.parts
      .map((part) => {
        if (part instanceof ConsoleAndFileMessage) {
          return part.getLogFileMessage()
        } else {
          return String(part)
        }
      })
      .join('')
  }
}

export class SimpleConsoleAndFileMessage extends ConsoleAndFileMessage {
  constructor(
    private consoleMessage: string,
    private logFileMessage: string
  ) {
    super()
  }

  public getConsoleMessage(): string {
    return this.consoleMessage
  }

  public getLogFileMessage(): string {
    return this.logFileMessage
  }
}

export type Message = ConsoleAndFileMessage | unknown
