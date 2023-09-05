import java.util.*;
import java.io.*;
import java.util.LinkedList;

class Arco {
    int no_final;
    int valor;
    
    Arco(int fim, int v){
	no_final = fim;
	valor = v;
    }

    int extremo_final() {
	return no_final;
    }

    int valor_arco() {
	return valor;
    }
}


class No {
	int dia;
    LinkedList<Arco> adjs;

    No() {
	adjs = new LinkedList<Arco>();
    }
}


class Grafo {
    No verts[];
    int nvs, narcos;
			
    public Grafo(int n) {
	nvs = n;
	narcos = 0;
	verts  = new No[n+1];
	for (int i = 0 ; i <= n ; i++)
	    verts[i] = new No();
        // para vertices numerados de 1 a n (posicao 0 nao vai ser usada)
    }
    
    public int num_vertices(){
	return nvs;
    }

    public int num_arcos(){
	return narcos;
    }

    public LinkedList<Arco> adjs_no(int i) {
	return verts[i].adjs;
    }
    
    public void insert_new_arc(int i, int j, int valor_ij){
	verts[i].adjs.addFirst(new Arco(j,valor_ij));
        narcos++;
    }

    public Arco find_arc(int i, int j){
	for (Arco adj: adjs_no(i))
	    if (adj.extremo_final() == j) return adj;
	return null;
    }
}

class Sociologia {
	public static void PP(int j) {
		if(!visitado[j]) {
			visitado[j] = true;
			for(Arco arco: grafo.verts[j].adjs) 
				PP(arco.no_final);
			pilha.addLast(j);
		}
	}
	public static void PP2(int j) {
		if(!visitado[j]) {
			visitado[j] = true;
			for(Arco arco: grafoTrans.verts[j].adjs) {
				PP2(arco.no_final);
			}
			n++;
		}
	}
	public static int n;
	public static boolean[] visitado;
	public static LinkedList<Integer> pilha = new LinkedList<Integer>();
	public static Grafo grafo;
	public static Grafo grafoTrans;
	public static void main (String args[]) {
		Scanner in = new Scanner(System.in);
		int nCenarios = in.nextInt();
		for(int i=0;i<nCenarios;i++) {
			int pf=0, g=0;
			int nAlunos = in.nextInt();
			visitado = new boolean[nAlunos+1];
			grafo = new Grafo(nAlunos);
			grafoTrans = new Grafo(nAlunos);
			for(int j=0;j<nAlunos;j++) {
				int idAluno = in.nextInt();
				int nAmigosAluno = in.nextInt();
				for(int k = 0;k<nAmigosAluno;k++) {
					int idAmigoAluno = in.nextInt();
					grafo.insert_new_arc(idAluno, idAmigoAluno, 0);
					grafoTrans.insert_new_arc(idAmigoAluno, idAluno, 0);
				}
			}
			Arrays.fill(visitado, false);
			for(int j=1;j<=nAlunos;j++) PP(j);
			Arrays.fill(visitado, false);
			while(pilha.size()!=0) {
				int j = pilha.removeLast();
				n = 0;
				if(!visitado[j]) PP2(j);
				if(n>3) g++; else if(n>0) pf += n;
			}
			System.out.println("Caso #" + (i+1));
			System.out.println(g +" "+ pf);
		}
	}
}
