//package cenas;

import java.util.LinkedList;
import java.util.Scanner;
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

class Sociologia {
	public static void main(String args[]){
		Scanner in = new Scanner(System.in);
		Grafo g;
		int casos = in.nextInt();
		for (int i = 0; i < casos; i++) {
			g = makeGraf(in);
			System.out.println("Caso #"+(i+1));
			DFS(g);
		}
		
	}
	static Grafo makeGraf(Scanner in) {
		int nAlunos = in.nextInt();
		Grafo g = new Grafo(nAlunos);
		
		for (int i = 0; i < nAlunos; i++) {
			int aluno = in.nextInt();
			int amigos = in.nextInt();
				for (int j = 0; j < amigos; j++) {
				int amigo = in.nextInt();
				g.insert_new_arc(aluno, amigo, 0);
			}
		}
		
		return g;
	}
	
	static void DFS(Grafo g) {
		Stack<Integer> s = new Stack<Integer>();
		int visitados[] = new int[g.nvs+1];
		for (int i = 1; i <= g.nvs; i++) {
			if(visitados[i]==0){
				DFS_Visit(g, i, s, visitados);
			}
		}
		
		Grafo gt = gTransposto(g);
		int vis[] = new int[g.nvs+1];
		int pessoas = 0, quatro = 0;
		while(!s.isEmpty()) {
			int i = s.pop();
			if(vis[i]==0){
				int c = DFS_Transposto(gt, i,vis);
				if(c>=4) {
					quatro++;
				}
				else{
					pessoas+=c;
				}
			}
		}
		System.out.println(quatro + " " + pessoas);
	}
	
	static void DFS_Visit(Grafo g, int v, Stack<Integer> s, int[] visitados){
		visitados[v]=1;
		for (Arco a : g.adjs_no(v)) {
			int w = a.extremo_final();
			if(visitados[w]==0){
				DFS_Visit(g, w, s, visitados);
			}
		}
		s.push(v);
		
	}
	
	static int DFS_Transposto(Grafo g, int v, int[] visitados) {
		int c=1;
		visitados[v]=1;
		for (Arco a : g.adjs_no(v)) {
			int w = a.extremo_final();
			if(visitados[w]==0){
				c+=DFS_Transposto(g, w, visitados);
				
			}
		}
		return c;
	}
	
	static Grafo gTransposto(Grafo g){
		Grafo gt = new Grafo(g.nvs);
		for (int i = 1; i <= g.nvs; i++) {
			for (Arco a : g.adjs_no(i)) {
				int w = a.extremo_final();
				gt.insert_new_arc(w, i, 0);
			}
		}
		return gt;
	}
}