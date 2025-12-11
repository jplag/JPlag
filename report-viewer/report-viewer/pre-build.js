import { writeFileSync } from 'fs'
import { execSync } from 'child_process'

const hash = execSync('git rev-parse HEAD').toString().trim()
const filePath = 'src/version/hash.json'
const fileContent = JSON.stringify({ hash })
writeFileSync(filePath, fileContent)
