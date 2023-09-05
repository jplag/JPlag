import java.util.LinkedList;
import java.util.Scanner;

class Node{
	int value;
	int timef;
	boolean visit;
	LinkedList<Node> adj;
	
	Node(int v){
		value = v;
		timef = 0;
		visit = false;
		adj = new LinkedList<Node>();
	}
	
	void addConnection(Node n){
		adj.addLast(n);
	}
}

class Graph{
	int nstudents;
	int time;
	Node g[];
	Node gt[];
	LinkedList<Integer> group;
	
	Graph(int nst){
		nstudents = nst;
		g = new Node[nst+1];
		gt = new Node[nst+1];
		time = 0;
		group = new LinkedList<Integer>();
	}
	
	void initialize(){
		for(int i=1; i<=nstudents; i++){
			g[i] = new Node(i);
			gt[i] = new Node(i);
		}
	}
	
	void createGraph(int s, int f){
		g[s].addConnection(g[f]);
		gt[f].addConnection(gt[s]);
	}
	
	
	void DFS(){
		for(int i=1; i<=nstudents; i++){
			if(!g[i].visit)
				DFS_Visit(g[i]);
		}
	}
	private void DFS_Visit(Node n){
		time++;
		n.visit = true;
		
		int size = n.adj.size();
		for(int i=0; i<size; i++){
			Node v = n.adj.get(i);
			if(!v.visit){
				DFS_Visit(v);
			}
		}
		time++;
		n.timef = time;
		gt[n.value].timef = time;
	}
	
	
	private Node discover(){
		int max_timef = 0;
		Node last_node = new Node(0);
		
		for(int i=1; i<=nstudents; i++){
			if(!gt[i].visit){
				if(gt[i].timef > max_timef){
					max_timef = gt[i].timef;
					last_node = gt[i];
				}
			}
		}
		return last_node;
	}
	
	
	private boolean nodeVisited(){
		for(int i=1; i<=nstudents; i++){
			if(!gt[i].visit)
				return false;
		}
		return true;
	}
	
	
	private int countNodes(Node n){
		int count = 1;
		n.visit = true;
		
		int size = n.adj.size();
		for(int i=0; i<size; i++){
			Node tmp = n.adj.get(i);
			if(!tmp.visit){
				count += countNodes(tmp);
			}
		}
		return count;
	}
	
	
	void DFS_T(){
		while(!nodeVisited()){
			Node v = discover();
			int nelem = countNodes(v);
			group.addLast(nelem);
			v.timef = 0;
		}
	}
	
	
	void output(){
		int ngroup = 0;
		int nout = 0;
		
		int size = group.size();
		for(int i=0; i<size; i++){
			int x = group.get(i);
			if(x >= 4)
				ngroup++;
			else
				nout += x;
		}
		System.out.printf("%d %d\n", ngroup, nout);
	}
}




public class Sociologia {
	public static void main(String args[]){
		Scanner stdin = new Scanner(System.in);
		
		
		int nscenarios = stdin.nextInt();
		
		for(int i=1; i<=nscenarios; i++){
			
			int nstudents = stdin.nextInt();
			
			Graph G = new Graph(nstudents);
			G.initialize();
			for(int j=1; j<=nstudents; j++){
				int student = stdin.nextInt();
				int nfriends = stdin.nextInt();
				for(int k=0; k<nfriends; k++){
					int friend = stdin.nextInt();
					G.createGraph(student, friend);
				}
			}
			
			G.DFS();
			G.DFS_T();
			
			System.out.printf("Caso #%d\n", i);
			G.output();
		}
	}
}