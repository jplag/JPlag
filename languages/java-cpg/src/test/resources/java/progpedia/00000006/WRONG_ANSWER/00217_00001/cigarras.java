import java.util.Scanner;


public class cigarras {

	public static void analisa_caminho(int valor, int[] locais){
		int i = 0;
		while ( locais[i] != 0) {
			if (locais[i]==valor) {
			for (int c = i; c<locais.length;c++) {
				locais[c] = 0;
			}
			}
			else {
				locais[i+1] = valor;
			}
		}
	}

	//DeadCodeStart
	public static void escreve_caminho(int[] locais){
		int c = 0;
		while (c!=0){
			System.out.println(locais[c]);
			c++;
		}

	}
	//DeadCodeEnd
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		final int[] locais = new int[30];

		Scanner n = new Scanner(System.in);
		int valor = n.nextInt();
		while (valor != 0) {
			analisa_caminho(valor, locais);
			valor=n.nextInt();
		}
		//DeadCodeStart
		escreve_caminho(locais);
		//DeadCodeEnd
	}

}
