
import java.util.LinkedList;
import java.util.Scanner;

class No{
	int no;
	int tempof;
	LinkedList<No> adj;
	boolean visitado;
	
	No(int n){
		no = n;
		tempof = 0;
		adj = new LinkedList<No>();
		visitado = false;
	}
	void addLigacao(No x){
		adj.addLast(x);
	}
}

class Graph{
	int tempo, npessoas;
	No g[];
	No gt[];
	LinkedList<Integer> grupos;
	
	Graph(int np){
		this.npessoas = np;
		tempo = 0;
		g = new No[np+1];
		gt = new No[np+1];
		grupos = new LinkedList<Integer>();
	}
	private void inicializar(){
		for(int i = 1; i<=npessoas; i++){
			g[i] = new No(i);
			gt[i] = new No(i);
		}
	}
	void createGrafo(Scanner in){
		inicializar();
		for(int i = 1; i<=npessoas; i++){
			int pessoa = in.nextInt();
			int namigo = in.nextInt();
			for(int j = 0; j<namigo; j++){
				int amigo = in.nextInt();
				g[pessoa].addLigacao(g[amigo]);
				gt[amigo].addLigacao(gt[pessoa]);
			}
		}
	}
	
	void DFS(){
		for(int i = 1; i<=npessoas; i++){
			if(!g[i].visitado)
				DFSVisit(g[i]);
		}
	}
	private void DFSVisit(No x){
		tempo++;
		x.visitado = true;
		for(No cursor: x.adj){
			if(!cursor.visitado)
				DFSVisit(cursor);
		}
		tempo++;
		x.tempof =tempo;
		gt[x.no].tempof = tempo;
	}
	private No findMax(){
		int maximo = 0; 
		No max = new No(0);
		
		for(int i = 1; i<=npessoas; i++){
			if(!gt[i].visitado){
				if(gt[i].tempof > maximo){
					maximo = gt[i].tempof;
					max = gt[i];
				}
			}
		}
		return max;
	}
	private boolean allVisited(){
		for(int i = 1; i<=npessoas; i++){
			if(!gt[i].visitado)
				return false;
		}
		return true;
	}
	
	void DFS_T(){
		while(!allVisited()){
			No TMax = findMax();
			int nelementos = NelementosGrupo(TMax);
			grupos.addLast(nelementos);
			TMax.tempof = 0;
		}
	}
	private int NelementosGrupo(No x){
		int count = 1;
		x.visitado = true;
		for(No cursor: x.adj){
			if(!cursor.visitado)
				count +=NelementosGrupo(cursor);
		}
		return count;
	}
	
	void Output(){
		int ngrupos = 0;
		int deFora = 0;
		for(int x: grupos){
			if(x>=4){ngrupos++;}
			else {deFora+=x;}
		}
		System.out.printf("%d %d\n", ngrupos, deFora);
	}
}
public class Sociologia {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int ncasos = in.nextInt();
		for(int i = 1; i<=ncasos; i++){
			int np = in.nextInt();
			Graph novo = new Graph(np);
			novo.createGrafo(in);
			novo.DFS();
			novo.DFS_T();
			
			System.out.printf("Caso #%d\n", i);
			novo.Output();
		}

	}

}