package jplag;

/**
 * This class implements the Greedy String Tiling algorithm as introduced by Michael Wise. However, it is very specific
 * to the classes {@link Structure}, {@link Token}, as well as {@link Matches} and {@link Match}.
 * @see <a href=
 * "https://www.researchgate.net/publication/262763983_String_Similarity_via_Greedy_String_Tiling_and_Running_Karp-Rabin_Matching">
 * String Similarity via Greedy String Tiling and Running Karp−Rabin Matching </a>
 */
public class GreedyStringTiling implements TokenConstants {

    private Matches matches = new Matches();
    private JPlag program;

    public GreedyStringTiling(JPlag program) {
        this.program = program;
    }

    /**
     * Creating hashes in linear time. The hash-code will be written in every Token for the next <hash_length> token
     * (includes the Token itself).
     * @param structure contains the tokens.
     * @param hashLength is the hash length (condition: 1 < hashLength < 26)
     * @param makeTable determines if a simple hash table is created in the structure.
     */
    public void createHashes(Structure structure, int hashLength, boolean makeTable) {
        // Here the upper boundary of the hash length is set.
        // It is determined by the number of bits of the 'int' data type and the number of tokens.
        if (hashLength < 1) {
            hashLength = 1;
        }
        hashLength = (hashLength < 26 ? hashLength : 25);

        if (structure.size() < hashLength) {
            return;
        }

        int modulo = ((1 << 6) - 1);   // Modulo 64!

        int loops = structure.size() - hashLength;
        structure.table = (makeTable ? new Table(3 * loops) : null);
        int hash = 0;
        int i;
        int hashedLength = 0;
        for (i = 0; i < hashLength; i++) {
            hash = (2 * hash) + (structure.tokens[i].type & modulo);
            hashedLength++;
            if (structure.tokens[i].marked) {
                hashedLength = 0;
            }
        }
        int factor = (hashLength != 1 ? (2 << (hashLength - 2)) : 1);

        if (makeTable) {
            for (i = 0; i < loops; i++) {
                if (hashedLength >= hashLength) {
                    structure.tokens[i].hash = hash;
                    structure.table.add(hash, i);   // add into hashtable
                } else {
                    structure.tokens[i].hash = -1;
                }
                hash -= factor * (structure.tokens[i].type & modulo);
                hash = (2 * hash) + (structure.tokens[i + hashLength].type & modulo);
                if (structure.tokens[i + hashLength].marked) {
                    hashedLength = 0;
                } else {
                    hashedLength++;
                }
            }
        } else {
            for (i = 0; i < loops; i++) {
                structure.tokens[i].hash = (hashedLength >= hashLength) ? hash : -1;
                hash -= factor * (structure.tokens[i].type & modulo);
                hash = (2 * hash) + (structure.tokens[i + hashLength].type & modulo);
                if (structure.tokens[i + hashLength].marked) {
                    hashedLength = 0;
                } else {
                    hashedLength++;
                }
            }
        }
        structure.hash_length = hashLength;
    }

    public final JPlagComparison compare(Submission subA, Submission subB) {
        Submission A, B, tmp;
        if (subA.tokenList.size() > subB.tokenList.size()) {
            A = subB;
            B = subA;
        } else {
            A = subB;
            B = subA;
        }
        // if hashtable exists in first but not in second structure: flip around!
        if (B.tokenList.table == null && A.tokenList.table != null) {
            tmp = A;
            A = B;
            B = tmp;
        }

        return compare(A, B, this.program.getOptions().getMinTokenMatch());
    }

    // first parameter should contain the smaller sequence!!!
    private final JPlagComparison compare(Submission subA, Submission subB, int mml) {
        Structure structA = subA.tokenList;
        Structure structB = subB.tokenList;

        // FILE_END used as pivot

        // init
        Token[] A = structA.tokens;
        Token[] B = structB.tokens;
        int lengthA = structA.size() - 1;  // minus pivots!
        int lengthB = structB.size() - 1;  // minus pivots!
        JPlagComparison comparison = new JPlagComparison(subA, subB);

        if (lengthA < mml || lengthB < mml) {
            return comparison;
        }

        // Initialize
        if (!program.getOptions().hasBaseCode()) {
            for (int i = 0; i <= lengthA; i++) {
                A[i].marked = A[i].type == FILE_END || A[i].type == SEPARATOR_TOKEN;
            }

            for (int i = 0; i <= lengthB; i++) {
                B[i].marked = B[i].type == FILE_END || B[i].type == SEPARATOR_TOKEN;
            }
        } else {
            for (int i = 0; i <= lengthA; i++) {
                A[i].marked = A[i].type == FILE_END || A[i].type == SEPARATOR_TOKEN || A[i].basecode;
            }

            for (int i = 0; i <= lengthB; i++) {
                B[i].marked = B[i].type == FILE_END || B[i].type == SEPARATOR_TOKEN || B[i].basecode;
            }
        }

        // start:
        if (structA.hash_length != this.program.getOptions().getMinTokenMatch()) {
            createHashes(structA, mml, false);
        }
        if (structB.hash_length != this.program.getOptions().getMinTokenMatch() || structB.table == null) {
            createHashes(structB, mml, true);
        }

        int maxmatch;
        int[] elemsB;

        do {
            maxmatch = mml;
            matches.clear();
            for (int x = 0; x <= lengthA - maxmatch; x++) {
                if (A[x].marked || A[x].hash == -1 || (elemsB = structB.table.get(A[x].hash)) == null) {
                    continue;
                }
                inner: for (int i = 1; i <= elemsB[0]; i++) { // elemsB[0] contains the length of the Array
                    int y = elemsB[i];
                    if (B[y].marked || maxmatch > lengthB - y) {
                        continue;
                    }

                    int j, hx, hy;
                    for (j = maxmatch - 1; j >= 0; j--) { // begins comparison from behind
                        if (A[hx = x + j].type != B[hy = y + j].type || A[hx].marked || B[hy].marked) {
                            continue inner;
                        }
                    }

                    // expand match
                    j = maxmatch;
                    while (A[hx = x + j].type == B[hy = y + j].type && !A[hx].marked && !B[hy].marked) {
                        j++;
                    }

                    if (j > maxmatch) {  // new biggest match? -> delete current smaller
                        matches.clear();
                        maxmatch = j;
                    }
                    matches.addMatch(x, y, j);  // add match
                }
            }
            for (int i = matches.size() - 1; i >= 0; i--) {
                int x = matches.matches[i].startA;  // begining of sequence A
                int y = matches.matches[i].startB;  // begining of sequence B
                comparison.addMatch(x, y, matches.matches[i].length);
                // in order that "Match" will be newly build (because reusing)
                for (int j = matches.matches[i].length; j > 0; j--) {
                    A[x++].marked = B[y++].marked = true;   // mark all Token!
                }
            }

        } while (maxmatch != mml);

        return comparison;
    }

