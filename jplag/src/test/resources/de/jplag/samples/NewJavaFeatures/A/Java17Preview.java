public class Java17Preview {

	public Java17Preview() {
		Shape s = Math.random() > 0.5 ? new Circle(42) : new Rect(1, 4);
		double area = switch (s) {
			case Circle c -> c.area() + 0.123;
			case Rect r -> r.area() + r.area();
		};

		System.out.println(area);
	}

	private abstract static sealed class Shape permits Circle, Rect {
		public abstract double area();
	}

	private static final class Circle extends Shape {
		private double r;

		private Circle(double r) {
			this.r = r;
		}

		@Override
		public double area() {
			return 2 * Math.PI * r * r;
		}
	}

	private static final class Rect extends Shape {
		private final int a;
		private final int b;

		private Rect(int a, int b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public double area() {
			return a * b;
		}
	}
}
