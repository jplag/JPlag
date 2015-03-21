package jplag.javax;

public class JavaToken extends jplag.Token implements JavaTokenConstants {
	private static final long serialVersionUID = 8090049637477580276L;
	private int line;

	public JavaToken(int type, String file, int zeile) {
		super(type, file, zeile);
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return 0;
	}

	public int getLength() {
		return 0;
	}

	public static String type2string(int type) {
		switch (type) {
	    case JavaTokenConstants.FILE_END:
	                            return "FILE_END";
	    case J_PACKAGE:         return "J_PACKAGE";
	    case J_IMPORT:          return "J_IMPORT";
	    case J_CLASS_BEGIN:     return "J_CLASS_BEGIN";
	    case J_CLASS_END:       return "J_CLASS_END";
	    case J_METHOD_BEGIN:    return "J_METHOD_BEGIN";
	    case J_METHOD_END:      return "J_METHOD_END";
	    case J_VARDEF:          return "J_VARDEF";
	    case J_SYNC_BEGIN:      return "J_SYNC_BEGIN";
	    case J_SYNC_END:        return "J_SYNC_END";
	    case J_DO_BEGIN:        return "J_DO_BEGIN";
	    case J_DO_END:          return "J_DO_END";
	    case J_WHILE_BEGIN:     return "J_WHILE_BEGIN";
	    case J_WHILE_END:       return "J_WHILE_END";
	    case J_FOR_BEGIN:       return "J_FOR_BEGIN";
	    case J_FOR_END:         return "J_FOR_END";
	    case J_SWITCH_BEGIN:    return "J_SWITCH_BEGIN";
	    case J_SWITCH_END:      return "J_SWITCH_END";
	    case J_CASE:            return "J_CASE";
	    case J_TRY_BEGIN:       return "J_TRY_BEGIN";
	    case J_CATCH_BEGIN:     return "J_CATCH_BEGIN";
	    case J_CATCH_END:       return "J_CATCH_END";
	    case J_FINALLY:         return "J_FINALLY";
	    case J_IF_BEGIN:        return "J_IF_BEGIN";
	    case J_ELSE:            return "J_ELSE";
	    case J_IF_END:          return "J_IF_END";
	    case J_COND:            return "J_COND";
	    case J_BREAK:           return "J_BREAK";
	    case J_CONTINUE:        return "J_CONTINUE";
	    case J_RETURN:          return "J_RETURN";
	    case J_THROW:           return "J_THROW";
	    case J_IN_CLASS_BEGIN:  return "J_IN_CLASS_BEGIN";
	    case J_IN_CLASS_END:    return "J_IN_CLASS_END";
	    case J_APPLY:           return "J_APPLY";
	    case J_NEWCLASS:        return "J_NEWCLASS";
	    case J_NEWARRAY:        return "J_NEWARRAY";
	    case J_ASSIGN:          return "J_ASSIGN";
	    case J_INTERFACE_BEGIN: return "J_INTERFACE_BEGIN";
	    case J_INTERFACE_END:   return "J_INTERFACE_END";
	    case J_CONSTR_BEGIN:    return "J_CONSTR_BEGIN";
	    case J_CONSTR_END:      return "J_CONSTR_END";
	    case J_INIT_BEGIN:      return "J_INIT_BEGIN";
	    case J_INIT_END:        return "J_INIT_END";
	    case J_VOID:            return "J_VOID";
	    case J_ABSTRACT:        return "J_ABSTRACT";
	    case J_FINAL:           return "J_FINAL";
	    case J_PUBLIC:          return "J_PUBLIC";
	    case J_STATIC:          return "J_STATIC";
	    case J_PROTECTED:       return "J_PROTECTED";
	    case J_PRIVATE:         return "J_PRIVATE";
	    case J_EXTENDS:         return "J_EXTENDS";
	    case J_TRANSIENT:       return "J_TRANSIENT";
	    case J_VOLANTILE:       return "J_VOLANTILE";
	    case J_ARRAY_INIT:      return "J_ARRAY_INIT";
	    case J_NATIVE:          return "J_NATIVE";
	    case J_SYNCHRONIZED:    return "J_SYNCHRONIZED";
	    case J_THROWS:          return "J_THROWS";
	    case J_THIS:            return "J_THIS";
	    case J_BOOLEAN_TYPE:    return "J_BOOLEAN_TYPE";
	    case J_CHAR_TYPE:       return "J_CHAR_TYPE";
	    case J_BYTE_TYPE:       return "J_BYTE_TYPE";
	    case J_SHORT_TYPE:      return "J_SHORT_TYPE";
	    case J_INT_TYPE:        return "J_INT_TYPE";
	    case J_LONG_TYPE:       return "J_LONG_TYPE";
	    case J_FLOAT_TYPE:      return "J_FLOAT_TYPE";
	    case J_DOUBLE_TYPE:     return "J_DOUBLE_TYPE";
	    case J_ASSIGNOP:        return "J_ASSIGNOP";
	    case J_ASSIGNBITOP:     return "J_ASSIGNBITOP";
	    case J_COND_OR:         return "J_COND_OR";
	    case J_COND_AND:        return "J_COND_AND";
	    case J_COND_IOR:        return "J_COND_IOR";
	    case J_COND_XOR:        return "J_COND_XOR";
	    case J_AND:             return "J_AND";
	    case J_EQUALITY:        return "J_EQUALITY";
	    case J_INSTANCEOF:      return "J_INSTANCEOF";
	    case J_SHIFT:           return "J_SHIFT";
	    case J_RELATIONAL:      return "J_RELATIONAL";
	    case J_ADD:             return "J_ADD";
	    case J_MULT:            return "J_MULT";
	    case J_DECINC:          return "J_DECINC";
	    case J_CAST:            return "J_CAST";
	    case J_SUPER:           return "J_SUPER";
	      //case J_LITERAL:         return "J_LITERAL";
	    case J_NULL:            return "J_NULL";
	    case J_LABEL:           return "J_LABEL";
	    case J_INT:             return "J_INT";
	    case J_FLOAT:           return "J_FLOAT";
	    case J_CHAR:            return "J_CHAR";
	    case J_STRING:          return "J_STRING";
	    case J_BOOLEAN:         return "J_BOOLEAN";
	
	    default:           return "<UNBEKANNT>";
		}
	}

	public static int numberOfTokens() {
		return NUM_DIFF_TOKENS;
	}
}
