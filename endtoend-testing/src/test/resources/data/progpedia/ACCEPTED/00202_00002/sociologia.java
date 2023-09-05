/*-------------------------------------------------------------------*\
|  Definicao de grafos com pesos (int)                                |
|     Assume-se que os vertices sao numerados de 1 a |V|.             |
|                                                                     |
|   A.P.Tomas, CC211 (material para prova pratica), DCC-FCUP, 2012    |
|   Last modified: 2013.01.03                                         |
\--------------------------------------------------------------------*/

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

class sociologia {
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		
		int n = in.nextInt();
		
		for (int i = 0; i < n; i++) {
			Grafo g = criaGrafo(in);
			kosarajuAdapt(g, i+1);
		}
	}
	
	static Grafo criaGrafo(Scanner in) {
		int nalunos = in.nextInt();
		Grafo g = new Grafo(nalunos);
		
		for (int i = 0; i < nalunos; i++) {
			int v = in.nextInt();
			int nr = in.nextInt();
			for (int j = 0; j < nr; j++) {
				g.insert_new_arc(v, in.nextInt(), 0);
			}
		}
		
		return g;
	}
	
	static void kosarajuAdapt(Grafo g, int num) {
		LinkedList<Integer> S = stackDFS(g);
		Grafo gt = gTransposto(g);
		
		boolean vis[] = new boolean[g.num_vertices()+1];
		
		int d=0;
		int f=0;
		
		while (!S.isEmpty()) {
			int v = S.pop();
			
			int c=0;
			if(!vis[v]) {
				c = DFS_Transposto(gt, v, vis);
			
				if(c>=4) d++;
				else f+=c;
			}
		}
		
		System.out.println("Caso #"+num);
		System.out.println(d+" "+f);
	}
	
	static Grafo gTransposto(Grafo g) {
		Grafo gt = new Grafo(g.num_vertices());
		
		for (int i = 1; i <= g.num_vertices(); i++) {
			for (Arco a : g.adjs_no(i)) {
				gt.insert_new_arc(a.extremo_final(), i, 0);
			}
		}
		
		return gt;
	}
	
	static LinkedList<Integer> stackDFS(Grafo g) {
		LinkedList<Integer> S = new LinkedList<Integer>();
		boolean vis[] = new boolean[g.num_vertices()+1];
		
		for (int i = 1; i <= g.num_vertices(); i++) {
			if(!vis[i]) {
				stackDFS_Visit(g, i, S, vis);
			}
		}
		
		return S;
	}
	
	static void stackDFS_Visit(Grafo g, int v, LinkedList<Integer> S, boolean vis[]) {
		vis[v] = true;
		
		for (Arco a : g.adjs_no(v)) {
			int w = a.extremo_final();
			
			if(!vis[w]) {
				stackDFS_Visit(g, w, S, vis);
			}
		}
		
		S.push(v);
	}
	
	static int DFS_Transposto(Grafo g, int v, boolean vis[]) {
		vis[v] = true;
		int c = 1;
		
		for (Arco a : g.adjs_no(v)) {
			int w = a.extremo_final();
			
			if(!vis[w]) {
				c += DFS_Transposto(g, w, vis);
			}
		}
		
		return c;
	}
}