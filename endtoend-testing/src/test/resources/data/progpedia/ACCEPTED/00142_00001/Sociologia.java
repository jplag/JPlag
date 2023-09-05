import java.util.*;

class No {

	int aluno;
	Boolean visitado;
	LinkedList<Integer> amigos;
	LinkedList<Integer> DFSresultado;

	No(int alu) {
		aluno = alu;
		amigos = new LinkedList<Integer>();
		DFSresultado = new LinkedList<Integer>();
		visitado = false;
	}
}

class Grafo {
	LinkedList<No> total;
	

	Grafo() {
		total = new LinkedList<No>();
	}

	void ColocaGrafo(int alu) {
		total.add(new No(alu));
	}

	void AdicionaLigacao(int amigo, int aluno) {
		for (No n : total) {
			if (n.aluno == aluno)
				n.amigos.add(amigo);
		}
	}

	void DFS(Grafo grafo) {
		for (No n : total) {
			if (n.visitado==false)
				DFSVisit(n, grafo, n);
			for (No k : total)
				k.visitado=false;
		}
	}

	void DFSVisit(No z, Grafo grafo, No n) {
		n.visitado=true;
		for (int a : n.amigos)
			for (No f : total) {
				if (a == f.aluno)
					if (f.visitado == false)
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

			for (No n : grafo.total) {
				if (n.visitado == false) {
					fim = n.DFSresultado;
					for (No t : grafoT.total)
						if (t.aluno == n.aluno)
							fim.retainAll(t.DFSresultado);
					for (int h : fim) {
						for (No f : grafo.total) {
							if (h == f.aluno && f.DFSresultado.size() != 1)
								f.visitado=true;
						}
					}
					if (fim.size() >= 4)
						ngrupos++;
				}
			}
			for (No n : grafo.total)
				if (n.visitado==false)
					nsgrupo++;
			if (ngrupos == 0)
				nsgrupo = nalunos;

			System.out.println("Caso #" + count);
			System.out.println(ngrupos + " " + nsgrupo);
			count++;
		}
	}
}
/*
4
4
1 3 2 4 3
4 0
2 2 1 3
3 2 2 1
6
1 2 3 5
2 2 3 4
4 1 2
3 2 2 1
6 1 5
5 2 6 1
8
1 4 6 2 4 5
3 1 2
2 2 3 4
4 1 5
6 0
5 3 4 8 7
7 1 5
8 2 5 3
10
1 4 6 2 4 5
3 2 2 1
9 0
2 2 3 4
4 2 5 9
6 1 1
5 3 4 8 7
7 1 5
8 1 5
10 1 9
*/