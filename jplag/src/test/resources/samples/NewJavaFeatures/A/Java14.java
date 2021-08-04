
public enum Java14 {
    MONDAY,
    TUESDAY,
    SATURDAY,
    SUNDAY;

    public static void main(String[] args) {
        // Advanced switch expressions (JEP 361):
        boolean isWeekend = switch (MONDAY) {
        case SATURDAY, SUNDAY -> true;
        default -> false;
        };
        System.out.println(isWeekend);

        // Text blocks (JEP 378): 
        String query = """
                SELECT "EMP_ID", "LAST_NAME"
                FROM "EMPLOYEE_TB";
                """;
        System.out.println(query);
    }

}
