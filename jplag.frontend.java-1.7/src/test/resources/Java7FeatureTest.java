import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Java7FeatureTest {

	int thousand = 1_000;
	int million = 1_000_000;

	int foo = 1234_5678;
	long l = 1_2_3_4__5_6_7_8L;
	int binary = 0b0001_0010_0100_1000;

	int mask = 0b1010_1010_1010;
	long big = 9_223_783_036_967_937L;
	long creditCardNumber = 1234_5678_9012_3456L;
	long socialSecurityNumber = 999_99_9999L;
	float pi = 3.14_15F;
	long hexBytes = 0xFF_EC_DE_5E;
	long hexWords = 0xCAFE_BFFE;

	public static void diamondOperator() {
		// old
		Map<String, List<Object>> tradesOld = new TreeMap<String, List<Object>>();

		// new
		Map<String, List<Object>> trades = new TreeMap<>();
	}

	private void stringSwitch(String foo) {

		// old
		if (foo.equalsIgnoreCase("new")) {
		} else if (foo.equalsIgnoreCase("exec")) {
		} else if (foo.equalsIgnoreCase("pend")) {
		}

		// new
		switch (foo) {
		case "new":
			break;
		case "exec":
			break;
		case "pend":
			break;
		default:
			break;
		}
	}

	public void oldTry() {
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		try {
			fos = new FileOutputStream("movies.txt");
			dos = new DataOutputStream(fos);
			dos.writeUTF("Java 7 Block Buster");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				dos.close();
			} catch (IOException e) {
			}
		}
	}

	public void newTry() {
		try (FileOutputStream fos = new FileOutputStream("movies.txt"); DataOutputStream dos = new DataOutputStream(fos)) {
			dos.writeUTF("Java 7 Block Buster");
		} catch (IOException e) {
		}
	}

	public void oldMultiCatch() {
		try {
			methodThatThrowsThreeExceptions();
		} catch (ExceptionOne e) {
		} catch (ExceptionTwo e) {
		} catch (ExceptionThree e) {
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

	private void methodThatThrowsThreeExceptions() throws ExceptionOne, ExceptionTwo, ExceptionThree {
	}

	public void foo(String bar) throws ExceptionOne, ExceptionTwo {
		try {
		} catch (Exception e) {
			throw e;
		}
	}
}
