import java.util.*;

class GNode{
    public ArrayList<Integer> filhos= new ArrayList<Integer>();
    public ArrayList<Integer> DFSresult= new ArrayList<Integer>();
    public int cor= 0;
    /*
      0= branco
      1= cinza
    */
}

class Search{
    public void DFS(GNode inicio, GNode[] listapontos, GNode origem){
	inicio.cor= 1;
	for(int k= 0; k< inicio.filhos.size(); k++){
	    int temp= inicio.filhos.get(k);
	    if(origem.DFSresult.contains(temp)== false){
		origem.DFSresult.add(temp);
	    }
	    GNode filho= new GNode();
	    if(listapontos[temp-1].cor== 0){
		filho= listapontos[temp-1];
	    }
	    DFS(filho, listapontos, origem);
	}
    }
}

class sociologia{
    public static void main(String args[]){
	Scanner stdin= new Scanner(System.in);
	int numerocasos= stdin.nextInt();
	for(int contadorcasos= 0; contadorcasos< numerocasos; contadorcasos++){
	    int numeropessoas= stdin.nextInt();
	    GNode[] listadenos= new GNode[numeropessoas];
	    GNode[] listadenosinvertida= new GNode[numeropessoas];
	    for(int i= 0; i< numeropessoas; i++){
		listadenosinvertida[i]= new GNode();
		listadenosinvertida[i].DFSresult.add(i+1);
	    }
	    for(int contadorpessoas= 0; contadorpessoas< numeropessoas; contadorpessoas++){
		int posicaono= stdin.nextInt();
		int posicaotemp= posicaono-1;
		listadenos[posicaotemp]= new GNode();
		listadenos[posicaotemp].DFSresult.add(posicaono);
		int numerofilhos= stdin.nextInt();
		if(numerofilhos!= 0){
		    for(int contadorfilhos= 0; contadorfilhos< numerofilhos; contadorfilhos++){
			int valortemp= stdin.nextInt();
			int valortemptemp= valortemp-1;
			listadenos[posicaotemp].filhos.add(valortemp);
			listadenosinvertida[valortemptemp].filhos.add(posicaono);
		    }
		}
	    }
	   
	    Search DFSnormal= new Search();
	    for(int contadorpessoas= 0; contadorpessoas< numeropessoas; contadorpessoas++){
		DFSnormal.DFS(listadenos[contadorpessoas], listadenos, listadenos[contadorpessoas]);
		for(int resetnos= 0; resetnos< numeropessoas; resetnos++){
		    listadenos[resetnos].cor= 0;
		}
	    }
	    Search DFSinvertida= new Search();
	    for(int contadorpessoas= 0; contadorpessoas< numeropessoas; contadorpessoas++){
		DFSinvertida.DFS(listadenosinvertida[contadorpessoas], listadenosinvertida, listadenosinvertida[contadorpessoas]);
		for(int resetnos= 0; resetnos< numeropessoas; resetnos++){
		    listadenosinvertida[resetnos].cor= 0;
		}
	    }

	    int contadordegrupos= 0;
	    int numerodenosdefora= 0;
	    for(int contadorpessoas= 0; contadorpessoas< numeropessoas; contadorpessoas++){
		if(listadenos[contadorpessoas].cor== 0){
		    ArrayList<Integer> conjuncao= new ArrayList<Integer>();
		    conjuncao= listadenos[contadorpessoas].DFSresult;
		    conjuncao.retainAll(listadenosinvertida[contadorpessoas].DFSresult);
		    for(int l= 0; l< conjuncao.size(); l++){
			if(listadenos[contadorpessoas].DFSresult.size()!= 1){
			    listadenos[(conjuncao.get(l))-1].cor= 1;
			}
		    }
		    if(conjuncao.size() >= 4){
			contadordegrupos++;
		    }	
		}
	    }
	    for(int contadorpessoas= 0; contadorpessoas< numeropessoas; contadorpessoas++){
		if(listadenos[contadorpessoas].cor== 0){
		    numerodenosdefora++;
		}
	    }
	    if(contadordegrupos== 0){
		numerodenosdefora= numeropessoas;
	    }
	    System.out.println("Caso #" + (contadorcasos+1));
	    System.out.println(contadordegrupos + " " + numerodenosdefora);
	}
    }
}