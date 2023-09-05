
import java.util.*;

class Sociologia {
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
		npessoas = np;
		tempo = 0;
		g = new No[np+1];
		gt = new No[np+1];
		grupos = new LinkedList<Integer>();
	}
	
	void createGrafo(Scanner in){
		for(int i = 1; i<=npessoas; i++){
			g[i] = new No(i);
			gt[i] = new No(i);
		}
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
	void DFSVisit(No x){
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
	No findMax(){
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
	boolean Visitados(){
		for(int i = 1; i<=npessoas; i++){
			if(!gt[i].visitado)
				return false;
		}
		return true;
	}
	
	void DFS_T(){
		while(!Visitados()){
			No TMax = findMax();
			int nelementos = NelementosGrupo(TMax);
			grupos.addLast(nelementos);
			TMax.tempof = 0;
		}
	}
	int NelementosGrupo(No x){
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


/*
class SocA {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
			int np = in.nextInt();
			GrafoA novo = new GrafoA(np);
			novo.criarGrafo(in);
			novo.DFS();
			novo.DFS_T();
			novo.amigo_um(np);
	}
}

void criarGrafo(Scanner in){
		for(int i = 1; i<=npessoas; i++){
			g[i] = new NoA(i);
			gt[i] = new NoA(i);
		}
		for(int i = 1; i<=npessoas; i++){
			int namigo = in.nextInt();
			for(int j = 0; j<namigo; j++){
				int amigo = in.nextInt();
				g[i].addLigacao(g[amigo]); 
				gt[amigo].addLigacao(gt[i]);
			}
		}
	}

void amigo_um(int np){
		int amigos_um = 0;
		for(int amigo = 1; amigo<=np; amigo++){
			if(g[amigo].adj.contains(g[1])){
				amigos_um++;
			}
		}
		System.out.println(amigos_um); 
}
*/




