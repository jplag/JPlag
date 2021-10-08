package de.jplag.scheme;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InsertStream extends FileInputStream {
  public InsertStream(File file) throws FileNotFoundException {
    super(file);
  }
  public InsertStream(FileDescriptor fdObj) {
    super(fdObj);
  }
  public InsertStream(String name) throws FileNotFoundException {
    super(name);
  }

  boolean end_of_file = false;

  @Override
public int read() throws IOException {
    int result = super.read();
    if (result != -1 || end_of_file) return result;
    end_of_file = true;
    return Character.LINE_SEPARATOR;
  }

  @Override
public int read(byte[] b) throws IOException {
    int result = super.read(b);
    if (result != -1 || end_of_file) return result;
    end_of_file = true;
    if (b.length > 1) {
      b[0] = Character.LINE_SEPARATOR;
      return 1;
    }
    return -1;
  }

  @Override
public int read(byte[] b, int off, int len) throws IOException {
    int result = super.read(b,off,len);
    if (result != -1 || end_of_file) return result;
    end_of_file = true;
    if (len > 0) {
      b[off] = Character.LINE_SEPARATOR;
      return 1;
    } else return 0;
  }
}
