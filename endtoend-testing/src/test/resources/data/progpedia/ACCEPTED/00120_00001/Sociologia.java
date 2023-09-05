import java.util.*;

class Node {

	int aluno;
	int cor;
	LinkedList<Integer> amigos;
	LinkedList<Integer> DFSresultado;

	Node(int alu) {
		aluno = alu;
		amigos = new LinkedList<Integer>();
		DFSresultado = new LinkedList<Integer>();
		cor = 0;
	}
}

class Grafo {
	LinkedList<Node> total;
	LinkedList<Node> tempos;

	Grafo() {
		total = new LinkedList<Node>();
	}

	void ColocaGrafo(int alu) {
		total.add(new Node(alu));
	}

	void AdicionaLigacao(int amigo, int aluno) {
		for (Node n : total) {
			if (n.aluno == aluno)
				n.amigos.add(amigo);
		}
	}

	void DFS(Grafo grafo) {
		for (Node n : total) {
			if (n.cor == 0)
				DFSVisit(n, grafo, n);
			for (Node k : total)
				k.cor = 0;
		}
	}

	void DFSVisit(Node z, Grafo grafo, Node n) {
		n.cor = 1;
		for (int a : n.amigos)
			for (Node f : total) {
				if (a == f.aluno)
					if (f.cor == 0)
						DFSVisit(z, grafo, f);
			}
		z.DFSresultado.add(n.aluno);
	}
}

public class Sociologia {
	public static void main(String[] args) {

		Scanner in = new Scanner(System.in);
		int count = 1;
		int ncenarios = in.nextInt();		
		LinkedList<Integer> fim = new LinkedList<Integer>();
		
		while (count <= ncenarios) {
			int ngrupos = 0, nsgrupo = 0;
			int nalunos = in.nextInt();
			Grafo grafo = new Grafo();
			Grafo grafoT = new Grafo();
			for (int i = 1; i <= nalunos; i++) {
				grafo.ColocaGrafo(i);
				grafoT.ColocaGrafo(i);
			}
			for (int i = 0; i < nalunos; i++) {
				int aluno = in.nextInt();
				int namigos = in.nextInt();
				for (int j = 0; j < namigos; j++) {
					int amigo = in.nextInt();
					grafo.AdicionaLigacao(amigo, aluno);
					grafoT.AdicionaLigacao(aluno, amigo);
				}
			}
			grafo.DFS(grafo);
			grafoT.DFS(grafoT);

			for (Node n : grafo.total) {
				if (n.cor == 0) {
					fim = n.DFSresultado;
					for (Node t : grafoT.total)
						if (t.aluno == n.aluno)
							fim.retainAll(t.DFSresultado);
					for (int h : fim) {
						for (Node f : grafo.total) {
							if (h == f.aluno && f.DFSresultado.size() != 1)
								f.cor = 1;
						}
					}
					if (fim.size() >= 4)
						ngrupos++;
				}
			}
			for (Node n : grafo.total)
				if (n.cor == 0)
					nsgrupo++;
			if (ngrupos == 0)
				nsgrupo = nalunos;

			System.out.println("Caso #" + count);
			System.out.println(ngrupos + " " + nsgrupo);
			count++;
		}
	}
}