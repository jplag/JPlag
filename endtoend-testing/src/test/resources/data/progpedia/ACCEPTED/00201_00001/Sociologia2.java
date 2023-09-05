import java.util.*;

class Graph {
    class Node {
	int index;              // mark node as visited by assigning it a visit number
	int lowlink;            // low-link index reaching the node (initially equal to index)
	LinkedList<Node> edges; // successors of current node
	
	Node() {
	    index= -1;
	    lowlink= -1;
	    edges= new LinkedList<Node>();
	}
    }
    Node graph[];      // graph is a vector of Nodes
    int numNodes;      // number of nodes
    int index;         // global counter for visited nodes
    int ctrScc;        // ctr of SCCs with size >= 4
    int ctrNos;        // ctr of nodes not included in SCCs sized >= 4
    LinkedList<Node> stack; // used to save nodes in a SCC and to verify that we have an SCC

    // Graph constructor -- n nodes 
    Graph(int n) {
	graph=     new Node[n];
	for (int i=0; i<n; i++) 
	    graph[i]= new Node();
	index=     0;
	ctrScc=    0;
	ctrNos=    0;
	stack=    new LinkedList<Node>();
	numNodes = n;
    }

    void addConnection(int a, int b) {
	// subtract 1 to index from 0 till numNodes-1
	graph[a-1].edges.addLast(graph[b-1]);
    }
    
    void tarjan() {
	// these were already initialized at graph creation
	//index= 0;
	//stack= new LinkedList<Integer>();
	//ctrScc= 0;
	//ctrNos= 0;

	for (Node v : graph) {
	    if (v.index==-1)
		dfs(v);
	}
    }

    void dfs(Node v) {
	int ctr;

	// mark v as visited by recording the depth index
	v.index=   index;
	v.lowlink= index;
	index++;
	stack.addFirst(v);
	
	// visit successors of v if not yet visited
	for (Node w : v.edges) {
	    if (w.index==-1) {
		dfs(w);
		v.lowlink= Math.min(v.lowlink, w.lowlink);
	    }
	    else if (stack.contains(w))  // if w is the stack, belongs to current SCC
		v.lowlink= Math.min(v.lowlink, w.index); // consider minimum depth index
	}
	if (v.lowlink==v.index) { // SCC found, v is its root
	    ctr= 1;               // to count for v
	    Node w;
	    //System.out.printf("SCC: %d ", v.index);
	    while ((w=stack.removeFirst())!=v) {
		ctr++;
		//	System.out.printf("%d ", w.index);
	    }
	    // update counters for Sociologia problem
	    if (ctr>=4) {
		ctrScc++;
		ctrNos += ctr;
	    }
	}
    }
    void printResult(int nc) {
	System.out.printf("Caso #%d\n",nc+1);
	System.out.printf("%d %d\n",ctrScc,numNodes-ctrNos);
    }
}


class Sociologia2 {
    public static void main(String args[]) {
	Scanner stdin= new Scanner(System.in);
	int c, ncases, n, connections;
	int a, b;
	Graph graph;

	ncases = stdin.nextInt();    // Number of cases
	for (c=0; c<ncases; c++) {
	    n= stdin.nextInt();      // Number of nodes
	    graph = new Graph(n);    // Create a new graph object 
	    for (int j= 0; j<n; j++) {
		a= stdin.nextInt();  // current node
		connections = stdin.nextInt(); // Number of links (successors)
		for (int i=0; i<connections; i++) {
		    b = stdin.nextInt();      // link or successor
		    graph.addConnection(a,b); // connect a to b (directed graph)
		}
	    }
	    // call Tarjan algorithm
	    graph.tarjan();
	    // print the result for case c
	    graph.printResult(c);
	}	
    }
}

