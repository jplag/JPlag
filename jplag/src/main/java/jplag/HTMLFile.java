package jplag;


import java.io.*;

//  FILE HANDLING
public class HTMLFile extends PrintWriter {
  private BufferedCounter bc;
  
  /* This static method has to be used to instanciate HTMLFile objects. */
  public static HTMLFile createHTMLFile(File f) throws IOException {
    BufferedCounter new_bc = new BufferedCounter(
        new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
    HTMLFile htmlfile = new HTMLFile(new_bc);
    htmlfile.bc = new_bc;
//      if (Program.verbose_details)
//        Program.print(null,f.toString()+"  ");
    return htmlfile;
  }
  
  private HTMLFile(BufferedWriter bw) throws IOException {
    super(bw);
  }

  public void close() {
    super.close();
  }    

  public int bytesWritten() {
    return bc.bytesWritten();
  }
}
