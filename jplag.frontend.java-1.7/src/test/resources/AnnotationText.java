package foo;

@SuppressWarnings("unused")
public class AnnotationText {

	@SuppressWarnings("unused")
	public AnnotationText() {
	}

	@SuppressWarnings("unused")
	public int foo = 5;

	@SuppressWarnings("unused")
	public void bar() {
		@SuppressWarnings("unused")
		int i = 9;
	}

	public void param(@SuppressWarnings("unused") int i) {
	}

}
