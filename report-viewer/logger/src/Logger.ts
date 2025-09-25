/* eslint-disable no-console */
import { NullableMappedPosition, Position, SourceMapConsumer } from 'source-map'
import { ConsoleAndFileMessage, Message } from './Message'
import { Queue } from './Queue'

type LogLevel = 'info' | 'warn' | 'error' | 'debug' | 'log'

type ILogger = Record<LogLevel, (...data: Message[]) => void>
interface LabeledLogger {
  label: (label: string) => ILogger
}

type MapInPosition = Position & { bias?: number; source: string }

interface OneWaySourceMapTranslation {
  originalPositionFor(generatedPosition: MapInPosition): NullableMappedPosition
}

const identifySourceMapTranslation: OneWaySourceMapTranslation = {
  originalPositionFor(generatedPosition: MapInPosition): NullableMappedPosition {
    return {
      source: generatedPosition.source,
      line: generatedPosition.line,
      column: generatedPosition.column,
      name: null
    }
  }
}

export class StaticLogger {
  private static logs: Log[] = []
  private static backLog: Queue<Log> = new Queue<Log>()
  private static mappers: Record<string, OneWaySourceMapTranslation> = {}
  private static isProcessingBackLog = false

  public static async fetchMapper(fileName: string) {
    const mapFileName = `assets/${fileName}.map`
    const content = await fetch(new URL(mapFileName, window.location.href))
      .then((res) => res.text())
      .catch(() => null)
    console.log(content)
    // vite returns the html for all non matching requests, so we filter that here
    if (content !== null && !content.startsWith('<')) {
      // @ts-expect-error This is needed in web environments
      SourceMapConsumer.initialize({
        'lib/mappings.wasm': 'https://unpkg.com/source-map@0.7.3/lib/mappings.wasm'
      })
      const consumer = await new SourceMapConsumer(content)
      this.mappers[fileName] = consumer
    } else {
      this.mappers[fileName] = identifySourceMapTranslation
    }
  }

  private static tryPrint(level: LogLevel, ...data: Message[]) {
    const log = this.buildLog(level, data)
    // If we still have unprinted logs, we append to the backlog to preserve the logging order
    if (this.backLog.isEmpty() && this.mappers[log.caller.source]) {
      this.print(log)
    } else {
      this.backLog.add(log)
      this.processBackLog()
    }
  }

  private static async processBackLog() {
    if (this.isProcessingBackLog) return
    this.isProcessingBackLog = true

    while (!this.backLog.isEmpty()) {
      const log = this.backLog.remove()!
      if (!this.mappers[log.caller.source]) {
        await this.fetchMapper(log.caller.source)
      }
      this.print(log)
    }

    this.isProcessingBackLog = false
  }

  private static print(log: Log) {
    this.logs.push(log)
    console[log.level](this.buildMessage(log, true))
  }

  public static error(...data: Message[]) {
    this.tryPrint('error', ...data)
  }
  public static warn(...data: Message[]) {
    this.tryPrint('warn', ...data)
  }
  public static info(...data: Message[]) {
    this.tryPrint('info', ...data)
  }
  public static log(...data: Message[]) {
    this.tryPrint('log', ...data)
  }
  public static debug(...data: Message[]) {
    this.tryPrint('debug', ...data)
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

    return `[${log.level.toUpperCase()}] ${log.time.toISOString()} ${this.getMappedPosition(log.caller)}${isConsole ? '\n' : ': '}${message}`
  }

  private static buildLog(level: LogLevel, data: Message[]): Log {
    return {
      caller: this.getCaller((new Error().stack?.split('\n')[3] || 'unknown').trim()),
      data,
      time: new Date(),
      level
    }
  }

  private static getCaller(rawCaller: string): MapInPosition {
    const parts = rawCaller.split('/')
    const wholeFileName = parts[parts.length - 1]

    const positionParts = wholeFileName.split(':')
    const fileName = positionParts[0].includes('?')
      ? positionParts[0].split('?')[0]
      : positionParts[0]
    const line = Number(positionParts[positionParts.length - 2])
    const column = Number(positionParts[positionParts.length - 1])

    return { line, column, source: fileName }
  }

  private static getMappedPosition(caller: MapInPosition) {
    const mapper = this.mappers[caller.source]
    const mapped = mapper?.originalPositionFor(caller)
    const position = {
      line: mapped?.line || caller.line,
      column: mapped?.column || caller.column,
      source: mapped?.source || caller.source
    }
    return `${position.source}:${position.line}:${position.column}`
  }
}

interface Log {
  caller: MapInPosition
  data: Message[]
  time: Date
  level: LogLevel
}

export const Logger: ILogger & LabeledLogger = StaticLogger
