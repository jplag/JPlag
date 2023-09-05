//package daa_sociologia;
import java.util.*;


class Node  {
    int valor;
    LinkedList <Integer> amigos;
    boolean visto;
    
    Node (int n) {
        valor = n;
        amigos = new LinkedList <Integer> ();
        visto = false;
    }
}

class Grafo {
    int ngrupos, nfora, aux;
    Node lista [];
    Node trans [];
    Stack <Node> pilha;
    
    Grafo (int n) {
        ngrupos = 0;
        nfora = 0;
        aux = 0;
        lista = new Node [n];
        trans = new Node [n];
        pilha = new Stack<Node> ();
        
        for (int i=0; i<n ; i++) {
            lista[i] = new Node (i);
            trans[i] = new Node (i);
        }
    }
    
    public void DFS () {
        for (Node i : lista) {
            if (!i.visto) {
                DFS_Visit(i);
            }
        }
    }
    
    public void DFS_Visit (Node i) {
        i.visto = true;
        for (int k : i.amigos) {
            if (!lista[k].visto) {
                DFS_Visit(lista[k]);
            }
        }
        pilha.push(trans[i.valor]);
    }
    
    public void DFS_T () {
        while (!pilha.isEmpty()) {
            Node a = pilha.pop();
            if (!trans[a.valor].visto) {
                DFS_TVisit(a);
                if (aux>3) 
                    ngrupos++;
                else 
                    nfora+=aux;
                aux = 0;
            }
        }
    }
    
    public void DFS_TVisit (Node i) {
        i.visto = true;
        for (int k : i.amigos) {
            if (!trans[k].visto) {
                DFS_TVisit(trans[k]);
            }
        }
        aux++;
    }
}
public class Daa_sociologia {
    public static void main(String[] args) {
        Scanner in = new Scanner (System.in);
        int ncasos = in.nextInt();
        for (int q=0; q<ncasos; q++) {
            int npessoas = in.nextInt();
            Grafo l = new Grafo(npessoas);
            for (int w = 0; w<npessoas; w++) {
                int id = in.nextInt();
                int namigos = in.nextInt();
                for (int e = 0; e<namigos; e++) {
                    int amigo = in.nextInt();
                    l.lista[id-1].amigos.add(amigo-1);
                    l.trans[amigo-1].amigos.add(id-1);
                }
            } 
            l.DFS();
            l.DFS_T();
            System.out.println("Caso #" + (q+1));
            System.out.println(l.ngrupos + " " + l.nfora);
        }
    }
}