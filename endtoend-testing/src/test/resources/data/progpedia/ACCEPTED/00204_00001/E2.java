import java.util.Scanner;
import java.util.LinkedList;
import java.util.Stack;

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


class E2 {
	public static void main(String args[]){
		Scanner in = new Scanner(System.in);
		int n = in.nextInt();
		for (int i = 1; i <= n; i++) {
			constroiGrafo(in, i);
		}
	}
	
	static void constroiGrafo(Scanner in, int ii){
		int nAl=in.nextInt();
		Grafo g = new Grafo(nAl);
		for (int i = 1; i <= nAl; i++) {
			int al = in.nextInt(), num = in.nextInt();
			for (int j = 1; j <= num; j++) {
				int p = in.nextInt();
				g.insert_new_arc(al, p, 0);
			}
		}
		
		System.out.println("Caso #" + ii);
		fortementeConexa(g);
	}
	
	static void DFSVisit(Grafo g, int v, boolean vis[], Stack<Integer> S){
		vis[v]=true;
		for (Arco a : g.adjs_no(v)) {
			if(vis[a.extremo_final()]==false){
				DFSVisit(g, a.extremo_final(), vis, S);
			}
		}
		S.push(v);
	}
	
	static void fortementeConexa(Grafo g){
		Stack<Integer> S = new Stack<Integer>();
		boolean vis[] = new boolean[g.nvs+1];
		for (int i = 1; i <= g.nvs; i++) {
			vis[i] = false;
		}
		for (int i = 1; i <= g.nvs; i++) {
			if(vis[i]==false){
				DFSVisit(g, i, vis, S);
			}
		}
		for (int i = 1; i <= g.nvs; i++) {
			vis[i] = false;
		}
		Grafo gt  = grafoTransposto(g);
		int nG = 0, num = gt.nvs;
		while(S.isEmpty()==false){
			int v = S.pop();
			if(vis[v]==false){
				Stack<Integer> Sa = new Stack<Integer>();
				DFSVisit(gt, v, vis, Sa);
				if(Sa.size()>=4){
					nG++;
					num -= Sa.size();
				}
			}
		}
		System.out.println(nG + " " + num);
	}
	
	static Grafo grafoTransposto(Grafo g){
		Grafo gt = new Grafo(g.nvs);
		for (int i = 1; i <= g.nvs; i++) {
			for (Arco a : g.adjs_no(i)) {
				gt.insert_new_arc(a.extremo_final(), i, 0);
			}
		}
		return gt;
	}
}




























