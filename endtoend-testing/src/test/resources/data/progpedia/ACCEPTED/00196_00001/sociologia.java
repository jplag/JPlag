import java.util.*;

class Grafo {
    int nverts, narcos;
    Vertice [] verts;

    Grafo (int n) {
	    nverts = 0;
	    narcos = 0;
	    verts = new Vertice[n];
    }

    void novoVert (int n) {
	    verts[n] = new Vertice(n);
	    nverts++;
    }

    void novoAdj (int v, int a, int c) {
    	if (procuraArco(v, a) == 0) {
		    verts[v].adicionarAdj(a, c);
		    narcos++;
		}
    }

    int procuraArco(int v, int d) {
    	Arco aux;
    	aux = verts[v].adjs;
    	while (aux != null) {
	    	if (aux.proxVert == d) return 1;
	    	else aux = aux.prox;
    	}

    	return 0;
    }
}

class Arco {
    int proxVert, c;
    Arco prox;

    Arco (int v, int ca) {
    proxVert = v;
    c = ca;
    prox = null;
    }

    Arco (int v, int ca, Arco a) {
    proxVert = v;
    c = ca;
    prox = a;
    }
}

class Vertice {
    int label;
    Arco adjs;

    Vertice (int v) {
    label = v;
    adjs = null;
    }

    void adicionarAdj (int n, int c) {
    adjs = new Arco(n, c, adjs); 
    }
}

class Fila {
	Node first;
	int length;

	Fila () {
		first = null;
		length = 0;
	}

	void imprimeFila() {
		Node aux;
		aux = first;
		while (aux != null) {
			System.out.println(aux.val);
			aux = aux.prox;
		}
	}

	boolean isEmpty() {
		if (length == 0) return true;
		return false;
	}

	void pushNode (int v) {
		Node aux;
		if (length == 0) {
			first = new Node(v, null);
			length++;
		} else {
			aux = new Node(v, first);
			first = aux;
			length++;
		}
	}

	int popNode () {
		int aux;
		aux = first.val;
		first = first.prox;
		length--;
		return aux;
	}

	class Node {
		int val;
		Node prox;

		Node (int v, Node p) {
			val = v;
			prox = p;
		}
	}
}

class Set {
	int [] s;
	int size, length;

	Set (int x) {
		int i;
		size = x+1;
		length = 0;
		s = new int[x+1];
		for (i=0; i < x+1; i++) s[i] = 0;
	}

	void insert (int n) { s[n] = 1; length++; }
	void remove (int n) { s[n] = 0; length--; }
	int size () { return length; }

	void imprime () {
		int i;
		for (i=1; i<size; i++) {
			if (s[i] == 1) System.out.println(i);
		}
	}

	boolean isEqual (Set x) {
		int i;
		if (length != x.length) return false;
		for (i=0; i < size; i++)
			if (s[i] != x.s[i]) return false;
		return true;
	}

	Set intersect (Set x) {
		Set aux = new Set(size);
		int i;
		for (i=0; i < size; i++) {
			if (s[i] == 1 && x.s[i] == 1) aux.insert(i);
		}
		return aux;
	}
}

class Lista {
	Node first;
	int length;

	Lista () {
		first = null;
		length = 0;
	}

	void addNode (Set n) {
		Node aux;
		int flag = 0;
		if (length == 0) {
			first = new Node(n, null);
			length++;
		} else {
			aux = first;
			flag = 0;
			while (aux != null) {
				if (aux.s.isEqual(n) == true) {
					flag = 1;
					break;
				}
				aux = aux.prox;
			}

			if (flag == 0) {
				aux = new Node(n, first);
				first = aux;
				length++;
			}
		}
		
	}

	class Node {
		Set s;
		Node prox;

		Node (Set n, Node p) {
			s = n;
			prox = p;
		}
	}
}

class sociologia {
	static Set depthFirstSearch (Grafo g, int v, int n) {
		int lidos[] = new int[n+1];
		int cont, i;
		Arco aux;
		Fila stack = new Fila();
		Set s = new Set(n+1);

		for (i=0; i < n+1; i++) lidos[i] = 0;

		stack.pushNode(v);
		s.insert(v);
		while (stack.isEmpty() == false) {
			i = stack.popNode();
			lidos[i] = 1;
			if (g.verts[i] != null) {
				aux = g.verts[i].adjs;
				while (aux != null) {
					if (lidos[aux.proxVert] == 0) {
						stack.pushNode(aux.proxVert);
						lidos[aux.proxVert] = 1;
						s.insert(aux.proxVert);
					}
					aux = aux.prox;
				}	
			}
		}

		return s;
	}

	public static void main (String [] args) {
		int ncasos, npessoas, i, j, n, aux, c;
		int grupo[];
		Grafo g, trans;
		Set s1, s2, s;
		Scanner kb = new Scanner(System.in);
		Fila listaSets;
		Lista sets;

		ncasos = kb.nextInt();
		for (c=0; c < ncasos; c++) {
			sets = new Lista();
			listaSets = new Fila();

			npessoas = kb.nextInt();
			aux = npessoas;
			g = new Grafo(npessoas+1);
			trans = new Grafo(npessoas+1);
			while (aux > 0) {
				i = kb.nextInt();
				g.novoVert(i);
				j = kb.nextInt();
				while (j > 0) {
					n = kb.nextInt();
					g.novoAdj(i, n, 0);
					if (trans.verts[n] == null) trans.novoVert(n);
					trans.novoAdj(n, i, 0); 
					j--;
				}
				aux--;
			}

			grupo = new int[npessoas+1];
			for (i=0; i <= npessoas; i++) grupo[i] = 0;
			for (i=1; i <= npessoas; i++) {
				s1 = depthFirstSearch(g, i, npessoas);
				s2 = depthFirstSearch(trans, i, npessoas);
				s = s1.intersect(s2);
				if (s.length >= 4) {
					sets.addNode(s);
					for (j=1; j < s.size; j++) {
						if (s.s[j] == 1) grupo[j] = 1;
					}
				}
			}

			j = 0;
			for (i=1; i <= npessoas; i++) {
				if (grupo[i] == 0) j++;
			}
			System.out.println("Caso #" + (c+1));
			System.out.println(sets.length + " " + j);
		}
	}
}