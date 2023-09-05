
import java.applet.*;
import java.awt.*;
import java.beans.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.net.*;
import java.nio.*;
import java.rmi.*;
import java.security.*;
import java.sql.*;
import java.text.*;
import java.util.*;



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


class SCC{
 static boolean[] visitado;
 static Stack<Integer> pilha = new Stack<Integer>();
 int nvs;
 static Grafo grafo;
 static Grafo transposto;
 static int maxgrupos=0;
 static int sozinhos=0;
 int tam_SCC=0;
SCC(int num_vertices, Grafo g, Grafo t){
            grafo=g;
            transposto=t;
            nvs=num_vertices;
            visitado=new boolean[nvs+1];
            maxgrupos=sozinhos=0;      
                }
void fill(boolean[] visitado){
        
            for (int i = 1; i <= nvs; i++) {
                    visitado[i]=false;
    }

                }
void processa(){

    for (int i = 1; i <=nvs; i++)  if(!visitado[i]) DFS(i);
    this.fill(visitado);
    while(!pilha.empty()){
        tam_SCC=0;    
        DFS1(pilha.pop());
        if(tam_SCC>=4)maxgrupos++;
        else sozinhos+=tam_SCC;
    
            }
    System.out.println(maxgrupos +" "+sozinhos);
    
}
void DFS(int vert){
        visitado[vert]=true;
      for (Arco e : grafo.verts[vert].adjs) {
            if(!visitado[e.no_final])DFS(e.no_final);
          
          
    }
      pilha.push(vert);
        }
void DFS1(int vert){
        visitado[vert]=true;
        tam_SCC ++;
        if(pilha.contains(vert))pilha.remove(pilha.indexOf(vert));
        for (Arco e : transposto.verts[vert].adjs) {
        if(!visitado[e.no_final])DFS1(e.no_final);
    }
 

}

}

public class JavaApplication7 {

  
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int cenarios=in.nextInt();
        for (int i = 1 ; i <= cenarios; i++) {
            System.out.println("Caso #"+i);
            int nalunos=in.nextInt();
            Grafo grafo= new Grafo(nalunos);
            Grafo trans= new Grafo(nalunos);
            for (int j = 1; j <= nalunos; j++) {
                int aluno=in.nextInt();
                int namigos=in.nextInt();
                if(namigos!=0)
                    for (int k = 0; k < namigos; k++) {
                           int curr=in.nextInt();
                          
                           grafo.insert_new_arc(aluno, curr, 0);
                           trans.insert_new_arc(curr, aluno, 0);
                    }
            }
                SCC novo = new SCC(grafo.num_vertices(), grafo,trans);
                novo.processa();
           
        }
        
        
        // TODO code application logic here
    }


    
    

}
