//package com.sociologia;
import java.util.*;

class Aluno {
	int nome;
	LinkedList<Integer> amigos;
	int flag;

	Aluno(int nome, int n) {
		this.nome = nome;
		amigos = new LinkedList<Integer>();
		flag = 0;
	}
}

public class Sociologia {
	static Stack<Aluno> pilha = new Stack<Aluno>();
	static Aluno[] alunos;
	static Aluno[] alunosT;
	
	static int count = 0;
	static int ngrupos = 0;
	static int nfora = 0;
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int ncasos = in.nextInt();

		for (int j = 0; j < ncasos; j++) {
			int nalunos = in.nextInt();
			alunos = new Aluno[nalunos];
			alunosT = new Aluno[nalunos];
			for (int k = 0; k < nalunos; k++) {
				alunos[k] = new Aluno(k+1, nalunos);
				alunosT[k] = new Aluno(k+1, nalunos);
			}
			while (nalunos-- > 0) {
				int aluno = in.nextInt();
				int namigos = in.nextInt();
				for (int i = 0; i < namigos; i++){
					int x = in.nextInt();
					alunos[aluno-1].amigos.add(x);
					alunosT[x-1].amigos.add(aluno);
				}
			}
			DFS();
			DFST();
			System.out.println("Caso #" + (j+1));
			System.out.println(ngrupos + " " + nfora);
			nfora = 0;
			ngrupos = 0;
			pilha.clear();
		}
	}

	static void DFS () {
		for (Aluno a : alunos) {
			if (a.flag == 0)
				DFSVISIT (a);
		}
	}
	
	static void DFSVISIT (Aluno a) {
		a.flag = 1;
		for (int n : a.amigos) {
			if (n != 0 && alunos[n-1].flag == 0)
				DFSVISIT(alunos[n-1]);
		}
		pilha.push(alunosT[a.nome - 1]);
	}
	
	static void DFST () {
		while (!pilha.empty()) {
			Aluno a = pilha.pop();
			if (a.flag == 0) {
				DFSVISITT (a);
				if (count > 3)
					ngrupos++;
				else
					nfora += count;
				count = 0;
			}
		}
	}
	
	static void DFSVISITT (Aluno a) {
		a.flag = 1;
		for (int n : a.amigos)
			if (n != 0 && alunosT[n-1].flag == 0)
				DFSVISITT(alunosT[n-1]);
		count++;
	}
}