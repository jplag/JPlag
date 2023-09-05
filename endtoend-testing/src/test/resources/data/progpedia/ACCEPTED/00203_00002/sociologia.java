import java.util.*;

class Arco {
    int no_final;

    Arco(int fim){
	no_final = fim;
    }

    int extremo_final() {
	return no_final;
    }
}

class No {
    
    LinkedList<Arco> adjs;
    boolean visitado;

    No() {
	adjs = new LinkedList<Arco>();
	visitado=false;
    }
}

class Grafo {
    No verts[];
    int nverts, narcos;
			
    public Grafo(int n) {
	nverts = n;
	narcos = 0;
	verts  = new No[n+1];
	for (int i = 0 ; i <= n ; i++)
	    verts[i] = new No();
        // para vertices numerados de 1 a n (posicao 0 nao vai ser usada)
    }
    
    public int num_vertices(){
	return nverts;
    }

    public int num_arcos(){
	return narcos;
    }

    public LinkedList<Arco> adjs_no(int i) {
	return verts[i].adjs;
    }
    
    public void insert_new_arc(int i, int j){
	verts[i].adjs.addFirst(new Arco(j));
        narcos++;
    }

    public Arco find_arc(int i, int j){
	for (Arco adj: adjs_no(i))
	    if (adj.extremo_final() == j) return adj;
	return null;
    }
}

class sociologia {
	static int[] resp=new int[2];

	public static void main(String[] args) {
	    Grafo gr;
		Scanner in=new Scanner(System.in);
		int cenarios=in.nextInt();
			
		for(int i=1;i<=cenarios;i++) {
		    gr=criargrafo(in);
		    tratargrafo(gr);
		    escreverResp(i);
	}
	
}
    static void inicializa(int i){
		resp[0]=0; // num de elementos fora dos grupos
		resp[1]=0; // num de grupos com + de 4 elementos
    }

	static Grafo criargrafo(Scanner inp){
		int nalunos=inp.nextInt();
		 inicializa(nalunos);
		Grafo g=new Grafo(nalunos);
		//System.out.println("-------arvore com "+nalunos+"alunos--------");
		for(int i=0;i<nalunos;i++){
			int vertice=inp.nextInt();
			//System.out.print("lista de adjacentes de "+vertice+": ");
			int namigos=inp.nextInt();
			for(int j=0;j<namigos;j++){
				int aponta=inp.nextInt();
				g.insert_new_arc(vertice,aponta);
			//	System.out.print(aponta+" ");
			}
		}
		return g;
		       
	}
	static void tratargrafo(Grafo g) {
		Grafo gt=ConstroiTransposto(g); // algoritmo de kosaraju-sharir
		g=GrafoNvisitado(g);
	    Stack<Integer> pilha=new Stack<Integer>();
	    for(int i=1;i<=g.nverts;i++)
			if(!g.verts[i].visitado)
				DFS_Visit(g,i,pilha);
		gt=GrafoNvisitado(gt);
		//resp[0]=0;
		while(!pilha.isEmpty()){
			int vert=pilha.pop();
			if(!gt.verts[vert].visitado){
					int aux=ContaElGrupos(gt,vert);
					if(aux>=4)
						resp[1]++;
					else
						resp[0]+=aux;
			}
		}
	}		
	static void DFS_Visit(Grafo gr,int vert,Stack<Integer> s) {
		gr.verts[vert].visitado=true;
		for(Arco a: gr.verts[vert].adjs){
			int w=a.no_final;
			if(!gr.verts[w].visitado)
				DFS_Visit(gr,w,s);
			}
		s.push(vert); 
	}
	static int ContaElGrupos(Grafo gr,int vert) {
		int contador=1; // cada grupo tem no minimo um elemento: o proprio 
		gr.verts[vert].visitado=true;
		for(Arco a: gr.verts[vert].adjs){
			int w=a.no_final;
			if(!gr.verts[w].visitado)
				contador+=ContaElGrupos(gr,w);
			}
			return contador;
		}
				
		
	
	static Grafo GrafoNvisitado (Grafo gr) {
		for(int i=1;i<=gr.nverts;i++)
		  gr.verts[i].visitado=false;
		return gr;
	}
	static Grafo ConstroiTransposto(Grafo gr) {
		Grafo g=new Grafo(gr.nverts);
		for(int i=1;i<=gr.nverts;i++){
			for(Arco a: gr.verts[i].adjs)
				g.insert_new_arc(a.no_final,i);
			}
			return g;
		}
				
		
	static void escreverResp(int i) {
		System.out.println("Caso #"+i+"\n"+resp[1]+" "+resp[0]);
		}
		
	}
		
		
