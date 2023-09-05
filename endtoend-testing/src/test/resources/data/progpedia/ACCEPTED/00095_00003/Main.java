import java.io.*;
import java.util.Scanner;

public class Main 
{
	public static void resetCheckedMatrix(boolean []checkedMatrix,int matrixDim) 
	{
		for(int c=0;c<matrixDim;c++)
			checkedMatrix[c] = false;
	}
	public static void resetRelationCountMatrix(int []rcMatrix,int matrixDim) 
	{
		for(int c=0;c<matrixDim;c++)
				rcMatrix[c] = 0;
	}
	public static void fillRelationCountMatrix(boolean[][] matrix,int []rcMatrix ,int matrixDim) 
	{
		for(int c=0;c<matrixDim;c++)
			for(int l=0; l<matrixDim; l++)
			{
				if(matrix[c][l])
					rcMatrix[c]++;
			}
	}
	public static void showRelationCountMatrix(int []rcMatrix,int matrixDim) 
	{
		for(int c=0;c<matrixDim;c++)
					System.out.print("[" +  rcMatrix[c]+ "]");
		
		System.out.println();
	}
	
	public static void linkMatrix(boolean[][] matrix,int matrixDim)
	{
		for(int c=0;c<matrixDim;c++)
		{
			for(int l=0; l<matrixDim; l++)
			{
					// IF XY ...
					if(matrix[c][l])
					{
						for(int colSearch=0;colSearch<matrixDim;colSearch++)
						{
							if(matrix[l][colSearch]) // ... AND YZ
								matrix[c][colSearch] = true; // XZ
							
							if(matrix[colSearch][c]) // ... AND ZX
								matrix[colSearch][l] = true; // ZY								
						}
					}				
			}
		}
	}
	public static void resetMatrix(boolean[][] matrix,int matrixDim)
	{
		int c,l;
		
		for(c=0;c<matrixDim;c++)
		{
			for(l=0; l<c; l++)
				matrix[c][l] = false;
			
			matrix[c][l] = true;
			
			for(++l;l<matrixDim;l++)
				matrix[c][l] = false;
		}		
	}
	public static void showMatrix(boolean[][] matrix,int matrixDim)
	{
		int c,l;
		
		System.out.print("|||");
		
		for(c=0;c<matrixDim;c++)
			System.out.print("|" + (c+1) + "|");
	
		System.out.print("\n");
		
		for(c=0;c<matrixDim;c++)
		{
			System.out.print("|" + (c+1) + "|");
			
			for(l=0;l<matrixDim;l++)
			{
				System.out.print("[" + (matrix[c][l] ? 1 : 0) + "]");
			}
			System.out.print("\n");
		}		
		System.out.println("\n");		
	}
	public static void main(String[] args) throws IOException 
	{
		InputStreamReader converter = new InputStreamReader (System.in);
		Scanner scanner = new Scanner(converter);

		int nSituations = scanner.nextInt();
		
		for(int s=1;s<=nSituations;s++)
		{
			
			int matrixDim = scanner.nextInt();
			
			boolean matrix[][] = new boolean[matrixDim][matrixDim];
			
			resetMatrix(matrix,matrixDim);
			//showMatrix(matrix,matrixDim);

			
			
			
			for(int i=0;i<matrixDim;i++)
			{
				int index = scanner.nextInt();
				int relations = scanner.nextInt();
				
				for(int j=1;j<=relations;j++)
				{
					int c = scanner.nextInt();
					
					matrix[index-1][c-1] = true;
				}
			}
			
			//showMatrix(matrix,matrixDim);
			
			//boolean k=false;if(!k) return;
			
			
			linkMatrix(matrix,matrixDim);
			//showMatrix(matrix,matrixDim);
			
			int[] rcMatrix = new int[matrixDim];
			boolean[] checkedMatrix = new boolean[matrixDim]; 
			
			resetRelationCountMatrix(rcMatrix,matrixDim);
			resetCheckedMatrix(checkedMatrix,matrixDim);
			fillRelationCountMatrix(matrix,rcMatrix,matrixDim);
			//showRelationCountMatrix(rcMatrix, matrixDim);
			
			int nGroups = 0;
			int nOut = 0;
			
			
			
			for(int c=0;c<matrixDim;c++)
			{
				if(checkedMatrix[c])
					continue;
				
				int nRelationsC = rcMatrix[c];
				
				int c1;
				
				int nElements = 1;
				
				for(c1=0;c1<matrixDim;c1++)
				{
					if(!checkedMatrix[c1] && (c!=c1))
					{
						int nRelationsC1 = rcMatrix[c1];
	
						if(nRelationsC == nRelationsC1)
						{
							
							boolean equal = true;
							
							for(int l=0;l<matrixDim;l++)
							{
								equal &= (matrix[c][l] == matrix[c1][l]);
								if(!equal)
									break;
							}
	
							if(equal)
							{
								checkedMatrix[c] = true;
								checkedMatrix[c1] = true;
								nElements++;
							}
						}
					}
				}
				
				if(nElements < 4)
					nOut += nElements;
				else
					nGroups++;
				
			}
			
			System.out.println("Caso #" + s + "\n" + nGroups + " " + nOut );
			
		}
	}

}
