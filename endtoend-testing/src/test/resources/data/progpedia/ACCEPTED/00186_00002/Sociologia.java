import java.util.*;
class Arco {
	int peso;
	int aponta;
	Arco(int p,int a){
		peso=p;
		aponta=a;
	}
}
class Grafo {
	 
	int sizeV;
	int sizeA;	
	Vertex vertex[];
	int visitado[];
	
class Vertex{
	LinkedList <Arco> arestas;
	
	Vertex(){
		arestas= new LinkedList<Arco>();
	}
}

	Grafo(int tamanho){
		sizeV=tamanho;
		vertex = new Vertex[tamanho];
		visitado = new int[tamanho];  //0->por visitar, 1->visitado, 2->fexado
		for(int i =0;i<tamanho;i++){
			vertex[i]=new Vertex();
			visitado[i]=0;
		}
	}
	
	public void AddArco(int were,int p,int a){ //Adiciona arco
		vertex[were].arestas.add(new Arco(p,a));
		sizeA++;
	}

	public int Vertices(){
		return sizeV;
	}

	public int Arcos(){
		return sizeA;
	}
	
	boolean existeA(int a, int b){
		ListIterator<Arco> iterador = vertex[a].arestas.listIterator();
		while(iterador.hasNext())
			if(iterador.next().aponta==b)
				return true;
	return false;					
	}
	
	String existeS(String xpto){ //VERIFICA A EXISTENCIA DE UM ARCO QUE UNE DUAS LETRAS
		for(int i=0;i<xpto.length()-1;i++){
			int vert = xpto.charAt(i)-'A';
			int aresta=xpto.charAt(i+1)-'A';
		if(!existeA(vert,aresta))
			return "n�o";	
		}
		return "sim";
	}
	
	int post (int a){
		int count =1;
		visitado[a]=1;
		ListIterator<Arco> iterador = vertex[a].arestas.listIterator();
		while(iterador.hasNext()){
			int temp = iterador.next().aponta;
			if(visitado[temp]==0)
				count += post(temp);
			
		}
		return count;
	}
}

class Sociologia{ 

	static void DFS(Grafo xpto,int v){ 
		xpto.visitado[v]=1;
		ListIterator<Arco> iterador = xpto.vertex[v].arestas.listIterator();
		while(iterador.hasNext()){   //n
			int aux = iterador.next().aponta;
			if(xpto.visitado[aux]==0)
				DFS(xpto,aux);
		}
		fexado.push(v);
	}

	static int DFS1(Grafo xpto,int v){
		xpto.visitado[v]=1;
		ListIterator<Arco> iterador = xpto.vertex[v].arestas.listIterator();
		int count=1;
		while(iterador.hasNext()){
			int aux = iterador.next().aponta;
			if(xpto.visitado[aux]==0)
				count += DFS1(xpto,aux);
		}
		return count;
	}


	static Stack<Integer> fexado = new Stack<Integer>();

	public static void main(String Args[]){
		Scanner ler = new Scanner(System.in);
		StringBuilder output=new StringBuilder();
		int casos = ler.nextInt();
		
		for(int lim=0;lim<casos;lim++){
			int npessoas=ler.nextInt();
			Grafo amigos = new Grafo(npessoas);
			Grafo amigos1 = new Grafo(npessoas);
			for(int i=0;i<npessoas;i++){ //n
				int amigo= ler.nextInt();
				int namigos=ler.nextInt();
				for(int x=0;x<namigos;x++){ //2k
					int amig = ler.nextInt();
					amigos.AddArco(amigo-1,0,amig-1);
					amigos1.AddArco(amig-1, 0, amigo-1);
				}
			}//n.2k
			for(int i =0;i<amigos.sizeV;i++) //n.2k + 2n
				if(amigos.visitado[i]==0)
					DFS(amigos,i);

			output.append(String.format("Caso #%d\n",lim+1));
			int grupos=0;
			int solos=0;

			while(!fexado.isEmpty()){ // n.2k + 4n
				int aux = fexado.pop();
				if(amigos1.visitado[aux]==0){
					int count = DFS1(amigos1,aux);
					if(count >=4)
						grupos++;
					else
						solos+=count;
				}
			}
			output.append(String.format("%d %d\n",grupos,solos));
		}

		System.out.print(output);
		ler.close();
	}
}
