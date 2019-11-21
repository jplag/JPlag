package jplag.scheme;

//import java.io.*;

public class SchemeToken extends jplag.Token implements SchemeTokenConstants {
	private static final long serialVersionUID = -143418583849687339L;
	private int line;

  public SchemeToken(int type, String file, int zeile) {
    super(type,file,zeile);
  }

  public int getLine() { return line; }
  public void setLine(int line) { this.line = line; }

  public int getColumn() {return 0;}
  public int getLength() {return 0;}

  public static String type2string(int type) {
    switch (type) {
    case SchemeTokenConstants.FILE_END:
                             return "********";
    case S_BOOL:             return "BOOL    ";
    case S_NUMBER:           return "NUMBER  ";
    case S_CHAR:             return "CHAR    ";
    case S_STRING:           return "STRING  ";
    case S_ID:               return "ID      ";
    case S_LIST_BEGIN:       return "(LIST   ";
    case S_LIST_END:         return "LIST)   ";
    case S_VECTOR_BEGIN:     return "(VECTOR ";
    case S_VECTOR_END:       return "VECTOR) ";
    case S_LITERAL:          return "LITERAL ";
    case S_QUOT_BEGIN:       return "(QUOT   ";
    case S_QUOT_END:         return "QUOT)   ";
    case S_CALL:             return "CALL    ";
    case S_LAMBDA_BEGIN:     return "(LAMBDA ";
    case S_LAMBDA_END:       return "LAMBDA) ";
    case S_FORMAL_BEGIN:     return "(FORMAL ";
    case S_FORMAL_END:       return "FORMAL) ";
    case S_BODY_BEGIN:       return "(BODY   ";
    case S_BODY_END:         return "BODY)   ";
    case S_IF_BEGIN:         return "(IF     ";
    case S_IF_END:           return "IF)     ";
    case S_ALTERN:           return "ALTERN  ";
    case S_ASSIGN_BEGIN:     return "(ASSIGN ";
    case S_ASSIGN_END:       return "ASSIGN) ";
    case S_COND_BEGIN:       return "(COND   ";
    case S_COND_END:         return "COND)   ";
    case S_ELSE:             return "ELSE    ";
    case S_CASE_BEGIN:       return "(CASE   ";
    case S_CASE_END:         return "CASE)   ";
    case S_DO_BEGIN:         return "(DO     ";
    case S_DO_END:           return "DO)     ";
    case S_COMMAND:          return "COMMAND ";
    case S_DEF_BEGIN:        return "(DEF    ";
    case S_DEF_END:          return "DEF)    ";
    case S_BEGIN:            return "(       ";
    case S_END:              return ")       ";
    case S_AND:              return "AND     ";
    case S_OR:               return "OR      ";
    case S_LET:              return "LET     ";
    case S_DELAY:            return "DELAY   ";
    case S_VAR:              return "VAR     ";
      
    default:                 return "<?????> ";
    }
  }

  @Override
  public int numberOfTokens() {
    return NUM_DIFF_TOKENS;
  }

  public static int staticNumberOfTokens() { return NUM_DIFF_TOKENS; }
}
