import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

// let antes de comecar a escrever...
// branco = 0 cinzento = 1 preto = 2 
//atencao ao n+1 do scanner

class Adjs{ // lista de ajacencias representa a lista dos filhos de um no
	LinkedList<Integer> adjs = new LinkedList<Integer>();
}

class Grafo{
	Adjs verts [];
	Grafo(int nv){
		verts= new Adjs[nv+1];
		for(int i = 0 ; i <= nv ; i++)
		{
			verts[i] = new Adjs();	
		}
	}	
}
public class sociologo {
	static int res1=0,res2=0;
	public static int DFSVISIT(Grafo a, int val,int instante,int cor[],int posnum[])
	{
		cor[val]=1;
		while(!a.verts[val].adjs.isEmpty())
		{
			int analisa = a.verts[val].adjs.removeFirst();
			if(cor[analisa]==0)
				instante = DFSVISIT(a, analisa, instante, cor,posnum);
		}
		instante++;
		posnum[val]=instante;
		return instante;
	}
	public static void DFS(Grafo a,int n, Stack<Integer> v,int posnum[])
	{
		int instante = 0;
		int prev[] = new int [n];
		for(int i = 1 ; i < n ; i++)
			prev[i]=Integer.MAX_VALUE;
		int cor[] = new int [n];
		while(!v.empty())
		{
			
			int val = v.pop();
			if( cor[val]==0)
				instante = DFSVISIT(a,val,instante,cor,posnum);
		}
		
	}
	public static int DFSnVISIT(Grafo a, int val,int instante,int cor[])
	{
		cor[val]=1;
		while(!a.verts[val].adjs.isEmpty())
		{
			int analisa = a.verts[val].adjs.removeFirst();
			if(cor[analisa]==0)
				instante = DFSnVISIT(a, analisa, instante, cor);
		}
		instante++;
		return instante;
	}
	public static void DFSN(Grafo a,int n, Stack<Integer> v)
	{
		int instante = 0;
		int prev[] = new int [n];
		for(int i = 1 ; i < n ; i++)
			prev[i]=Integer.MAX_VALUE;
		int cor[] = new int [n];
		while(!v.empty())
		{
			instante = 0;
			int val = v.pop();
			if( cor[val]==0)
				{
				
				instante = DFSnVISIT(a,val,instante,cor);
				//System.out.println(instante);
				if(instante >= 4)
					res1++;
				else
					res2+=instante;
				}
			
		}
		
	}
	
	public static void main(String args[])
	{
		Scanner io = new Scanner (System.in);
		int testes = io.nextInt();
		Stack<Integer> pilha = new Stack<Integer>();
		for(int i = 1 ; i <= testes ; i++)
		{
			int n = io.nextInt()+1;//ver isto nao precisa de nenhum n+1 xDDDDDDDDDDDDDDDDDDD
			Grafo a = new Grafo(n);
			Grafo b = new Grafo(n);
			//System.out.println("n " +n);
			for(int j = 1 ; j < n ; j++)
			{
				int liga = io.nextInt();
				
				pilha.add(liga);
				int nfilhos = io.nextInt();
				//System.out.println(nfilhos);
				for(int t = 0 ; t < nfilhos; t++)
				{
					int filho = io.nextInt();
					a.verts[liga].adjs.addFirst(filho);
					b.verts[filho].adjs.addFirst(liga);
				}
				
			}
			int posnum[] = new int [n];//isto tem que ser retornado :S
			//insercao xD
			DFS(a,n,pilha,posnum);
			for(int j = 1 ; j < n ; j++ )
			{	
				for(int t = 1 ; t < n ; t++ )
				{	
					if(posnum[t]==j)
						pilha.push(t);
				}
			}
			//System.out.println(pilha.pop());
			DFSN(b,n,pilha);
			
			System.out.println("Caso #"+i);
			System.out.println("" +res1 + " " + res2);
			res1=0;
			res2=0;
		}
	}
}
/*	for(int j = 1 ; j < n ; j ++ )
System.out.print(j);
System.out.println();
for(int j = 1 ; j < n ; j ++ )
System.out.print(posnum[j]);
System.out.println();
*/