class NegatedIfCondition {

    public int fourCases(boolean a, boolean b) {
        if (!a) {
            if (!b) return 0;
            else return 1;
        } else if (!b) return 2;
        else return 3;
    }

    public static void main(String[] args) {
        int zero = new NegatedIfCondition().fourCases(false, false);
        int one  = new NegatedIfCondition().fourCases(false, true);
        int two  = new NegatedIfCondition().fourCases(true, false);
        int three = new NegatedIfCondition().fourCases(true, true);
        System.out.println("%d%d%d%d".formatted(zero,one,two,three));
    }
}