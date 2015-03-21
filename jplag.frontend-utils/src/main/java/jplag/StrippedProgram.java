package jplag;

/**
 * This is a stripped version of the JPlag main class Program to be used during
 * front end development only.
 */
public class StrippedProgram implements ProgramI {
	public void addError(String errorMsg) {
		System.err.println(errorMsg);
	}

	public void print(String normalMsg, String longMsg) {
		if (longMsg != null) {
			System.out.println(longMsg);
		} else if (normalMsg != null) {
			System.out.println(normalMsg);
		} else {
			System.out.println("Someboy messed up - no message given");
		}
	}
}
