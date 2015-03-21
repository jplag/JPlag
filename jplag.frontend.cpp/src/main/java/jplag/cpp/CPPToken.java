package jplag.cpp;

public class CPPToken extends jplag.Token implements CPPTokenConstants {
	private static final long serialVersionUID = 1L;
	private int line;

	public CPPToken(int type, String file, int zeile) {
		super(type,file,zeile);
	}

	public int getLine() { return line; }
	public void setLine(int line) { this.line = line; }

	public int getColumn() {return 0;}
	public int getLength() {return 0;}

	public static String type2string(int type) {
		switch (type) {
			case CPPTokenConstants.FILE_END:
									return "********";

			case C_BLOCK_BEGIN:     return "BLOCK{";
			case C_BLOCK_END:       return "}BLOCK";
			case C_SCOPE:           return "SCOPE";
			case C_QUESTIONMARK:    return "COND";
			case C_ELLIPSIS:        return "...";
			case C_ASSIGN:          return "ASSIGN";
			case C_DOT:             return "DOT";
			case C_ARROW:           return "ARROW";
			case C_DOTSTAR:         return "DOTSTAR";
			case C_ARROWSTAR:       return "ARROWSTAR";
			case C_AUTO:            return "AUTO";
			case C_BREAK:           return "BREAK";
			case C_CASE:            return "CASE";
			case C_CATCH:           return "CATCH";
			case C_CHAR:            return "CHAR";
			case C_CONST:           return "CONST";
			case C_CONTINUE:        return "CONTINUE";
			case C_DEFAULT:         return "DEFAULT";
			case C_DELETE:          return "DELETE";
			case C_DO:              return "DO";
			case C_DOUBLE:          return "DOUBLE";
			case C_ELSE:            return "ELSE";
			case C_ENUM:            return "ENUM";
			case C_EXTERN:          return "EXTERN";
			case C_FLOAT:           return "FLOAT";
			case C_FOR:             return "FOR";
			case C_FRIEND:          return "FRIEND";
			case C_GOTO:            return "GOTO";
			case C_IF:              return "IF";
			case C_INLINE:          return "INLINE";
			case C_INT:             return "INT";
			case C_LONG:            return "LONG";
			case C_NEW:             return "NEW";
			case C_PRIVATE:         return "PRIVATE";
			case C_PROTECTED:       return "PROTECTED";
			case C_PUBLIC:          return "PUBLIC";
			case C_REDECLARED:      return "REDECLARED";
			case C_REGISTER:        return "REGISTER";
			case C_RETURN:          return "RETURN";
			case C_SHORT:           return "SHORT";
			case C_SIGNED:          return "SIGNED";
			case C_SIZEOF:          return "SIZEOF";
			case C_STATIC:          return "STATIC";
			case C_STRUCT:          return "STRUCT";
			case C_CLASS:           return "CLASS";
			case C_SWITCH:          return "SWITCH";
			case C_TEMPLATE:        return "TEMPLATE";
			case C_THIS:            return "THIS";
			case C_TRY:             return "TRY";
			case C_TYPEDEF:         return "TYPEDEF";
			case C_UNION:           return "UNION";
			case C_UNSIGNED:        return "UNSIGNED";
			case C_VIRTUAL:         return "VIRTUAL";
			case C_VOID:            return "VOID";
			case C_VOLANTILE:       return "VOLANTILE";
			case C_WHILE:           return "WHILE";
			case C_OPERATOR:        return "OPERATOR";
			case C_THROW:           return "THROW";
			case C_ID:              return "ID";
			case C_FUN:             return "FUN";
			case C_NULL:            return "NULL";

			default:                return "<UNBEKANNT>";
		}
	}

	public static int numberOfTokens() { 
		return NUM_DIFF_TOKENS;
	} 
}
