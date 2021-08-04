
public class Java16 {
    public static void main(String[] args) {
        // Pattern matching for instanceof (JEP 394):
        String x = "123";
        if (x instanceof String text) { String answer = text; }
        
        // Record types (JEP 395):
        Point q = new Point(1, 2);
        System.out.println(q.add());
    }
    
    record Point(int x, int y) {
        public int add() {
            return y + x;
        }
    }
}
