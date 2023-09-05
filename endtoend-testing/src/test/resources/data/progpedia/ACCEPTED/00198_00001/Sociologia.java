import java.util.*;

class Sociologia {
	public static void main(String args[]){
		Scanner stdin = new Scanner(System.in);

		int ncenarios = stdin.nextInt();
		for (int i = 0; i < ncenarios; i++) {
			analisaCenario(stdin, i);
		}
	}

	static void analisaCenario(Scanner stdin, int indcenario) {
		int nalunos = stdin.nextInt();
		Grafo cenario = new Grafo(nalunos);
		for (int i = 0; i < nalunos; i++) {
			int id = stdin.nextInt() - 1;
			analisaAluno(cenario, id, stdin);
		}
		//System.out.println(cenario);
		System.out.printf("Caso #%d\n", indcenario + 1);
		contaGrupos(cenario);
	}

	static void analisaAluno(Grafo cenario, int id, Scanner stdin) {
		int namigos = stdin.nextInt();
		for (int i = 0; i < namigos; i++) {
			int idamigo = stdin.nextInt() - 1;
			Vertex amigo = cenario.vertices[idamigo];
			cenario.vertices[id].arcosadj.add(new Arco(amigo));
		}
	}

	static void contaGrupos(Grafo cenario) {
		int ngrupos4oumais = 0;
		int npessoasoutros = 0;
		Stack<Vertex> s = new Stack<Vertex>();
		while (s.size() < cenario.vertices.length) {
			Vertex u = findNonStacked(cenario);
			cenario.dfsVisit(u, s, 1, true);
		}

		cenario.reverteArcos();
		//System.out.println(cenario);
		while (!s.isEmpty()) {
			Vertex u = s.pop();
			if (u.stacked == true) {
				int npessoasgrupo = cenario.dfsVisit(u, s, 1, false);
				if (npessoasgrupo >= 4)
					ngrupos4oumais++;
				else
					npessoasoutros = npessoasoutros + npessoasgrupo;
			}
		}
		System.out.printf("%d %d\n", ngrupos4oumais, npessoasoutros);
	}

	static Vertex findNonStacked(Grafo cenario) {
		for (int i = 0; i < cenario.vertices.length; i++) {
			if (cenario.vertices[i].stacked == false)
				return cenario.vertices[i];
		}
		return null;
	}

	static class Grafo {
		Vertex[] vertices;

		Grafo(int n) {
			vertices = new Vertex[n];

			for (int i = 0; i < n; i++) {
				vertices[i] = new Vertex(i);
			}
		}

		public int dfsVisit(Vertex u, Stack<Vertex> s, int npessoasgrupo, boolean primvolta) {
			u.color = Vertex.Color.gray;
			for (int i = 0; i < u.arcosadj.size(); i++) {
				if (u.arcosadj.get(i).nofinal.color == Vertex.Color.white) {
					Vertex v = u.arcosadj.get(i).nofinal;
					npessoasgrupo++;
					npessoasgrupo = dfsVisit(v, s, npessoasgrupo, primvolta);
				}
			}
			u.color = Vertex.Color.black;
			if (primvolta == true) {
				s.push(u);
				u.stacked = true;
			}
			if (primvolta == false)
				u.stacked = false;
			return npessoasgrupo;
		}

		public void reverteArcos() {
			for (int i = 0; i < this.vertices.length; i++) {
				List<Arco> toRemove = new LinkedList<Sociologia.Arco>();
				for (int j = 0; j < this.vertices[i].arcosadj.size(); j++) {
					Arco actual = this.vertices[i].arcosadj.get(j);
					if (actual.processed == false) {
						Arco novo = new Arco(this.vertices[i]);
						novo.processed = true;
						this.vertices[actual.nofinal.id].arcosadj.add(novo);
						toRemove.add(actual);
					}
				}
				
				for (Arco arco : toRemove) {
					this.vertices[i].arcosadj.remove(arco);
				}
			}
			
			for (int i = 0; i < this.vertices.length; i++) {
				this.vertices[i].color = Vertex.Color.white;
			}
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (Vertex v : this.vertices) {
				sb.append("v = " + v.id + "\n");
				for (Arco a : v.arcosadj) {
					sb.append("\t--> " + a.nofinal.id + "\n");
				}
			}
			
			return sb.toString();
		}
	}

	static class Vertex {
		enum Color {
			white, gray, black
		}

		int id;
		Color color;
		boolean stacked;
		LinkedList<Arco> arcosadj;

		Vertex(int i) {
			id = i;
			color = Color.white;
			stacked = false;
			arcosadj = new LinkedList<Arco>();
		}
	}

	static class Arco {
		boolean processed;
		Vertex nofinal;

		Arco(Vertex n) {
			processed = false;
			nofinal = n;
		}
	}

}
