package jplag;


import java.io.*;

/* This stream adds a newline to the end of a file.
 * this is a proxy
 */
public class NewlineStream extends InputStream {
  private int endOfFile = 0;
  private InputStream stream;

  public NewlineStream(InputStream stream) {
    super();
    this.stream = stream;
  }

  public int read() throws IOException {
    int result;
    switch (endOfFile) {
    case 0:
      result = stream.read();
      if (result == -1) {
	result = 13;
	endOfFile = 1;
      }
      break;
    case 1:
      result = 10;
      endOfFile = 2;
      break;
    default:
      result = -1;
      break;
    }
    return result;
  }
}
