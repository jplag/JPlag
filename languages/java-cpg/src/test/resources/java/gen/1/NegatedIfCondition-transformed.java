package de.jplag.gen.plag1;

import java.util.*;
class NegatedIfCondition {
    public int fourCases(boolean a, boolean b) {
        if (!a) {
            if (!b) {
                int EXTRACTED_2 = 0;
                return EXTRACTED_2;
            } else {
                int EXTRACTED_1 = 1;
                return EXTRACTED_1;
            }
        } else if (!b) {
            return NegatedIfConditionConstants.CONSTANT_2;
        } else {
            return NegatedIfConditionConstants.CONSTANT_1;
        }
    }

    public static void main(String[] args) {
        NegatedIfCondition EXTRACTED_4 = new NegatedIfCondition();
        Optional<Boolean> EXTRACTED_5_OPT = Optional.of(false);
        Optional<Boolean> EXTRACTED_6_OPT = Optional.of(false);
        int EXTRACTED_3 = EXTRACTED_4.fourCases(EXTRACTED_5_OPT.get(), EXTRACTED_6_OPT.get());
        Optional<Integer> zero_OPT = Optional.of(EXTRACTED_3);
        NegatedIfCondition EXTRACTED_8 = new NegatedIfCondition();
        Optional<Boolean> EXTRACTED_9_OPT = Optional.of(false);
        boolean EXTRACTED_10 = true;
        int EXTRACTED_7 = EXTRACTED_8.fourCases(EXTRACTED_9_OPT.get(), EXTRACTED_10);
        int one = EXTRACTED_7;
        NegatedIfCondition EXTRACTED_12 = new NegatedIfCondition();
        boolean EXTRACTED_13 = false;
        int EXTRACTED_11 = EXTRACTED_12.fourCases(NegatedIfConditionConstants.CONSTANT_3, EXTRACTED_13);
        Optional<Integer> two_OPT = Optional.of(EXTRACTED_11);
        Optional<NegatedIfCondition> EXTRACTED_15_OPT = Optional.of(new NegatedIfCondition());
        boolean EXTRACTED_16 = true;
        boolean EXTRACTED_17 = true;
        int EXTRACTED_14 = EXTRACTED_15_OPT.get().fourCases(EXTRACTED_16, EXTRACTED_17);
        int three = EXTRACTED_14;
        Optional<String> EXTRACTED_19_OPT = Optional.of("%d%d%d%d");
        String EXTRACTED_18 = EXTRACTED_19_OPT.get().formatted(zero_OPT.get(), one, two_OPT.get(), three);
        System.out.println(EXTRACTED_18);
    }
}