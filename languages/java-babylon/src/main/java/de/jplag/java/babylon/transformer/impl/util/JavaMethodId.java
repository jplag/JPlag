package de.jplag.java.babylon.transformer.impl.util;

import javax.annotation.Nullable;

import com.sun.source.tree.MethodTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import jdk.incubator.code.CodeType;
import jdk.incubator.code.dialect.core.FunctionType;
import jdk.incubator.code.dialect.java.ClassType;
import jdk.incubator.code.dialect.java.MethodRef;

/**
 * Uniquely identifies a java method within a single compilation unit.<br>
 * Stringly typed since the contexts from which this is used use their own representations for type names and
 * signatures.
 * @param owner the owner of the method, eg the class in which it is declared
 * @param name the name of the method
 * @param signature the signature of the method, with the return type omitted (since that is not needed for a unique
 * description)
 */
public record JavaMethodId(String owner, String name, String signature) {
    /**
     * Create a new instance based on a method reference.
     * @param from the method reference
     * @return a new instance or null
     */
    public static @Nullable JavaMethodId of(MethodRef from) {
        if (!(from.refType() instanceof ClassType jt))
            return null;
        return new JavaMethodId(jt.toString(), from.name(), signature(from.signature()));
    }

    private static String signature(FunctionType functionType) {
        StringBuilder sb = new StringBuilder("(");
        boolean first = true;
        for (CodeType parameterType : functionType.parameterTypes()) {
            if (first)
                first = false;
            else
                sb.append(",");
            sb.append(parameterType);
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Create a new instance based on a method tree.
     * @param node the method tree
     * @return a new instance or null
     */
    public static @Nullable JavaMethodId of(MethodTree node) {
        return JavaMethodId.of(((JCTree.JCMethodDecl) node).sym);
    }

    /**
     * Create a new instance based on a symbol.
     * @param symbol the symbol
     * @return a new instance or null
     */
    public static @Nullable JavaMethodId of(Symbol.MethodSymbol symbol) {
        return new JavaMethodId(symbol.owner.name.toString(), symbol.name.toString(), signature(symbol));
    }

    private static String signature(Symbol.MethodSymbol symbol) {
        return "(" + symbol.type.argtypes((symbol.flags() & Flags.VARARGS) != 0) + ")";
    }
}
