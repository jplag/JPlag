import { writeFileSync } from 'node:fs'
import { execSync } from 'node:child_process'

const hash = execSync('git rev-parse HEAD').toString().trim()
const filePath = 'src/version/hash.json'
const fileContent = JSON.stringify({ hash })
writeFileSync(filePath, fileContent)
