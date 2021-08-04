
public enum Java14 {
    MONDAY,
    TUESDAY,
    SATURDAY,
    SUNDAY;

    public static void main(String[] args) {
        // Advanced switch expressions (JEP 361):
        boolean isWeekend = switch (MONDAY) {
        case FRIDAY, SATURDAY, SUNDAY -> true;
        default -> false;
        };
        System.out.println(isWeekend);

        // Text blocks (JEP 378): 
        String query = """
                SELECT "EMPLOYEE_ID", "NAME"
                FROM "EMPLOYEE_TABLE";
                """;
        System.out.println(query);
    }

}
