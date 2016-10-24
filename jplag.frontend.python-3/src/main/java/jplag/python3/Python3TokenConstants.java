package jplag.python3;

public interface Python3TokenConstants extends jplag.TokenConstants {

    final static int FILE_END = 0;
    final static int SEPARATOR_TOKEN = 1;

    final static int IMPORT = 2;
    final static int CLASS_BEGIN = 3;
    final static int CLASS_END = 4;
    final static int METHOD_BEGIN = 5;
    final static int METHOD_END = 6;
    final static int ASSIGN = 7;
    final static int WHILE_BEGIN = 8;
    final static int WHILE_END = 9;
    final static int FOR_BEGIN = 10;
    final static int FOR_END = 11;
    final static int TRY_BEGIN = 12;
    final static int EXCEPT_BEGIN = 13;
    final static int EXCEPT_END = 14;
    final static int FINALLY = 15;
    final static int IF_BEGIN = 16;
    final static int IF_END = 18;
    final static int APPLY = 19;
    final static int BREAK = 20;
    final static int CONTINUE = 21;
    final static int RETURN = 22;
    final static int RAISE = 23;
    final static int DEC_BEGIN = 24;
    final static int DEC_END = 25;
    final static int LAMBDA = 26;
    final static int ARRAY = 27;
    final static int ASSERT = 28;
    final static int YIELD = 29;
    final static int DEL = 30;
    final static int WITH_BEGIN = 31;
    final static int WITH_END = 32;

    final static int NUM_DIFF_TOKENS = 33;
}
