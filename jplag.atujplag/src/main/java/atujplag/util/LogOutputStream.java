package atujplag.util;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class LogOutputStream extends PrintStream {
	boolean autoFlush = false;

	public LogOutputStream(OutputStream out) {
		super(out);
		this.out = out;
	}

	public LogOutputStream(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
		this.out = out;
		this.autoFlush = autoFlush;
	}

	public LogOutputStream reopen() throws FileNotFoundException {
		out = new FileOutputStream(System.getProperty("log.dir") +
				System.getProperty("file.separator") +
				System.getProperty("soap.msgs.file"), true);
		LogOutputStream log = new LogOutputStream(out, autoFlush);

		return log;
	}

	public void close() {
		super.close();
	}

}

