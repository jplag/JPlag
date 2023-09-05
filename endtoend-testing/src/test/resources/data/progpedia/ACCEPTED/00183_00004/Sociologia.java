//package com.sociologia;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

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
    //int label;
    LinkedList<Arco> adjs;
    boolean visited;
    No() {
    	adjs = new LinkedList<Arco>();
    	visited=false;
    }
}


class Grafo {
    No verts[];
    int nvs, narcos, sol, fora;
    LinkedList<Integer> stack;
			
    public Grafo(int n) {
	nvs = n;
	narcos = 0;
	sol=0;
	fora=n;
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
    
	public void cleanVisited(){
		for(int i=1;i<=nvs;i++)
			verts[i].visited = false;
	}
	
	public void dfs(){
		cleanVisited();
		stack = new LinkedList<Integer>();
		for(int i=1;i<=nvs;i++)
			if(!verts[i].visited){
				verts[i].visited = true;
				dfs_pushstack(i);
			}
				
	}

	public void dfs_pushstack(int c){
		LinkedList<Arco> li = verts[c].adjs;
		while(!li.isEmpty()){
			int next = li.removeFirst().extremo_final();
			if(!verts[next].visited){
				verts[next].visited = true;
				dfs_pushstack(next);
			}
				
		}
		stack.addFirst(c);
		//System.out.println("add: " + c);
	}

	public void dfs_popstack(LinkedList<Integer> stack_){
		int c;
		stack = stack_;
		LinkedList<Integer> temp;
		
		while(!stack.isEmpty()){
			c = stack.removeFirst();

			if(!verts[c].visited){
				verts[c].visited = true;
				temp = new LinkedList<Integer>();
				temp.add(c);
				temp = dfs_scc(c,temp);
				
				if (temp.size() > 3){
					sol++;
					fora-= temp.size();
				}
					
			}
			
				
		}
	}

	public LinkedList<Integer> dfs_scc(int c, LinkedList<Integer> temp){
		Iterator<Arco> li = verts[c].adjs.iterator();

		while(li.hasNext()){
			int next = li.next().extremo_final();
			
			if(!verts[next].visited){
				verts[next].visited = true;	
				temp.add(next);
				dfs_scc(next, temp);				
			}	
		}
		return temp;
	}
    
    public LinkedList<Arco> adjs_no(int i) {
    	return verts[i].adjs;
    }
    
    public void insert_new_arc(int i, int j, int valor_ij){
	verts[i].adjs.add(new Arco(j,valor_ij));
	//System.out.println("de " +i + "para " + j);
        narcos++;
    }

    public Arco find_arc(int i, int j){
	for (Arco adj: adjs_no(i))
	    if (adj.extremo_final() == j) return adj;
	return null;
    }
}

public class Sociologia {
	
	static Grafo[] LerGrafo(Scanner in){
		
		Grafo[] rede = new Grafo[2];
		int Nalunos, aluno, Namigos, amigo ;
				
		Nalunos = in.nextInt();
		rede[0] = new Grafo(Nalunos);
		rede[1] = new Grafo(Nalunos);
		
		for(int j=0; j<Nalunos; j++){
			aluno = in.nextInt();
			Namigos = in.nextInt();
			
			for (int k = 0; k < Namigos; k++){
				amigo = in.nextInt();
						
				rede[0].insert_new_arc(aluno, amigo, 0);
				rede[1].insert_new_arc(amigo, aluno, 0);
			}
		}
		return rede;
	}
	
	


	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		int Ncenarios; 
		Grafo rede[];
		
		Ncenarios = in.nextInt();
		
		for(int i = 0; i< Ncenarios; i++){
			rede = LerGrafo(in);
			rede[0].dfs();
			rede[1].dfs_popstack(rede[0].stack);
			System.out.println("Caso #" + (i+1) + "\n" + rede[1].sol + " " + rede[1].fora);
			
				
		}
		
	}
	
	

}
