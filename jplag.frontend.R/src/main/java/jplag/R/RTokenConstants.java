package jplag.R;

/*
Tokens que consideramos importantes de R a la hora de analizar si hay plagio entre entregas de estudiantes.
*/


public interface RTokenConstants extends jplag.TokenConstants {
  final static int FILE_END = 			0;
  final static int SEPARATOR_TOKEN = 	1;

  final static int BEGIN_FUNCTION = 2;
  final static int END_FUNCTION = 3;
  final static int FUNCTION_CALL = 4;
  final static int NUMBER = 5;
  final static int STRING = 6;
  final static int BOOL = 7;
  final static int ASSIGN = 8;
  final static int ASSIGN_FUNC = 9;
  final static int ASSIGN_LIST = 10;
  final static int HELP = 11;
  final static int INDEX = 12;
  final static int PACKAGE = 13;
  final static int IF_BEGIN = 14;
  final static int IF_END = 15;
  final static int FOR_BEGIN = 16;
  final static int FOR_END = 17;
  final static int WHILE_BEGIN = 18;
  final static int WHILE_END = 19;
  final static int REPEAT_BEGIN = 20;
  final static int REPEAT_END = 21;
  final static int NEXT = 22;
  final static int BREAK = 23;
  final static int COMPOUND_BEGIN = 24;
  final static int COMPOUND_END = 25;


  final static int NUM_DIFF_TOKENS = 	26;
}
