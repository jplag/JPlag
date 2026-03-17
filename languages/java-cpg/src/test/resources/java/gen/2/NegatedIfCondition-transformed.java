package de.jplag.gen.plag2;

import java.util.Optional;
class NegatedIfCondition {
    public boolean NegatedIfCondition_callMe_not() {
        throw new RuntimeException("You'd better not have called me!");
    }

    public int fourCases(boolean a, boolean b) {
        if (!a)
            if (!b) {
                int EXTRACTED_12 = 0;
                int EXTRACTED_10 = EXTRACTED_12;
                int EXTRACTED_4 = EXTRACTED_10;
                return EXTRACTED_4;
            } else {
                Optional<Integer> EXTRACTED_11_OPT = Optional.of(1);
                int EXTRACTED_9 = EXTRACTED_11_OPT.get();
                Optional<Integer> EXTRACTED_3_OPT = Optional.of(EXTRACTED_9);
                return EXTRACTED_3_OPT.get();
            }
        else if (!b) {
            int EXTRACTED_8 = 2;
            int EXTRACTED_6 = EXTRACTED_8;
            int EXTRACTED_2 = EXTRACTED_6;
            return EXTRACTED_2;
        } else {
            Optional<Integer> EXTRACTED_7_OPT = Optional.of(3);
            int EXTRACTED_5 = EXTRACTED_7_OPT.get();
            Optional<Integer> EXTRACTED_1_OPT = Optional.of(EXTRACTED_5);
            return EXTRACTED_1_OPT.get();
        }
    }

    public static void main(String[] args) {
        Optional<NegatedIfCondition> EXTRACTED_14_OPT = Optional.of(new NegatedIfCondition());
        boolean EXTRACTED_15 = false;
        int EXTRACTED_13 = EXTRACTED_14_OPT.get().fourCases(EXTRACTED_15, NegatedIfConditionConstants.CONSTANT_1);
        int zero = EXTRACTED_13;
        NegatedIfCondition EXTRACTED_17 = new NegatedIfCondition();
        Optional<Boolean> EXTRACTED_18_OPT = Optional.of(false);
        boolean EXTRACTED_19 = true;
        int EXTRACTED_16 = EXTRACTED_17.fourCases(EXTRACTED_18_OPT.get(), EXTRACTED_19);
        int one = EXTRACTED_16;
        int two = NegatedIfConditionConstants.CONSTANT_2;
        NegatedIfCondition EXTRACTED_21 = new NegatedIfCondition();
        Optional<Boolean> EXTRACTED_22_OPT = Optional.of(true);
        boolean EXTRACTED_23 = true;
        int EXTRACTED_20 = EXTRACTED_21.fourCases(EXTRACTED_22_OPT.get(), EXTRACTED_23);
        int three = EXTRACTED_20;
        String EXTRACTED_25 = "%d%d%d%d";
        String EXTRACTED_24 = EXTRACTED_25.formatted(zero, one, two, three);
        System.out.println(EXTRACTED_24);
    }
}