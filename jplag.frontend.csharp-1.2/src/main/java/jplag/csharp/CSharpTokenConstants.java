package jplag.csharp;

public interface CSharpTokenConstants extends jplag.TokenConstants {
  final static int FILE_END = 0;

  // Used to optionally separate methods from each other
  // with an always marked token
  final static int SEPARATOR_TOKEN = 1;
  
  final static int _INVOCATION =       2;
  final static int _OBJECT_CREATION =  3;
  final static int _ARRAY_CREATION =   4;
  final static int _ASSIGNMENT =       5;
  final static int _L_BRACE =          6;
  final static int _R_BRACE =          7;
  final static int _DECLARE_VAR =      8;
  final static int _DECLARE_CONST =    9;
  final static int _IF =              10;
  final static int _ELSE =            11;
  final static int _SWITCH_BEGIN =    12;
  final static int _SWITCH_END =      13;
  final static int _CASE =            14;
  final static int _DO =              15;
  final static int _WHILE =           16;
  final static int _FOR =             17;
  final static int _FOREACH =         18;
  final static int _BREAK =           19;
  final static int _CONTINUE =        20;
  final static int _GOTO =            21;
  final static int _RETURN =          22;
  final static int _THROW =           23;
  final static int _CHECKED =         24;
  final static int _UNCHECKED =       25;
  final static int _LOCK =            26;
  final static int _USING =           27;
  final static int _TRY =             28;
  final static int _CATCH =           29;
  final static int _FINALLY =         30;
  final static int _NAMESPACE_BEGIN = 31;
  final static int _NAMESPACE_END =   32;
  final static int _USING_DIRECTIVE = 33;
  final static int _CLASS_BEGIN =     34;
  final static int _CLASS_END =       35;
  final static int _METHOD =          36;
  final static int _PROPERTY =        37;
  final static int _EVENT =           38;
  final static int _INDEXER =         39;
  final static int _OPERATOR =        40;
  final static int _CONSTRUCTOR =     41;
  final static int _STATIC_CONSTR =   42;
  final static int _DESTRUCTOR =      43;
  final static int _STRUCT_BEGIN =    44;
  final static int _STRUCT_END =      45;
  final static int _INTERFACE_BEGIN = 46;
  final static int _INTERFACE_END =   47;
  final static int _ENUM =            48;
  final static int _DELEGATE =        49;
  final static int _ATTRIBUTE =       50;
  final static int _END_IF =          51;
  final static int _UNSAFE =          52;
  final static int _FIXED =           53;

  final static int NUM_DIFF_TOKENS = 54;
}