    public final JPlagBaseCodeComparison compareWithBaseCode(Submission subA, Submission subB) {
        Submission A, B, tmp;
        if (subA.tokenList.size() > subB.tokenList.size()) {
            A = subB;
            B = subA;
        } else {
            A = subB;
            B = subA;
        }
        // if hashtable exists in first but not in second structure: flip around!
        if (B.tokenList.table == null && A.tokenList.table != null) {
            tmp = A;
            A = B;
            B = tmp;
        }

        return compareWithBaseCode(A, B, this.program.getOptions().getMinTokenMatch());
    }

    private JPlagBaseCodeComparison compareWithBaseCode(Submission subA, Submission subB, int mml) {
        Structure structA = subA.tokenList;
        Structure structB = subB.tokenList;

        // FILE_END used as pivot

        // init
        Token[] A = structA.tokens;
        Token[] B = structB.tokens;
        int lengthA = structA.size() - 1;  // minus pivots!
        int lengthB = structB.size() - 1;  // minus pivots!
        JPlagBaseCodeComparison baseCodeComparison = new JPlagBaseCodeComparison(subA, subB);

        if (lengthA < mml || lengthB < mml) {
            return baseCodeComparison;
        }

        // Initialize
        for (int i = 0; i <= lengthA; i++) {
            A[i].marked = A[i].type == FILE_END || A[i].type == SEPARATOR_TOKEN;
        }

        for (int i = 0; i <= lengthB; i++) {
            B[i].marked = B[i].type == FILE_END || B[i].type == SEPARATOR_TOKEN;
        }

        // start:
        if (structA.hash_length != this.program.getOptions().getMinTokenMatch()) {
            createHashes(structA, mml, true);
        }
        if (structB.hash_length != this.program.getOptions().getMinTokenMatch() || structB.table == null) {
            createHashes(structB, mml, true);
        }

        int maxmatch;
        int[] elemsB;

        do {
            maxmatch = mml;
            matches.clear();
            for (int x = 0; x <= lengthA - maxmatch; x++) {
                if (A[x].marked || A[x].hash == -1 || (elemsB = structB.table.get(A[x].hash)) == null) {
                    continue;
                }
                inner: for (int i = 1; i <= elemsB[0]; i++) {// elemsB[0] contains the length of the Array
                    int y = elemsB[i];
                    if (B[y].marked || maxmatch > lengthB - y) {
                        continue;
                    }

                    int j, hx, hy;
                    for (j = maxmatch - 1; j >= 0; j--) { // begins comparison from behind
                        if (A[hx = x + j].type != B[hy = y + j].type || A[hx].marked || B[hy].marked) {
                            continue inner;
                        }
                    }
                    // expand match
                    j = maxmatch;
                    while (A[hx = x + j].type == B[hy = y + j].type && !A[hx].marked && !B[hy].marked) {
                        j++;
                    }

                    if (j != maxmatch) {  // new biggest match? -> delete current smaller
                        matches.clear();
                        maxmatch = j;
                    }
                    matches.addMatch(x, y, j);  // add match
                }
            }
            for (int i = matches.size() - 1; i >= 0; i--) {
                int x = matches.matches[i].startA;  // beginning in sequence A
                int y = matches.matches[i].startB;  // beginning in sequence B
                baseCodeComparison.addMatch(x, y, matches.matches[i].length);
                // in order that "Match" will be newly build (because reusing)
                for (int j = matches.matches[i].length; j > 0; j--) {
                    A[x].marked = B[y].marked = true;   // mark all Token!
                    A[x].basecode = B[y].basecode = true;
                    x++;
                    y++;
                }
            }
        } while (maxmatch != mml);

        return baseCodeComparison;
    }

    public void resetBaseSubmission(Submission sub) {
        Structure tmpStruct = sub.tokenList;
        Token[] tok = tmpStruct.tokens;
        for (int z = 0; z < tmpStruct.size() - 1; z++) {
            tok[z].basecode = false;
        }
    }
}
