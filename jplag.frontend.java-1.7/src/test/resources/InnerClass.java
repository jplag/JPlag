
public class InnerClass {
	public void foo() {
	}

	public class Bar {
		public void bar() {
		}
	}
	
	public class Bar {
		public class BBar {}
		private class BFoo {}
	}

	private class BarFoo {
		public void barFoo() {
		}
	}

}
