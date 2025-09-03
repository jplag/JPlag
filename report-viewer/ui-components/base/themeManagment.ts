const USE_DARK_MODE_KEYWORD = 'jplag:use-dark-mode'

export function getUseDarkModeSetting() {
  const local = localStorage.getItem(USE_DARK_MODE_KEYWORD)
  if (local !== null) {
    return local === 'true'
  }

  return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
}

export function saveUseDarkModeSetting(value: boolean) {
  localStorage.setItem(USE_DARK_MODE_KEYWORD, value ? 'true' : 'false')
}
