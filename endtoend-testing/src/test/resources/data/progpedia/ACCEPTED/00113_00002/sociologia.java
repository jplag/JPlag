import java.util.*;

class No{
	int no;
	int tempof;
	LinkedList<No> adj;
	boolean visitado;
	
	No(int n){
		no=n;
		tempof=0;
		adj= new LinkedList<No>();
		visitado=false;
	}
	
	void addLigacao(No x){
		adj.addLast(x);
	}
}

class Grafos{
	int tempo;
	int npessoas;
	No g[];
	No gt[];
	LinkedList<Integer> grupos;
	
	Grafos(int np){
		tempo=0;
		npessoas=np;
		g=new No[np+1];
		gt=new No[np+1];
		grupos=new LinkedList<Integer>();
	}
	
	void inicializar(){
		for(int i=1;i<=npessoas;i++){
			g[i]=new No(i);
			gt[i]=new No(i);
		}
	}
	
	void criarGrafo(Scanner in){
		inicializar();
		for(int i=1;i<=npessoas;i++){
			int pessoa=in.nextInt();
			int namigos=in.nextInt();
			for(int j=0;j<namigos;j++){
				int amigo=in.nextInt();
				g[pessoa].addLigacao(g[amigo]);
				gt[amigo].addLigacao(gt[pessoa]);
			}
		}
	}
	
	void DFS(){
		for(int i=1;i<=npessoas;i++){
			if(!g[i].visitado)
				DFSVisit(g[i]);
		}
	}
	
	void DFSVisit(No x){
		tempo=tempo+1;
		x.visitado=true;
		for(No cursor:x.adj){
			if(!cursor.visitado)
				DFSVisit(cursor);
		}
		tempo=tempo+1;
		x.tempof=tempo;
		gt[x.no].tempof=tempo;
	}
	
	No findMax(){
		int maximo=0;
		No max=new No(0);
		for(int i=1;i<=npessoas;i++){
			if(!gt[i].visitado){
				if(gt[i].tempof>maximo){
					maximo=gt[i].tempof;
					max=gt[i];
				}
			}
		}
		return max;
	}
	
	boolean  todosVisitados(){
		for(int i=1;i<=npessoas;i++){
			if(!gt[i].visitado)
				return false;
		}
		return true;
	}
	
	void DFS_T(){
		while(!todosVisitados()){
			No TMax=findMax();
			int nelementos=NelementosGrupo(TMax);
			grupos.addLast(nelementos);
			TMax.tempof=0;
		}
	}
	
	int NelementosGrupo(No x){
		int contar=1;
		x.visitado=true;
		for(No cursor:x.adj){
			if(!cursor.visitado)
				contar=contar+NelementosGrupo(cursor);
		}
		return contar;
	}
	
	void output(){
		int ngrupos=0;
		int deFora=0;
		for(int x:grupos){
			if(x>=4)
				ngrupos++;
			else
				deFora=deFora+x;
		}
		System.out.printf("%d %d\n",ngrupos,deFora);
	}
}

class sociologia{
	public static void main(String[] args){
		Scanner in=new Scanner(System.in);
		int ncasos=in.nextInt();
		for(int i=1;i<=ncasos;i++){
			int np=in.nextInt();
			Grafos novo=new Grafos(np);
			novo.criarGrafo(in);
			novo.DFS();
			novo.DFS_T();
			System.out.printf("Caso #%d\n", i);
			novo.output();
		}
	}
}