import java.util.*;

class Pessoa{
	int id;
	int namigos;
	int amigos[];
	int t;
	boolean visitado;

	
	Pessoa(int i){
		id=i;
		visitado=false;
	}
	Pessoa(int i, int n){
		id=i;
		namigos=n;
		amigos=new int[namigos];
		t=0;
		visitado=false;
	}
	int pos(){
		int p=0;
		for(int i=0;i<amigos.length;i++)
			if(amigos[i]!=0)
				p++;
		return p;
	}

}

class Grafo{
	int nos;
	int tempo;
	Pessoa ppl[];
	Pessoa tppl[];
	int ngrupos;
	int nelementos;
	int res;
	int visitados;
	
	Grafo(int n){
		nos=n;
		tempo=0;
		ppl=new Pessoa[n+1];
		tppl=new Pessoa[n+1];
		ngrupos=0;
		nelementos=0;
		res=0;
		visitados=0;
	}
	
		
	
	
	void criar(Scanner kb){
		for(int i=1;i<=nos;i++){
			ppl[i]=new Pessoa(i);
			tppl[i]=new Pessoa(i,nos);
		}
		for(int i=0;i<nos;i++){
			int id=kb.nextInt();
			int nf=kb.nextInt();
			Pessoa nova=new Pessoa(id,nf);
			ppl[id]=nova;
			for(int j=0;j<nf;j++){
				int f=kb.nextInt();
				ppl[id].amigos[j]=f;
			}
		}
	}
	void transpor(){
		for(int i=1;i<=nos;i++){
			int aux,in;
			for(int j=0;j<ppl[i].amigos.length;j++){
				aux=ppl[i].amigos[j];
				in=tppl[aux].pos();
				tppl[aux].amigos[in]=i;
			}		
		}
	}
	void dfs(){
		for(int i=1;i<ppl.length;i++){
			if(ppl[i].visitado==false)
				dfs_visit(i);
		}
	}
	void dfs_visit(int x){
		if(ppl[x].visitado==false){
			ppl[x].visitado=true;
			for(int j=0;j<ppl[x].amigos.length;j++){
				dfs_visit(ppl[x].amigos[j]);
			}
			tempo++;
			ppl[x].t=tempo;
		}
	}
	
	int findMax(){
		int max=-1;
		int indice=-1;
		for(int i=1;i<=nos;i++)
			if(max<ppl[i].t && tppl[i].visitado==false){
				max=ppl[i].t;
				indice=i;
			}
		return indice;
	}
	
	void dfs_t_visit(int x){
		if(tppl[x].visitado==false){
			tppl[x].visitado=true;
			visitados++;
			for(int j=0;j<tppl[x].pos();j++){
				dfs_t_visit(tppl[x].amigos[j]);
			}
			nelementos++;
		}
	}
	void dfs_t(){
		while(visitados<nos){
			int id=findMax();
			if(tppl[id].visitado==false && id!=-1){
				nelementos=0;
				dfs_t_visit(id);
				if(nelementos>=4)
					ngrupos++;
				else
					res=res+nelementos;
			}
		}
		
	}
	
	void print(){
			System.out.println(ngrupos+" "+res);
	}
}

public class Sociologia {
	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);
		int ncasos=kb.nextInt();
		for(int i=1;i<=ncasos;i++){
			int n=kb.nextInt();
			Grafo g=new Grafo(n);
			g.criar(kb);
			g.dfs();
			g.transpor();
			g.dfs_t();
			System.out.println("Caso #"+i);
			g.print();
		}
	}
}
