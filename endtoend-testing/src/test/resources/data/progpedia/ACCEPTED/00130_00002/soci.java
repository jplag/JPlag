import java.util.*;


class No{

int val,amigos;
boolean visitado;
LinkedList<Integer> adj;

    No(int vali)
                {
                val=vali;
                visitado=false;
                adj= new LinkedList<Integer>();
                amigos=0;}
    
    void addNo(int val)
        {  
            
            adj.addFirst(val);
            amigos++;            
                
        }
        }

class Grafo{
No[] g;
No[] gt;
int grupos,pessoas,ptemp;
LinkedList<Integer> tempos;

        Grafo(Scanner in)
            {
            grupos=0;
            tempos= new LinkedList<Integer>();
            pessoas=in.nextInt();
            g=new No[pessoas+1];
            gt=new No[pessoas+1];
            
            for (int i = 1;i<=pessoas;i++)
                {g[i]= new No(i);gt[i]= new No(i);}
            
            for (int i = 0;i<pessoas;i++)
                {   
                    int ptemp=in.nextInt();
                    int am=in.nextInt();
                    for (int j=1;j<=am;j++)
                        {
                         int amactual=in.nextInt();
                         g[ptemp].addNo(amactual);
                         gt[amactual].addNo(ptemp);
                        }
                   }
             }
         void DFS(){
         for (int i=1;i<=pessoas;i++)
                {if (!g[i].visitado)
                    { DFSVisit(g[i]);}}
                        
         }
         void DFSVisit(No actual)
                    { actual.visitado=true;
                        for (int cada : actual.adj) 
                        { if (!g[cada].visitado) {DFSVisit(g[cada]);}}
                        tempos.addFirst(actual.val);}
        
        
        void contar()
                        {
                         while (!tempos.isEmpty()){
                            int agora=tempos.removeFirst();
                            if (!gt[agora].visitado){
                                gt[agora].visitado=true;
                                int pessoast= contarf(agora);
                                if (pessoast>=4){grupos++;pessoas= pessoas-pessoast;}}
                                
                                }
                         }
                                
     int contarf(int pai)
                        {   int contagem=1;
                            gt[pai].visitado=true;
                            for (int adjac : gt[pai].adj)
                                { if (!gt[adjac].visitado)
                                        { contagem=contagem+contarf(adjac);}
                                 }
                             return contagem;
                            }
                                
}
                            
class soci{

public static void main(String args[])

        {
        
        Scanner ler= new Scanner(System.in);
        int casos;
        casos=ler.nextInt();
        for (int i=0;i<casos;i++)
            { Grafo um= new Grafo(ler);
              um.DFS();
              um.contar();
              System.out.println("Caso #" + (i+1) );
              System.out.println(um.grupos + " " + um.pessoas);} 

}
}

