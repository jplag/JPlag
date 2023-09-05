import java.util.Scanner;
import java.util.LinkedList;
import java.util.ListIterator;

class node{
	LinkedList<Integer> adj;
	boolean visited;
	node(){
		adj = new LinkedList<Integer>();
		visited = false;
	}
}

class graph{
	int nv,sol1,sol2,res;
	node[] vertices;
	LinkedList<Integer> stack;

	graph(int n){
		nv = n;
		sol1 = 0;
		sol2 = 0;
		vertices = new node[nv];
		for(int i=0;i<nv;i++)
			vertices[i] = new node();
	}

	public void addEdge(int from, int to){
		vertices[from].adj.addLast(to);
	}

	public void cleanVisited(){
		for(int i=0;i<nv;i++)
			vertices[i].visited = false;
	}

	public void dfs(){
		cleanVisited();
		stack = new LinkedList<Integer>();
		for(int i=0;i<nv;i++)
			if(!vertices[i].visited)
				dfs_pushstack(i);
	}

	public void dfs_pushstack(int c){
		vertices[c].visited = true;
		ListIterator<Integer> li = vertices[c].adj.listIterator(0);
		while(li.hasNext()){
			int next = li.next();
			if(!vertices[next].visited)
				dfs_pushstack(next);
		}

		stack.addFirst(c);
	}

	public void dfs_popstack(LinkedList<Integer> stack_){
		int c;
		stack = stack_;

		cleanVisited();

		while(!stack.isEmpty()){
			c = stack.removeFirst();

			if(!vertices[c].visited){
				int res = dfs_scc(c);
				if(res > 3)
					sol1++;
				else
					sol2 += res;
			}
		}
	}

	public int dfs_scc(int c){
		vertices[c].visited = true;
		int count=1;
		ListIterator<Integer> li = vertices[c].adj.listIterator();
		while(li.hasNext()){
			int next = li.next();
			if(!vertices[next].visited)
				count += dfs_scc(next);
		}

		return count;
	}
}

class sociologia{
	static graph gr,grt;
	static Scanner sc;

	public static void main(String[] args){
		sc = new Scanner(System.in);
		int ncases = sc.nextInt();

		for(int i=0;i<ncases;i++){
			readGraph();
			gr.dfs();
			grt.dfs_popstack(gr.stack);
			System.out.println("Caso #" + (i+1));
			System.out.println(grt.sol1 + " " + grt.sol2);
		}
	}

	public static void readGraph(){
		int np = sc.nextInt();
		gr = new graph(np);
		grt = new graph(np);

		for(int k=0;k<np;k++){
			int p = sc.nextInt();
			int na = sc.nextInt();
			for(int j=0;j<na;j++){
				int a = sc.nextInt();
				gr.addEdge(p-1,a-1);
				grt.addEdge(a-1,p-1);
			}
		}
	}
}