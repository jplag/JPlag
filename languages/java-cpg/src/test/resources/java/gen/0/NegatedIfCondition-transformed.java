package de.jplag.gen.plag0;
class NegatedIfCondition {
    public int fourCases(boolean a, boolean b) {
        if (!a) {
            if (!b) {
                return NegatedIfConditionConstants.CONSTANT_1;
            } else {
                int EXTRACTED_3 = 1;
                return EXTRACTED_3;
            }
        } else if (!b) {
            int EXTRACTED_2 = 2;
            return EXTRACTED_2;
        } else {
            int EXTRACTED_1 = 3;
            return EXTRACTED_1;
        }
    }

    public static void main(String[] args) {
        int zero = NegatedIfConditionConstants.CONSTANT_2;
        boolean EXTRACTED_5 = false;
        boolean EXTRACTED_6 = true;
        int EXTRACTED_4 = NegatedIfConditionConstants.CONSTANT_3.fourCases(EXTRACTED_5, EXTRACTED_6);
        int one = EXTRACTED_4;
        NegatedIfCondition EXTRACTED_8 = new NegatedIfCondition();
        boolean EXTRACTED_9 = true;
        boolean EXTRACTED_10 = false;
        int EXTRACTED_7 = EXTRACTED_8.fourCases(EXTRACTED_9, EXTRACTED_10);
        int two = EXTRACTED_7;
        NegatedIfCondition EXTRACTED_12 = new NegatedIfCondition();
        boolean EXTRACTED_13 = true;
        int EXTRACTED_11 = EXTRACTED_12.fourCases(NegatedIfConditionConstants.CONSTANT_4, EXTRACTED_13);
        int three = EXTRACTED_11;
        String EXTRACTED_15 = "%d%d%d%d";
        String EXTRACTED_14 = EXTRACTED_15.formatted(zero, one, two, three);
        System.out.println(EXTRACTED_14);
    }
}