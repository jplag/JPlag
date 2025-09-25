interface Node<T> {
  data: T
  next: Node<T> | null
}

export class Queue<T> {
  private head: Node<T> | null = null
  private tail: Node<T> | null = null

  public add(data: T): void {
    const newNode: Node<T> = { data, next: null }
    if (this.tail) {
      this.tail.next = newNode
    } else {
      this.head = newNode
    }
    this.tail = newNode
  }

  public remove(): T | null {
    if (!this.head) return null
    const dequeuedData = this.head.data
    this.head = this.head.next
    if (!this.head) {
      this.tail = null
    }
    return dequeuedData
  }

  public isEmpty(): boolean {
    return this.head === null
  }
}
