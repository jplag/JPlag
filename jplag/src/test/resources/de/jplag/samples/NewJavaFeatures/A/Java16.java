
public class Java16 {
    public static void main(String[] args) {
        // Text blocks (JEP 378): 
        String query = """
                SELECT "EMP_ID", "LAST_NAME"
                FROM "EMPLOYEE_TB";
                """;
        System.out.println(query);
        
        // Pattern matching for instanceof (JEP 394):
        String x = "123";
        if (x instanceof String s) { String a = s; }
        
        // Record types (JEP 395):
        Point p = new Point(1, 2);
        System.out.println(p.add());
    }
    
    record Point(int x, int y) {
        public int add() {
            return x + y;
        }
    }
}
