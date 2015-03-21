package jplag.csharp;


public class CSharpToken extends jplag.Token implements CSharpTokenConstants {
  private static final long serialVersionUID = 1L;
  private int line, column, length;

  public CSharpToken(int type, String file, int line, int column, int length) {
    super(type, file, line, column, length);
 }

  public int getLine() { return line; }
  public int getColumn() { return column; }
  public int getLength() { return length; }
  public void setLine(int line) { this.line = line; }
  public void setColumn(int column) { this.column = column; }
  public void setLength(int length) { this.length = length; }

  public static String type2string(int type) {
    switch (type) {
    case CSharpTokenConstants.FILE_END:
                                   return "**********";
    case _INVOCATION:              return "INVOCATION";
    case _OBJECT_CREATION:         return "OBJECT_CRE";
    case _ARRAY_CREATION:          return "ARRAY_CREA";
    case _ASSIGNMENT:              return "ASSIGNMENT";
    case _L_BRACE:                 return "L_BRACE { ";
    case _R_BRACE:                 return "R_BRACE } ";
    case _DECLARE_VAR:             return "DECLAREVAR";
    case _DECLARE_CONST:           return "DECLARE_CO";
    case _IF:                      return "IF {      ";
    case _ELSE:                    return "ELSE      ";
    case _END_IF:                  return "IF }      ";
    case _SWITCH_BEGIN:            return "SWITCH {  ";
    case _SWITCH_END:              return "SWITCH }  ";
    case _CASE:                    return "CASE      ";
    case _DO:                      return "DO        ";
    case _WHILE:                   return "WHILE     ";
    case _FOR:                     return "FOR       ";
    case _FOREACH:                 return "FOREACH   ";
    case _BREAK:                   return "BREAK     ";
    case _CONTINUE:                return "CONTINUE  ";
    case _GOTO:                    return "GOTO      ";
    case _RETURN:                  return "RETURN    ";
    case _THROW:                   return "THROW     ";
    case _CHECKED:                 return "CHECKED   ";
    case _UNCHECKED:               return "UNCHECKED ";
    case _LOCK:                    return "LOCK      ";
    case _USING:                   return "USING     ";
    case _TRY:                     return "TRY       ";
    case _CATCH:                   return "CATCH     ";
    case _FINALLY:                 return "FINALLY   ";
    case _NAMESPACE_BEGIN:         return "NAMESPACE{";
    case _NAMESPACE_END:           return "NAMESPACE}";
    case _USING_DIRECTIVE:         return "USING_DIR ";
    case _CLASS_BEGIN:             return "CLASS {   ";
    case _CLASS_END:               return "CLASS }   ";
    case _METHOD:                  return "METHOD    ";
    case _PROPERTY:                return "PROPERTY  ";
    case _EVENT:                   return "EVENT     ";
    case _INDEXER:                 return "INDEXER   ";
    case _OPERATOR:                return "OPERATOR  ";
    case _CONSTRUCTOR:             return "CONSTR    ";
    case _STATIC_CONSTR:           return "ST_CONSTR ";
    case _DESTRUCTOR:              return "DESTRUCTOR";
    case _STRUCT_BEGIN:            return "STRUCT {  ";
    case _STRUCT_END:              return "STRUCT }  ";
    case _INTERFACE_BEGIN:         return "INTERFACE{";
    case _INTERFACE_END:           return "INTERFACE}";
    case _ENUM:                    return "ENUM      ";
    case _DELEGATE:                return "DELEGATE  ";
    case _ATTRIBUTE:               return "ATTRIBUTE ";
    case _UNSAFE:                  return "UNSAFE    ";
    case _FIXED:                   return "FIXED     ";

    default:                      return "<UNKNOWN> ";
    }
  }
  
  public static int numberOfTokens() { 
    return NUM_DIFF_TOKENS;
  } 
}

