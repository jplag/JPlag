import java.util.*;

class Aluno {
    int cod;
    int place;
    int place2;
    boolean estado;

    Aluno (int c) {
	cod = c;
	place = 0;
	place2 = 0;
	estado = false;
    }
}

class socio {

    static int lugar (Aluno gr[], int val, int t) {
	for (int i=1; i<=t; i++) if (gr[i].place==val) return i;
	return -1;
    }

    static int dfs (Aluno gr[], int k, int pl, boolean m [][], int t) {
	gr[k].estado=true;
	for (int i=1; i<=t; i++) if (!gr[i].estado && m[k][i]) pl=dfs(gr,i,pl,m,t);
	gr[k].place=pl;
	pl++;
	return pl;   
    }

    static int dfsv (Aluno gr[], int k, int pl, boolean m [][], int t) {
	gr[k].estado=false;
	for (int i=1; i<=t; i++) if (gr[i].estado && m[i][k]) pl=dfsv(gr,i,pl,m,t);		
	gr[k].place2=pl;
	pl++;
	return pl;   
    }

    public static void main (String args[]) {
	Scanner kb = new Scanner (System.in);
	int cenar = kb.nextInt();	
	for (int i=1; i<=cenar; i++) {	    
	    int grupos = 0;
	    int solos = 0;
	    int alunos = kb.nextInt();
	    Aluno lista [] = new Aluno [alunos+1];
	    boolean matr[][] = new boolean [alunos+1][alunos+1];
	    for (int j=1; j<= alunos; j++) {
		int cod = kb.nextInt();
		lista[j] = new Aluno(j);
		int amigos = kb.nextInt();
		for (int z=0; z<amigos; z++) {
		    int am = kb.nextInt();
		    matr[cod][am]=true;
		}
	    }	
	    int lug = 1;
	    for (int j=1; j<=alunos; j++) {
		if (!lista[j].estado) {
		    //System.out.println("Visitar "+j);
		    lug = dfs(lista,j,lug,matr,alunos);
		}
	    }	   
	    lug = 1;
	    int p = 1;
	    for (int j=alunos; j>0; j--) {
		int k = lugar (lista, j, alunos);		    
		if (lista[k].estado) {
		    //System.out.println("Visitar "+k);
		    lug = dfsv(lista,k,lug,matr,alunos);
		    //System.out.println("tam "+(lug-p));
		    if ((lug-p)>3) grupos++;
		    else solos = solos+lug-p;
		    p=lug;
		}
	    }	    	   
	    System.out.println("Caso #"+i);	    
	    System.out.println(grupos+" "+solos);
	}
    }
}

/*
4
4
1 3 2 4 3
4 0
2 2 1 3
3 2 2 1
6
1 2 3 5
2 2 3 4
4 1 2
3 2 2 1
6 1 5
5 2 6 1
8
1 4 6 2 4 5
3 1 2
2 2 3 4
4 1 5
6 0
5 3 4 8 7
7 1 5
8 2 5 3
10
1 4 6 2 4 5
3 2 2 1
9 0
2 2 3 4
4 2 5 9
6 1 1
5 3 4 8 7
7 1 5
8 1 5
10 1 9

*/