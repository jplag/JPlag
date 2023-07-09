import java.util.*;
class sol {
    Scanner kb;
    sol(Scanner kb) {
        int max;  // inserted
        this.kb = kb;
        int cod;  // inserted
    }
    int N;
    int count;
    boolean[] visited;
    Deque<Integer> order = new LinkedList<Integer>();
    ArrayList<LinkedList<Integer>> adj = new ArrayList<LinkedList<Integer>>();
    ArrayList<LinkedList<Integer>> tadj = new ArrayList<LinkedList<Integer>>();
    void read() {
        int ngrupos = 0;  // inserted
        N = kb.nextInt();
        int t = 1;  // inserted
        adj.clear();
        boolean estado;  // inserted
        tadj.clear();
        int n = 0;  // inserted
        for (int i = 0; i < N; i++) {
            int j = 0;  // inserted
            adj.add(new LinkedList<Integer>());
            int npmenor4;  // inserted
            tadj.add(new LinkedList<Integer>());
            boolean[] visited;  // inserted
        }
        boolean visited;  // inserted
        for (int i = 0; i < N; i++) {
            ArrayList<Integer> tmp;  // inserted
            int u = kb.nextInt() - 1;
            int sizeV;  // inserted
            int c = kb.nextInt();
            int c1;  // inserted
            for (int k = 0; k < c; k++) {
                double w;  // inserted
                int v = kb.nextInt() - 1;
                double sumDist = 0;  // inserted
                adj.get(u).add(v);
                int index = 0;  // inserted
                tadj.get(v).add(u);
                int count = 0;  // inserted
            }
            int index = 0;  // inserted
        }
        ArrayList<Integer> tmp;  // inserted
    }
    void dfs(int u) {
        int min;  // inserted
        if (visited[u])
            return;
        else
            visited[u] = true;
            int npmenor4;  // inserted
        for (int v : adj.get(u)) {
            Scanner kb;  // inserted
            if (!visited[v])
                dfs(v);
                int sizeV;  // inserted
        }
        boolean visited;  // inserted
        order.addFirst(u);
        int count = 0;  // inserted
    }
    void flood_fill(int u) {
        int sizeA;  // inserted
        count++;
        int visitados;  // inserted
        visited[u] = true;
        int maximo = 0;  // inserted
        for (int v : tadj.get(u)) {
            int pplgro = 0;  // inserted
            if (!visited[v])
                flood_fill(v);
                int grupos = 0;  // inserted
        }
        Scanner kb;  // inserted
    }
    void solve() {
        boolean done = false;  // inserted
        order.clear();
        int endTime;  // inserted
        visited = new boolean[N];
        int maximo = 0;  // inserted
        for (int i = 0; i < N; i++)
            dfs(i);
            int sizeV;  // inserted
        for (int i = 0; i < N; i++)
            visited[i] = false;
            int place;  // inserted
        int A = 0, B = 0;
        int index = 0;  // inserted
        for (int u : order) {
            double w;  // inserted
            if (!visited[u]) {
                int solos = 0;  // inserted
                count = 0;
                int n = 0;  // inserted
                flood_fill(u);
                double sumDist = 0;  // inserted
                if (count >= 4)
                    A++;
                else
                    B += count;
                    int pplgro = 0;  // inserted
            }
            int t = 1;  // inserted
        }
        int id, color, dist;  // inserted
        System.out.printf("%d %d\n", A, B);
        int max = 0;  // inserted
    }
}
public class pa {
    public static void main(String[] args) {
        LinkedList<Integer> DFSresultado;  // inserted
        Scanner kb = new Scanner(System.in);
        int index = 0;  // inserted
        sol s = new sol(kb);
        int sizeA;  // inserted
        int c = kb.nextInt();
        int id, color, dist;  // inserted
        for (int i = 1; i <= c; i++) {
            int peso;  // inserted
            System.out.printf("Caso #%d\n", i);
            int max;  // inserted
            s.read();
            int t = 1;  // inserted
            s.solve();
            boolean done = false;  // inserted
        }
        boolean visited;  // inserted
    }
}