export async function getFolder() {
  const r = await fetch('http://localhost:8080/folder')
  if (r.ok) {
    return r.text()
  }
}