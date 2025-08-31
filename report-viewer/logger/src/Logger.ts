/* eslint-disable no-console */
import { ConsoleAndFileMessage, Message } from './Message'

type LogLevel = 'info' | 'warn' | 'error' | 'debug' | 'log'

type ILogger = Record<LogLevel, (...data: Message[]) => void>
interface LabeledLogger {
  label: (label: string) => ILogger
}

export class StaticLogger {
  private static logs: Log[] = []

  private static print(level: LogLevel, label?: string, ...data: Message[]) {
    const newLog = this.buildLog(level, data, label)
    this.logs.push(newLog)
    console[level](this.buildMessage(newLog, true))
  }

  public static error(...data: Message[]) {
    this.print('error', undefined, ...data)
  }
  public static warn(...data: Message[]) {
    this.print('warn', undefined, ...data)
  }
  public static info(...data: Message[]) {
    this.print('info', undefined, ...data)
  }
  public static log(...data: Message[]) {
    this.print('log', undefined, ...data)
  }
  public static debug(...data: Message[]) {
    this.print('debug', undefined, ...data)
  }

  public static getLog() {
    return this.logs.map((log) => this.buildMessage(log, false)).join('\n')
  }

  public static label(label: string): ILogger {
    return {
      info: (...data: Message[]) => this.info('info', label, ...data),
      warn: (...data: Message[]) => this.warn('warn', label, ...data),
      error: (...data: Message[]) => this.error('error', label, ...data),
      debug: (...data: Message[]) => this.debug('debug', label, ...data),
      log: (...data: Message[]) => this.log('log', label, ...data)
    }
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

    let displayedCaller = log.label || this.getCaller(log.caller)
    // @ts-expect-error TS doesn't know about import.meta.env.DEV as it comes from vite
    if (import.meta.env.DEV) {
      displayedCaller = this.getCaller(log.caller)
      if (log.label) {
        displayedCaller += ` (${displayedCaller})`
      }
    }
    return `[${log.level.toUpperCase()}] ${log.time.toISOString()} ${displayedCaller}${isConsole ? '\n' : ': '}${message}`
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

  private static getCaller(rawCaller: string) {
    const functionName = rawCaller.split('@')[0]
    const parts = rawCaller.split('/')
    const wholeFileName = parts[parts.length - 1]

    const positionParts = wholeFileName.split(':')
    const fileName = positionParts[0].includes('?')
      ? positionParts[0].split('?')[0]
      : positionParts[0]
    const codePosition =
      positionParts[positionParts.length - 2] + ':' + positionParts[positionParts.length - 1]

    return `${functionName}@${fileName}:${codePosition}`
  }
}

interface Log {
  caller: string
  label?: string
  data: Message[]
  time: Date
  level: LogLevel
}

export const Logger: ILogger & LabeledLogger = StaticLogger
