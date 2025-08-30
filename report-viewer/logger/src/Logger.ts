import { ConsoleAndFileMessage, Message } from './Message'

type LogLevel = 'info' | 'warn' | 'error' | 'debug' | 'log'

export class Logger {
  private static logs: Log[] = []

  public static print(level: LogLevel, label?: string, ...data: Message[]) {
    const newLog = this.buildLog(level, data, label)
    this.logs.push(newLog)
    // eslint-disable-next-line no-console
    console[level](this.buildMessage(newLog, true))
  }

  private static buildMessage(log: Log, isConsole = true): string {
    const message = log.data
      .map((item) => {
        if (item instanceof ConsoleAndFileMessage) {
          return isConsole ? item.getConsoleMessage() : item.getLogFileMessage()
        }
        return String(item)
      })
      .join(', ')

    // @ts-expect-error TS doesn't know about import.meta.env.DEV as it comes from vite
    const diplayedCaller = import.meta.env.DEV
      ? `${log.caller} (${log.label})`
      : log.label || log.caller
    return `[${log.time.toISOString()}] [${diplayedCaller}]${isConsole ? '' : ` [${log.level}]`}: ${message}`
  }

  public static info(...data: Message[]) {
    this.print('info', undefined, ...data)
  }
  public static warn(...data: Message[]) {
    this.print('warn', undefined, ...data)
  }
  public static error(...data: Message[]) {
    this.print('error', undefined, ...data)
  }
  public static debug(...data: Message[]) {
    this.print('debug', undefined, ...data)
  }
  public static log(...data: Message[]) {
    this.print('log', undefined, ...data)
  }

  public static getLog() {
    return this.logs.map((log) => this.buildMessage(log, false)).join('\n')
  }

  public static label(label: string) {
    return {
      info: (...data: Message[]) => this.info('info', label, ...data),
      warn: (...data: Message[]) => this.warn('warn', label, ...data),
      error: (...data: Message[]) => this.error('error', label, ...data),
      debug: (...data: Message[]) => this.debug('debug', label, ...data),
      log: (...data: Message[]) => this.log('log', label, ...data)
    }
  }

  private static buildLog(level: LogLevel, data: Message[], label?: string): Log {
    return {
      caller: (new Error().stack?.split('\n')[3] || 'unknown').trim(),
      data,
      time: new Date(),
      level,
      label
    }
  }
}

interface Log {
  caller: string
  label?: string
  data: Message[]
  time: Date
  level: LogLevel
}
