
public class GenericPocket<T> {
	private T value;

	public GenericPocket() {
	}

	public GenericPocket(T value) {
		this.value = value;
	}

	public void set(T value) {
		this.value = value;
	}

	public T get() {
		return value;
	}

	public boolean isEmpty() {
		return value != null;
	}

	public void empty() {
		value = null;
	}

	public void main(String[] args) {
		GenericPocket<String> pocket = new GenericPocket<String>();
	}
}