import java.util.*;
//import java.io.*;

class EmptyQueueException extends RuntimeException {  
    public EmptyQueueException(String err) {
	super(err);
    }
}
class Node<E> {
    E       val;
    Node<E> next;
    
    Node(E v, Node<E> n) {
	val= v;
	next= n;
    }
}
class Queue<E>  {
    private int size;      
    private Node<E> first; 
    private Node<E> last;  

    Queue()  {              
	size= 0;
	first= last= null;
    }
    public boolean isEmpty()  { return (size==0); }
    public int size() { return size; }

  
    public E dequeue() throws EmptyQueueException { 

	if (isEmpty())
	    return null;

	E res = first.val;	
	first= first.next;
	size--;
	if (first==null)
	    last=null;
	return res;
    }
	    
    
    
    public void enqueue(E v) {
	Node<E>  novo = new Node<E> (v, null);
    
	if (isEmpty())
	    first=last=novo;
	else {
	    last.next=null;
	    last.next= novo;
	    last = novo;
	}
	size++;
    }
}

class nos {

    int o[][];
    int p;
    int cnt;
    int time;
	
    nos(int ola[][],int xl){
	p = xl;
	o = ola;
	cnt = 0;
	time = 0;
    }
    
    


    void scc () {
	
	int mt[][] = new int[p][p];
	
	int fs[] = dfs(o,p);
	mt = gl(o,p);
	dfsl(mt,p,fs);
    }

    void dfs_visitl (int o[][],String color[],int pais[],int h,int p) {
	
	cnt++;

      	color[h] = "gray";

	for (int a=0;a<p;a++) {

	    if (o[h][a] == 1){
		if (color[a] == "white"){
		    pais[a] = h;
		    dfs_visitl(o,color,pais,a,p);
		}
	    }
	}
	color[h] = "black";
		
    }


    void dfsl (int oo[][],int p,int f[]) {


	String color [] = new String [p];
	int pais [] = new int [p];
	
	for (int i= 0; i<p; i++) {	
	    color[i] = "white";
	}
	
	for (int i= 0; i<p; i++) {	
	    pais[i] = 0;
	}
	
		
	Queue<Integer> filas = new Queue<Integer>();

	int u = -1;
	int z = 1;
	int uz = 1;

	while (uz != 0){
	    z = max(p,f);

	    if (u != z) {
		filas.enqueue(z);
		f[z]=0;
		u = z;
	    }
	    else {uz = 0;}
	}
	int cntg = 0;
	int cntf = 0;

	while (! filas.isEmpty()) {
	    
	    int q = filas.dequeue();

	  
	    if (color[q] == "white"){
    
		dfs_visitl(oo,color,pais,q,p);
		if (cnt > 3) {
		    cntg++;
		}
		else {cntf += cnt;}
		cnt = 0;
	    }
	}

	System.out.println(cntg + " " + cntf);
	
    }


    int max(int p,int f[]){

	int max =0;
	int ind =0;

	for (int b=0;b<p;b++) {

	    if (max < f[b]){
		max = f[b];
		ind = b;
	    }
	}
	
	return ind;
    }



    int[] dfs(int o[][],int p) {
	
	String color [] = new String [p];
	int pais [] = new int [p];
	int d[] = new int [p];
	int f[] = new int [p];

	for (int i= 0; i<p; i++) {	
	    color[i] = "white";
	}
	
	for (int i= 0; i<p; i++) {	
	    pais[i] = 0;
	    f[i]=0;
	    d[i]=0;
	}	
	
	for (int h=0;h<p;h++) {
	    if (color[h] == "white"){
		dfs_visit(o,color,pais,h,d,p,f);
	    }
	}
      	
	return f;
	
    }

    void dfs_visit (int o[][],String color[],int pais[],int h,int d[],int p,int f[]) {

	time++;
	d[h] = time;
	color[h] = "gray";

	for (int a=0;a<p;a++) {

	    if (o[h][a] == 1){
		if (color[a] == "white"){
		    pais[a] = h;
		    dfs_visit(o,color,pais,a,d,p,f);
		}
	    }
	}
	color[h] = "black";
	time++;
	f[h] = time;
    
    }


    int[][] gl (int matriz[][],int r) { 

	int matrizl [][] = new int [r][r];

	for (int ii=0;ii<r;ii++){
	    for (int jj=0;jj<r;jj++){
		if (matriz[ii][jj] == 1){
		    matrizl[jj][ii] = 1;
		}
	    }
	}

	return matrizl;
    }
}


class sociologia {

    public static void main(String args[]) {

	Scanner in = new Scanner(System.in);
	
	int x = in.nextInt();
	
	int y = 0;

	Queue<Integer> fila = new Queue<Integer>();

	for (int i=0;i<x;i++) {

	    y = in.nextInt();
	    

	    int matriz[][] = new int [y][y];
	    
	    for (int j=0;j<y;j++) {
		
		int r = in.nextInt();
		int m = in.nextInt();
		
		for (int k=0;k<m;k++){
		    
		    int n = in.nextInt();
		    
		    matriz[(r-1)][(n-1)] = 1;
		    
		}
	    }
	    int num = i+1;
	    System.out.println("Caso #" + num);
	    nos falar = new nos(matriz,y);
	    falar.scc();
	}
	
    }

}