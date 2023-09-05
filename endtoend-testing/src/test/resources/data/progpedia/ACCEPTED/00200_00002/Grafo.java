import java.util.*;

class Edge {
	int outro;

	Edge(int w) {
		outro = w;
	}
}

class Node {
	LinkedList<Edge> adj;
	boolean visitado;

	Node() {
		adj = new LinkedList<Edge>();
		visitado = false;
	}

}

class Grafo {
	final int V;
	int E;
	Node[] nos;
	int color[];
	//int pred[];
	int cor[];

	Grafo(int v) {
		V = v;
		E = 0;
		nos = new Node[V + 1];
		for (int i = 1; i < V + 1; i++)
			nos[i] = new Node();
		//pred = new int[V + 1];
		color = new int[V + 1];
		cor = new int[V + 1];
	}

	public void addEdge(int v, int w) {
		nos[v].adj.addFirst(new Edge(w));
		E++;
	}

	public void totxt() {
		System.out.println("V: " + V + " " + "E: " + E);
		for (int i = 1; i < V + 1; i++) {
			System.out.print(i + ": ");
			for (Edge e : nos[i].adj) {
				System.out.print(e.outro + "|");
			}
			System.out.println();
		}

	}

	LinkedList<Integer> s = new LinkedList<Integer>();

	public void DFS() {

		for (int i = 1; i < V + 1; i++) {
			color[i] = 0; // 0 white 1 gray 2 black
			//pred[i] = 0;
		}
		for (int i = 1; i < V + 1; i++) {
			if (color[i] == 0)
				VDFS(i);
		}
		// for (int i = 1; i < V + 1; i++)
		// System.out.print(pred[i] + "_" + i + " ");
		// System.out.println();

		//System.out.println(s.toString());

	}

	public void VDFS(int u) {
		color[u] = 1;
		for (Edge e : nos[u].adj) {
			if (color[e.outro] == 0) {
				//pred[e.outro] = u;
				VDFS(e.outro);
			}

		}
		s.push(u);
		color[u] = 2;

	}

	public void dfs2(Grafo R, int r) {
		cor[r] = 1;

		for (Edge e : R.nos[r].adj) {
			if (cor[e.outro] == 0 && R.nos[e.outro].visitado == false) {
				dfs2(R, e.outro);
			}
		}
		path.addLast(r);
		R.nos[r].visitado = true;
		cor[r] = 2;
		
	}

	public Grafo reverse() {
		Grafo R = new Grafo(V);
		for (int v = 1; v < V + 1; v++) {
			for (Edge w : nos[v].adj) {
				R.addEdge(w.outro, v);
			}
		}
		return R;
	}
	LinkedList<Integer> path;
	public void Kosajaru(Grafo R, int ni) {
		
		int v;
		int r1=0 ,r2=0;
		while (!s.isEmpty()) {
			path = new LinkedList<Integer>();
			v = s.pop();
			dfs2(R,v);
			//System.out.println("-"+path.toString());
			if (path.size()>=4)
				r1++;
			else
				r2=r2 + path.size();
			while (!path.isEmpty()){
				s.remove(path.removeLast());
			}
		}
		System.out.println("Caso #"+ni);
		System.out.println(r1+" "+r2);
	}

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int nc, na, id, nf, fid;

		nc = in.nextInt();
		for (int i = 0; i < nc; i++) {
			na = in.nextInt();
			Grafo G = new Grafo(na);
			for (int j = 0; j < na; j++) {
				id = in.nextInt();
				nf = in.nextInt();
				for (int t = 0; t < nf; t++) {
					fid = in.nextInt();
					G.addEdge(id, fid);
				}
			}
			// G.totxt();
			// System.out.println("___________________");
			G.DFS();
			 G.Kosajaru(G.reverse(),i+1);
			// Grafo R;
			// R=G.reverse();
			// R.totxt();
			// System.out.println("-------------");
			// R.DFS();
		}

	}

}
