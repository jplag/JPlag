import java.util.*;

class NGrupos {
    int apartir4; //numero de grupos com 4 ou mais elementos
    int npmenor4; //numero de pessoas que pertencem a grupos com menos de 4 elementos

    NGrupos(){
	apartir4=0;
	npmenor4=0;
    }
}

class VerticesAdj {

    int vertice;
    VerticesAdj prox;

    VerticesAdj(int v,VerticesAdj p){
	vertice=v;
	prox=p;
    }
}

class Grafo {

    VerticesAdj g[]; //grafo
    VerticesAdj gT[]; //grafo transposto
    int nverts;
    NGrupos ngrupos; //indica quantos grupos existem para >=4 elementos e para <4 elementos

    private boolean vvisitado[]; //vector auxiliar para as pesquisas em profundidade
    private Stack<Integer>decresT_final; //pilha que tem os vertices por ordem decrescente de t_final. No topo da pilha fica o vertice que tem maior t_final 
    private int n; //variavel auxiliar para determinar quantos elementos tem cada grupo

    Grafo(int nv){
	g=new VerticesAdj[nv+1];
	gT=new VerticesAdj[nv+1];
	for(int i=1;i<=nv;i++){
	    g[i]=null;
	    gT[i]=null;
	}

	nverts=nv;

	ngrupos=new NGrupos();

	vvisitado=new boolean[nv+1];
	for(int i=1;i<=nv;i++)
	    vvisitado[i]=false;
	decresT_final=new Stack<Integer>();
	n=0;
    }

    void inserirLigacao(int de, int para){
	g[de]=new VerticesAdj(para,g[de]);
	gT[para]=new VerticesAdj(de,gT[para]);
    }

    NGrupos numeroGrupos(){
	DFS_G();
	DFS_GT_tfinal();

	return ngrupos;
    }

    void DFS_G(){
	for(int i=1;i<=nverts;i++)
	    if(!vvisitado[i])
		DFS_Gvisit(i);
    }

   private void DFS_Gvisit(int v){
	vvisitado[v]=true;

	for(VerticesAdj p=g[v];p!=null;p=p.prox)
	    if(!vvisitado[p.vertice])
		DFS_Gvisit(p.vertice);

	decresT_final.push(v);
    }

    void DFS_GT_tfinal(){ //visita em profundidade o grafo transposto, mas comecando a visitar pelos vertices de t_fianl maior
	for(int i=1;i<=nverts;i++)
	    vvisitado[i]=false;

	while(!decresT_final.empty()){
	    int v=decresT_final.pop();
	    if(!vvisitado[v]){
		DFS_GTvisit(v);
		if(n<4)
		    ngrupos.npmenor4+=n;
		else
		    ngrupos.apartir4++;
		n=0;
	    }
	}
    }

    private void DFS_GTvisit(int v){
	vvisitado[v]=true;

	for(VerticesAdj p=gT[v];p!=null;p=p.prox)
	    if(!vvisitado[p.vertice])
		DFS_GTvisit(p.vertice);

	n++;
    }
}

class ProblemaF4 {

    public static void main(String []args){
	Scanner sc=new Scanner(System.in);
	int ncasos;

	ncasos=sc.nextInt();
	for(int i=1;i<=ncasos;i++){
	    int nalunos=sc.nextInt();
	    Grafo grafo=new Grafo(nalunos);
	    while(nalunos-->0){
		int aluno=sc.nextInt();
		int namigos=sc.nextInt();
		while(namigos-->0){
		    int amigo=sc.nextInt();
		    grafo.inserirLigacao(aluno,amigo);
		}
	    }
	    System.out.println("Caso #"+i);
	    NGrupos ngrupos=grafo.numeroGrupos();
	    System.out.println(ngrupos.apartir4+" "+ngrupos.npmenor4);
	}
    }
}

