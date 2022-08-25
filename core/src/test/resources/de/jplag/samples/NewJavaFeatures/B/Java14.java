
public enum Java14 {
    MONDAY,
    TUESDAY,
    SATURDAY,
    SUNDAY;

    public static void main(String[] args) {
        // Old switch expressions:
        boolean isWeekend;
        switch (MONDAY) {
            case SATURDAY:
            case SUNDAY: isWeekend = true; break;
            default: isWeekend = false;
        }
        System.out.println(isWeekend);
    }

}
