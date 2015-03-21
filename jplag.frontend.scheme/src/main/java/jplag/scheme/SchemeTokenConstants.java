package jplag.scheme;

public interface SchemeTokenConstants extends jplag.TokenConstants {
  final static int FILE_END = 0;

  // Used to optionally separate methods from each other
  // with an always marked token
  final static int SEPARATOR_TOKEN = 1;
  
  final static int S_BOOL = 2;
  final static int S_NUMBER = 3;
  final static int S_CHAR = 4;
  final static int S_STRING = 5;
  final static int S_ID = 6;
  final static int S_LIST_BEGIN = 7;
  final static int S_LIST_END = 8;
  final static int S_VECTOR_BEGIN = 9;
  final static int S_VECTOR_END = 10;
  final static int S_LITERAL = 11;
  final static int S_QUOT_BEGIN = 12;
  final static int S_QUOT_END = 13;
  final static int S_CALL = 14;
  final static int S_LAMBDA_BEGIN = 15;
  final static int S_LAMBDA_END = 16;
  final static int S_FORMAL_BEGIN = 17;
  final static int S_FORMAL_END = 18;
  final static int S_BODY_BEGIN = 19;
  final static int S_BODY_END = 20;
  final static int S_IF_BEGIN = 21;
  final static int S_IF_END = 22;
  final static int S_ALTERN = 23;
  final static int S_ASSIGN_BEGIN = 24;
  final static int S_ASSIGN_END = 25;
  final static int S_COND_BEGIN = 26;
  final static int S_COND_END = 27;
  final static int S_ELSE = 28;
  final static int S_CASE_BEGIN = 29;
  final static int S_CASE_END = 30;
  final static int S_DO_BEGIN = 31;
  final static int S_DO_END = 32;
  final static int S_COMMAND = 33;
  final static int S_DEF_BEGIN = 34;
  final static int S_DEF_END = 35;
  final static int S_BEGIN = 36;
  final static int S_END = 37;
  final static int S_AND = 38;
  final static int S_OR = 39;
  final static int S_LET = 40;
  final static int S_DELAY = 41;
  final static int S_VAR = 42;

  final static int NUM_DIFF_TOKENS = 43;
}
