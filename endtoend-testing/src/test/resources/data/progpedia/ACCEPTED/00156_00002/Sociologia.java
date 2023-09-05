import java.util.Scanner;
import java.util.Stack;

class Svertice {
	int id;
	Svertice parent;
	String color;
	int startTime;
	int endTime;
	int[] friends;
	int numFriends;
	int[] friends_t;
	int numFriends_t;

	Svertice(int id, int size) {
		this.id = id;
		parent = null;
		color = "white";
		startTime = -1;
		endTime = -1;
		friends = new int[size];

	}

	void reset() {
		parent = null;
		color = "white";
		startTime = -1;
		endTime = -1;
	}

	void addFriend(int id) {
		friends[numFriends] = id;
		numFriends++;
	}

	void initiateFriends_t(int size) {
		friends_t = new int[size];
	}

	void addFriend_t(int id) {
		friends_t[numFriends_t] = id;
		numFriends_t++;
	}

}



public class Sociologia {
	static Svertice[] all_alunos;
	static int time;
	static Stack<Svertice> ordem_alunos;
	static int[] ocorrencias;
	static int ngrupos;
	static int npessoas;

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);

		int cases = in.nextInt();

		for(int cs = 0; cs < cases; cs++) {
			int nalunos = in.nextInt();  
			in.nextLine();
			all_alunos = new Svertice[nalunos];
			ocorrencias = new int[nalunos];
			for(int na = 0; na < nalunos; na++) {

				int aluno = in.nextInt();
				int nf = in.nextInt();
				all_alunos[aluno-1] = new Svertice(aluno, nf);

				for(int i = 0; i < nf; i++) {
					int temp = in.nextInt();
					all_alunos[aluno-1].addFriend(temp);
					ocorrencias[temp-1]++;
				}
			}
			DFS();
			calcularGrafoTransposto();
			DFS_T();

			System.out.println("Caso #" + (cs+1));
			System.out.println(ngrupos + " " + npessoas);

		}
		in.close();
	}

	static void DFS() {
		time = 0;
		ordem_alunos = new Stack<Svertice>();
		for(Svertice aluno: all_alunos) {
			if(aluno.color.equals("white")) {
				DFS_VISIT(aluno);
			}
		}
	}

	static void DFS_VISIT(Svertice aluno) {
		time++;
		aluno.startTime = time;
		aluno.color = "gray";
		for(int id_amigo: aluno.friends) {
			Svertice amigo = all_alunos[id_amigo-1];
			if(amigo.color.equals("white")) {
				amigo.parent = aluno;
				DFS_VISIT(amigo);
			}
		}
		time++;
		aluno.endTime = time;
		aluno.color = "black";
		ordem_alunos.push(aluno);

	}

	static void calcularGrafoTransposto() {
		for(Svertice aluno: all_alunos) {
			aluno.initiateFriends_t(ocorrencias[aluno.id-1]);
		}
		for(Svertice aluno: all_alunos) {
			for(int amigo_id: aluno.friends) {
				all_alunos[amigo_id-1].addFriend_t(aluno.id);
			}
			aluno.reset();
		}
	}

	static void DFS_T() {
		//Numero de grupos com componente fortemente conexa e grau >= 4
		ngrupos = 0;
		//Numero de pessoas que nao ficaram em grupos com os requisitos necessarios
		npessoas = 0;

		while(ordem_alunos.isEmpty() == false){
			time = 0;
			Svertice aluno = ordem_alunos.pop();
			if(aluno.color.equals("white")) {
				DFS_T_VISIT(aluno);
				if((time/2) >= 4) {
					ngrupos++;
				}
				else {
					npessoas += time/2;
				}
			}
		}
	}

	static void DFS_T_VISIT(Svertice aluno) {
		time++;
		aluno.startTime = time;
		aluno.color = "gray";
		for(int amigo_id: aluno.friends_t) {
			Svertice amigo = all_alunos[amigo_id-1];
			if(amigo.color.equals("white")) {
				amigo.parent = aluno;
				DFS_T_VISIT(amigo);
			}
		}
		aluno.color = "black";
		time++;
		aluno.endTime = time;
	}

}
