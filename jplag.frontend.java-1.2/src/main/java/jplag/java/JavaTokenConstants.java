package jplag.java;

public interface JavaTokenConstants extends jplag.TokenConstants {
  final static int FILE_END = 0;

  final static int J_PACKAGE = 1;               //
  final static int J_IMPORT = 2;                //
  final static int J_CLASS_BEGIN = 3;           //
  final static int J_CLASS_END = 4;             //
  final static int J_METHOD_BEGIN = 5;          //
  final static int J_METHOD_END = 6;            //
  final static int J_VARDEF = 7;                //
  final static int J_SYNC_BEGIN = 8;            //
  final static int J_SYNC_END = 9;              //
  final static int J_DO_BEGIN = 10;             //
  final static int J_DO_END = 11;               //
  final static int J_WHILE_BEGIN = 12;          //
  final static int J_WHILE_END = 13;            //
  final static int J_FOR_BEGIN = 14;            //
  final static int J_FOR_END = 15;              //
  final static int J_SWITCH_BEGIN = 16;         //
  final static int J_SWITCH_END = 17;           //
  final static int J_CASE = 18;                 //
  final static int J_TRY_BEGIN = 19;            //
  final static int J_CATCH_BEGIN = 20;          //
  final static int J_CATCH_END = 21;            //
  final static int J_FINALLY = 22;              //
  final static int J_IF_BEGIN = 23;             //
  final static int J_ELSE = 24;                 //
  final static int J_IF_END = 25;               //
  final static int J_COND = 26;                 //
  final static int J_BREAK = 27;                //
  final static int J_CONTINUE = 28;             //
  final static int J_RETURN = 29;               //
  final static int J_THROW = 30;                //
  final static int J_IN_CLASS_BEGIN=31;         //
  final static int J_IN_CLASS_END = 32;         //
  final static int J_APPLY = 33;                //
  final static int J_NEWCLASS = 34;             //
  final static int J_NEWARRAY = 35;             //
  final static int J_ASSIGN = 36;               //
  final static int J_INTERFACE_BEGIN=37;        //
  final static int J_INTERFACE_END = 38;        //
  final static int J_CONSTR_BEGIN = 39;         //
  final static int J_CONSTR_END = 40;           //
  final static int J_INIT_BEGIN = 41;           //
  final static int J_INIT_END = 42;             //
  final static int J_VOID = 43;                 //
  final static int J_ARRAY_INIT_BEGIN = 44;     //
  final static int J_ARRAY_INIT_END = 45;       //

  final static int NUM_DIFF_TOKENS = 46;
}
