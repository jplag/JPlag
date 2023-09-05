import java.util.*;


class GNode {
	int id, color, dist;
	GNode pai;
	ArrayList<Integer> adj;
	ArrayList<Integer> pre;

	GNode(int id) {
		this.id=id;
		color=0; //0=white 1=grey 2=black
		pai=null;
		adj=new ArrayList<Integer> (); //lista de adjacencias
		pre=new ArrayList<Integer> (); //lista de precedente aka transposta
	}
}
	
class Grafo { 
	GNode lista[];
	int n;
	boolean clean;
	Stack<GNode> ft; //reverse finishing order
	
	Grafo(int n){
		this.n=n;
		lista=new GNode[n];
		clean=true;
		ft=new Stack<GNode> ();
	}
	
	public void DFS() {
		if(!clean)
			ClearSec();
		
		for(GNode u:lista)
			if(u.color==0)
				DFS(u);
	}
	
	private void DFS(GNode u) {
		u.color=1;
		
		for(int i: u.adj) {
			GNode v=lista[i-1];
			if(v.color==0) {
				v.pai=u;
				DFS(v);
			}
		}
		
		u.color=2;
		ft.push(u);
	}

	private void ClearSec() {
		ft.clear();
		for(GNode i:lista) {
			i.color=0;
			i.pai=null;
		}
	}
	
	public void TransposeGrafo() {
//		for(GNode v: lista)
//			for (int u: v.adj) {
//				lista[u-1].pre.add(v.id);
//			}
		
		ArrayList<Integer> tmp;
		for(GNode v: lista) {
			tmp=v.adj;
			v.adj=v.pre;
			v.pre=tmp;
		}
		ClearSec();
	}
	
	@SuppressWarnings("unchecked")
	public void ComponentesConexos() {
		Stack<GNode> s;
		int grupo, naogrupo, tamanho;
		
		DFS();
		s=(Stack<GNode>) ft.clone();
		TransposeGrafo();
		
		grupo=naogrupo=tamanho=0;
		while (!s.isEmpty()) {
			GNode v=s.pop();
			if (v.color==0) {
				DFS(v);
				tamanho=ft.size();
				if(tamanho>3)
					grupo++;
				else
					naogrupo+=tamanho;
				ft.clear();
			}
		}
		
		System.out.println(grupo+" "+naogrupo);
	}
}

public class Sociologia {

   
    public static void main(String[] args) {
        
        Scanner in=new Scanner(System.in);
		
		int nc=in.nextInt(); //num de casos
		
		for(int i=1; i<=nc; i++) {
			int na=in.nextInt(); //num de alunos
			Grafo grafo=new Grafo(na);
			
			for(int j=0; j<na; j++) //Inicializar
				grafo.lista[j]=new GNode(j+1);
			
			for(int j=0; j<na; j++) {
				int x=in.nextInt(); //id do aluno
				//grafo.lista[x-1]=new GNode(x);
				int nf=in.nextInt(); //qnts alunos vai contar
				
				for(int k=0; k<nf; k++){
					int amigo=in.nextInt();
					grafo.lista[x-1].adj.add( amigo );
					grafo.lista[amigo-1].pre.add(x);
				}
			}
			System.out.println("Caso #"+i);
			grafo.ComponentesConexos();
		}
        
        
    }
    
}
