package jplag;


/* A simple but useful progress bar.
 * It is initialized with the maximum value and updated using the set(int)
 * method.
 */
public class Progress {
  private Program program;
  private long size;
  private long pos;

  static private final String progress_string =
    "0%---------------+----------------50%---------------+-------------100%\n";
  
  public Progress(int size, Program p) {
    this.program = p;
    this.size = size;
    pos = 0;
    program.print(progress_string, null);
  }

  public Progress(long size,Program p) {
  	this.program=p;
    this.size = size;
    pos = 0;
    program.print(progress_string, null);
  }

  public final void set(long count) {
    long new_pos = (count*70 / size);
    while (pos < new_pos) {
      program.print("#", null);
      pos++;
    }
  }

  public final void set(int count) {
    set((long)count);
  }
}



