import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class J7TryCatch {
	
	public void simpleTry() {
		try {
		} catch (Exception e) {
		} finally {
		}
	}
	
	public void tryWithResource(FileOutputStream fos) {
		try (DataOutputStream dos = new DataOutputStream(fos)) {
			dos.writeUTF("Java 7 Block Buster");
		} catch (IOException e) {
		}
	}
	
	public void tryWithResources() {
		try (FileOutputStream fos = new FileOutputStream("movies.txt"); DataOutputStream dos = new DataOutputStream(fos)) {
			dos.writeUTF("Java 7 Block Buster");
		} catch (IOException e) {
		}
	}
	
	public void newMultiCatch() {
		try {
			methodThatThrowsThreeExceptions();
		} catch (ExceptionOne | ExceptionTwo | ExceptionThree e) {
		}
	}

	public void newMultiMultiCatch() {
		try {
			methodThatThrowsThreeExceptions();
		} catch (ExceptionOne e) {
		} catch (ExceptionTwo | ExceptionThree e) {
		}
	}
}
