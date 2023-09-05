/*-------------------------------------------------------------------*\
|  Definicao de grafos com pesos (int)                                |
|     Assume-se que os vertices sao numerados de 1 a |V|.             |
|                                                                     |
|   A.P.Tomas, CC211 (material para prova pratica), DCC-FCUP, 2012    |
|   Last modified: 2013.01.03                                         |
\--------------------------------------------------------------------*/

import java.text.DateFormatSymbols;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

class Arco {
	int no_final;
	int valor;

	Arco(int fim, int v) {
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
	// int label;
	LinkedList<Arco> adjs;
	boolean visitado = false;

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
		verts = new No[n + 1];
		for (int i = 0; i <= n; i++)
			verts[i] = new No();
		// para vertices numerados de 1 a n (posicao 0 nao vai ser usada)
	}

	public int num_vertices() {
		return nvs;
	}

	public int num_arcos() {
		return narcos;
	}

	public LinkedList<Arco> adjs_no(int i) {
		return verts[i].adjs;
	}

	public void insert_new_arc(int i, int j, int valor_ij) {
		verts[i].adjs.addFirst(new Arco(j, valor_ij));
		narcos++;
	}

	public Arco find_arc(int i, int j) {
		for (Arco adj : adjs_no(i))
			if (adj.extremo_final() == j)
				return adj;
		return null;
	}
}

public class main {

	public static Stack<Integer> DFS(Grafo g) {
		Stack<Integer> s = new Stack<Integer>();
		int visitados[] = new int[g.num_vertices() + 1];
		for (int i = 1; i <= g.num_vertices(); i++) {
			visitados[i] = 0;
		}
		for (int i = 1; i <= g.num_vertices(); i++) {
			if (visitados[i] == 0)
				DFS_Visit(i, g, s, visitados);
		}
		return s;
	}

	public static void DFS_Visit(int v, Grafo g, Stack<Integer> s,
			int[] visitados) {
		visitados[v] = 1;
		for (Arco a : g.adjs_no(v)) {
			int w = a.extremo_final();
			if (visitados[w] == 0) {
				DFS_Visit(w, g, s, visitados);
			}
		}
		s.push(v);
	}

	public static int DFS_V_Transp(int v, Grafo g, int[] visitados) {
		int c = 1;
		visitados[v] = 1;
		for (Arco a : g.adjs_no(v)) {
			int w = a.extremo_final();
			if (visitados[w] == 0)
				c += DFS_V_Transp(w, g, visitados);
		}
		return c;
	}

	public static Grafo lerGrafo(Scanner stdin) {
		int nAlunos = stdin.nextInt();
		Grafo g = new Grafo(nAlunos);
		for (int j = 0; j < nAlunos; j++) {
			int aluno = stdin.nextInt();
			int nAmigos = stdin.nextInt();
			for (int k = 0; k < nAmigos; k++) {
				int amigos = stdin.nextInt();
				g.insert_new_arc(aluno, amigos, 0);

			}
		}
		return g;
	}

	public static Grafo gTransp(Grafo g) {
		Grafo gt = new Grafo(g.num_vertices());
		for (int i = 1; i <= g.num_vertices(); i++) {
			for (Arco a : g.adjs_no(i)) {
				int w = a.extremo_final();
				gt.insert_new_arc(w, i, 0);
			}
		}
		return gt;
	}

	public static void main(String[] args) {

		Scanner stdin = new Scanner(System.in);
		Stack<Integer> s = new Stack<Integer>();
		int nCenarios = 0;

		Grafo g;

		nCenarios = stdin.nextInt();
		for (int i = 0; i < nCenarios; i++) {
			g = lerGrafo(stdin);
			System.out.println("Caso #" + (i + 1));
			s = DFS(g);

			int dentroGrupo = 0;
			int foraGrupo = 0;

			Grafo gt = gTransp(g);

			int[] visitados = new int[gt.num_vertices() + 1];
			for (int v = 1; v <= g.num_vertices(); v++)
				visitados[v] = 0;
			while (!s.isEmpty()) {
				int v = s.pop();
				if (visitados[v] == 0) {
					int c = DFS_V_Transp(v, gt, visitados);
					if (c >= 4) {
						dentroGrupo++;
					} else {
						foraGrupo += c;
					}
				}
			}
			System.out.println(dentroGrupo + " " + foraGrupo);
		}

	}

}
