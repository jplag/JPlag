import java.util.*;

class Social{
    
    public static Stack<Integer> s = new Stack<Integer>();
    public static int ctr = 0;
    public static int[] flood;

    public static void printGraph(ArrayList<ArrayList<Integer>> g){
	for(int i=1; i<g.size(); i++){
	    System.out.print(i+" -> ");
	    for(int j=0; j<g.get(i).size(); j++)
		System.out.print(g.get(i).get(j)+" ");
	    System.out.println();
	}
    }
    
    public static void dfs(ArrayList<ArrayList<Integer>> g){
	int v[] = new int[g.size()];
	for(int i=1; i<g.size(); i++)
	    if(v[i]==0)
		runDFS(g,i,v);
    }
    
    public static void runDFS(ArrayList<ArrayList<Integer>> g, int n, int[] v){
	v[n]=1;
	for(int i=0; i<g.get(n).size(); i++)
	    if(v[g.get(n).get(i)]==0)
		runDFS(g,g.get(n).get(i),v);
	if(!s.contains(n))
	    s.push(n);
    }
    
    public static ArrayList<ArrayList<Integer>> transpose(ArrayList<ArrayList<Integer>> g){
	ArrayList<ArrayList<Integer>> s = new ArrayList<ArrayList<Integer>>();
	for(int i=0; i<g.size(); i++)
	    s.add(new ArrayList<Integer>());
	
	for(int i=0; i<g.size(); i++)
	    for(int j=0; j<g.get(i).size(); j++)
		s.get(g.get(i).get(j)).add(i);
	return s;
    }
    
    public static void solve(ArrayList<ArrayList<Integer>> g){
	flood = new int[g.size()];
	while(!s.isEmpty()){
	    int n = s.pop();
	    if(flood[n]==0){
		ctr++;	
		solve_2(g,n,flood);
	    }
	}
    }
    
    public static void solve_2(ArrayList<ArrayList<Integer>> g, int n, int[] flood){
	flood[n]=ctr;
	for(int i=0; i<g.get(n).size(); i++)
	    if(flood[g.get(n).get(i)]==0)
		solve_2(g,g.get(n).get(i),flood);
    }
    
    public static void main(String[] args){
	
	Scanner input = new Scanner(System.in);
	
	int nrcases = input.nextInt();
	for(int k=0; k<nrcases; k++){
	    System.out.println("Caso #"+(k+1));
	    ArrayList<ArrayList<Integer>> g = new ArrayList<ArrayList<Integer>>();
	    s = new Stack<Integer>();
	    ctr = 0;
	    
	    int nrnodes = input.nextInt();
	    for(int i=0; i<nrnodes+1; i++)
		g.add(new ArrayList<Integer>());
	    
	    for(int i=0; i<nrnodes; i++){
		int node  = input.nextInt();
		int nrcons = input.nextInt();
		for(int j=0; j<nrcons; j++)
		    g.get(node).add(input.nextInt());
	    }
	    //printGraph(g);
	    dfs(g);
	    g=transpose(g);
	    solve(g);
	    
	    Arrays.sort(flood);
	    int groups = 0;
	    int pplgro = 0;

	    for(int i=1; i<flood.length; i++){
		int nrppl = 0;
		for(int j=0; j<flood.length; j++)
		    if(flood[j]==i)
			nrppl++;
		if(nrppl>=4){
		    groups++;
		    pplgro+=nrppl;
		}
	    }
	    System.out.println(groups+" "+(nrnodes-pplgro));
	}
    }
}