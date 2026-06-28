package de.jplag.java.babylon.tokenizer.impl;

import java.io.File;
import java.lang.constant.ClassDesc;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.TokenType;
import de.jplag.java.babylon.ParserBabylon;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;

import com.google.auto.service.AutoService;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Name;
import jdk.incubator.code.CodeType;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreType;
import jdk.incubator.code.dialect.core.FunctionType;
import jdk.incubator.code.dialect.core.TupleType;
import jdk.incubator.code.dialect.core.VarType;
import jdk.incubator.code.dialect.java.ArrayType;
import jdk.incubator.code.dialect.java.ClassType;
import jdk.incubator.code.dialect.java.JavaType;
import jdk.incubator.code.dialect.java.PrimitiveType;
import jdk.incubator.code.dialect.java.TypeVariableType;
import jdk.incubator.code.dialect.java.WildcardType;

/**
 * {@link BabylonTokenizer} implementation that fully outputs all {@link Op}s as tokens without further interpretation.
 * Includes result types.
 */
public class FullTypedBabylonTokenizer extends FullBabylonTokenizer {
    private static final Logger logger = LoggerFactory.getLogger(FullTypedBabylonTokenizer.class);

    /**
     * Identifier of this tokenizer.
     */
    public static final String IDENTIFIER = "full-typed";
    private final Set<CodeType> codebaseTypes;

    protected FullTypedBabylonTokenizer(ParserBabylon parser, File file, Set<CodeType> codebaseTypes) {
        super(parser, file);
        this.codebaseTypes = codebaseTypes;
    }

    @Override
    protected TokenType getTokenType(Op op) {
        StringBuilder sb = new StringBuilder();
        if (op.parent() != null) {
            CodeType resultType = op.result().type();
            if (!resultType.equals(JavaType.VOID) && !containsCodebaseType(resultType)) {
                sb.append(resultType.externalize()).append(" = ");
            }
        }
        sb.append(op.externalizeOpName());
        return new UnknownTokenType(sb.toString());
    }

    private boolean containsCodebaseType(Iterable<? extends CodeType> codebaseTypes) {
        for (CodeType codebaseType : codebaseTypes) {
            if (containsCodebaseType(codebaseType)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsCodebaseType(CodeType codeType) {
        return switch (codeType) {
            case CoreType coreType -> containsCodebaseType(coreType);
            case JavaType javaType -> containsCodebaseType(javaType);
            default -> {
                logger.warn("Unknown code type: {}", codeType);
                yield false;
            }
        };
    }

    private boolean containsCodebaseType(CoreType coreType) {
        return switch (coreType) {
            case FunctionType functionType -> containsCodebaseType(functionType.returnType()) || containsCodebaseType(functionType.parameterTypes());
            case TupleType tupleType -> containsCodebaseType(tupleType.componentTypes());
            case VarType varType -> containsCodebaseType(varType.valueType());
        };
    }

    private boolean containsCodebaseType(JavaType javaType) {
        return switch (javaType) {
            case ArrayType arrayType -> containsCodebaseType(arrayType.componentType());
            case ClassType classType -> codebaseTypes.contains(classType.erasure()) || containsCodebaseType(classType.typeArguments());
            case PrimitiveType _ -> false;
            case TypeVariableType typeVariableType -> containsCodebaseType(typeVariableType.bound());
            case WildcardType wildcardType -> containsCodebaseType(wildcardType.boundType());
            default -> {
                logger.warn("Unknown java type: {}", javaType);
                yield false;
            }
        };
    }

    /**
     * {@link BabylonTokenizer.Provider} for {@link FullTypedBabylonTokenizer}.
     */
    @AutoService(BabylonTokenizer.Provider.class)
    public static class Provider extends FullBabylonTokenizer.Provider {
        /**
         * Create a new instance.
         */
        public Provider() {
            super(IDENTIFIER);
        }

        @Override
        public BabylonTokenizer getTokenizer(TokenizerConstructionContext context) {
            return new FullTypedBabylonTokenizer(context.parser(), context.file(), getCodebaseTypes(context.codebaseAsts()));
        }

        private Set<CodeType> getCodebaseTypes(Iterable<? extends CompilationUnitTree> codebaseAsts) {
            Set<CodeType> result = new HashSet<>();
            for (CompilationUnitTree ast : codebaseAsts) {
                ast.accept(new CodebaseTreeTypesScanner(), result);
            }
            return Collections.unmodifiableSet(result);
        }
    }

    private static class CodebaseTreeTypesScanner extends TreeScanner<Void, Set<CodeType>> {
        @Override
        public Void visitClass(ClassTree node, Set<CodeType> codeTypes) {
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) node;
            codeTypes.add(convert(classDecl.sym));
            return super.visitClass(node, codeTypes);
        }

        private JavaType convert(Symbol.ClassSymbol s) {
            Symbol.ClassSymbol enclosingClass = s.getEnclosingElement().enclClass();
            if (enclosingClass != null) {
                Name innerName = s.flatName().subName(enclosingClass.flatName().length() + 1);
                return JavaType.qualified(convert(enclosingClass), innerName.toString());
            } else {
                return JavaType.type(ClassDesc.of(s.flatName().toString()));
            }
        }
    }
}
