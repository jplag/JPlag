
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
    }

}
