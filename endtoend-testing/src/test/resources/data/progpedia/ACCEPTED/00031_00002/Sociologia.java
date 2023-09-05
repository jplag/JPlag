import java.io.*;
import java.util.*;

class Sociologia{
	
	public static int G[][];
	public static int cont[];
	public static Stack<Integer> stk;
	public static boolean color[];
	
	public static int Gt[][];
	public static int cont_gt[];
	
	public static void main(String args[]){
		Scanner scn = new Scanner(System.in);
		
		int nr_casos = scn.nextInt();
		for (int nc=1; nc<=nr_casos; nc++){
			
			int nr_alunos = scn.nextInt();
			G = new int[nr_alunos+1][];
			cont = new int[nr_alunos+1];
			
			for (int na=0; na<nr_alunos; na++){
				
				int id = scn.nextInt();
				int nr_f = scn.nextInt();
				G[id] = new int[nr_f];
				
				for (int nf=0; nf<nr_f; nf++)
					G[id][cont[id]++] = scn.nextInt();
			}
			
			stk = new Stack<Integer>();
			color = new boolean[nr_alunos + 1];
			
			for (int i=1; i<=nr_alunos; i++)
				if (!color[i])
					DFSpile(i);
			
			// criar GT
			Gt = new int[nr_alunos+1][];
			cont_gt = new int[nr_alunos+1];
			
			for (int i=1; i<=nr_alunos; i++)
				Gt[i] = new int [nr_alunos-1];
			
			for (int na=1; na<=nr_alunos; na++){
				for (int i=0; i<cont[na]; i++){
					int x = G[na][i];
					Gt[x][cont_gt[x]++] = na;
				}	                        
			}
			
			Arrays.fill(color, false);
			int n = 0; //nr de componentes
			int r = 0; //nr de alunos
			
			while (!stk.isEmpty()){
				int a = stk.pop();
				
				if (!color[a]){
					int x = DFScont(a);
					if (x >= 4) n++;
					else r += x;
				}
			}
			
			System.out.println("Caso #" + nc);
			System.out.println(n + " " + r);
		}
	}
	
	public static void DFSpile(int root){
		color[root] = true;
		
		for (int i=0; i<cont[root]; i++){
			int x = G[root][i];
			if (!color[x])
				DFSpile(x);
		}
		stk.push(root);
	}
	
	public static int DFScont(int root){
		color[root] = true;
		int t = 1;
		for (int i=0; i<cont_gt[root]; i++){
			int x = Gt[root][i];
			if (!color[x])
				t += DFScont(x);
		}
		return t;
	}
}