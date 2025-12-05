import { Version } from '@jplag/model'
import versionJson from './version.json'
import hashJson from './hash.json'

export const reportViewerVersion = Version.fromJsonField(versionJson['report_viewer_version'])
export const minimalReportVersion = Version.fromJsonField(versionJson['minimal_report_version'])
export const commitHash = (hashJson as { hash?: string }).hash
