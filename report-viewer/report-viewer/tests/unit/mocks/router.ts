import { vi } from 'vitest'

vi.mock('../../../src/router', () => ({
  router: {
    push: vi.fn()
  }
}))
