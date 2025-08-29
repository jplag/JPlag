import { Version } from '@jplag/model'
import versionJson from './version.json'

export const reportViewerVersion = Version.fromJsonField(versionJson['report_viewer_version'])
export const minimalReportVersion = Version.fromJsonField(versionJson['minimal_report_version'])
