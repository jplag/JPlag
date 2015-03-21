package jplag.clustering;

//import jplag.*;

public class SimilarityMatrix {
  private int size;
  private float [] matrix;
//   private float maximum = 0;
  
  /** The parameter is the number of submissions. */
  public SimilarityMatrix(int size) {
    this.size = size;
    matrix = new float[size*(size+1)/2];
  }

  public int size() { return size; }

  public void setSimilarity(int a, int b, float sim) {
    int tmp;
    if (a >= size || b >= size)
      throw new ArrayIndexOutOfBoundsException();
    if (a > b) {
      tmp = a;   a = b;   b = tmp;
    }
    matrix[a+(b*(b-1)/2)] = sim;

//     if (sim > maximum)
//       maximum = sim;
  }

  //  public float getMaximum() { return maximum; }

  public float getSimilarity(int a, int b) {
    int tmp;
    if (a >= size || b >= size)
      throw new ArrayIndexOutOfBoundsException();
    if (a > b) {
      tmp = a;   a = b;   b = tmp;
    }
    return matrix[a+(b*(b-1)/2)];
  }

  public String toString() {
    String tmp = "";
    for (int a=0; a<size; a++) {
      for (int b=a+1; b<size; b++) {
	int length = tmp.length();
	tmp += (int)getSimilarity(a,b);
	while (tmp.length() < length + 3)
	  tmp += " ";
      }
      tmp += "\n";
    }
    return tmp;
  }
}
