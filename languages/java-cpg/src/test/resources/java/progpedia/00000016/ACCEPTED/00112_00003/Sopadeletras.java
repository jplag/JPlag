import java.util.LinkedList;
import java.util.Scanner;

class Arco {
    int no_final;
    int valor;
    
    Arco(int fim, int v){
	no_final = fim;
	valor = v;
    }

    int extremo_final() {
	return no_final;
    }

    int valor_arco() {
	return valor;
    }
}


class No {
    //int label;
    LinkedList<Arco> adjs;

    No() {
	adjs = new LinkedList<Arco>();
    }
}


class Grafo {
    No verts[];
    int nvs, narcos;
			
    public Grafo(int n) {
	nvs = n;
	narcos = 0;
	verts  = new No[n+1];
	for (int i = 0 ; i <= n ; i++)
	    verts[i] = new No();
        // para vertices numerados de 1 a n (posicao 0 nao vai ser usada)
    }
    
    public int num_vertices(){
	return nvs;
    }

    public int num_arcos(){
	return narcos;
    }

    public LinkedList<Arco> adjs_no(int i) {
	return verts[i].adjs;
    }
    
    public void insert_new_arc(int i, int j, int valor_ij){
	verts[i].adjs.addFirst(new Arco(j,valor_ij));
        narcos++;
    }

    public Arco find_arc(int i, int j){
	for (Arco adj: adjs_no(i))
	    if (adj.extremo_final() == j) return adj;
	return null;
    }
}

public class Sopadeletras {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		String s1, s2; 
		
		s1 = in.next();
		s2 = in.next();
		
		
		
		int t1 = s1.length(); int t2 = s1.length();
		
		int t = Math.min(t1, t2);
		
		Grafo sopa = new Grafo(20);
		int flag = 0;
		char a, b;
		
		for(int i = 0; i< t; i++){
			a = s1.charAt(i);
			b = s2.charAt(i);
			
			if(a != b){
				System.out.print(a); System.out.println(b);
				break;
			}
			else if(i == (t-1))
				flag=1;
		}
		
		if(flag == 1)
			System.out.println("Nenhum");
	}

}
