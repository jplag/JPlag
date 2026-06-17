package de.jplag.java.babylon.transformer.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import javax.annotation.Nullable;

import de.jplag.java.babylon.CodeModelCreator;
import de.jplag.java.babylon.transformer.Prepass;
import de.jplag.java.babylon.transformer.TransformationPipeline;

import com.google.auto.service.AutoService;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.CodeType;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.core.FunctionType;
import jdk.incubator.code.dialect.core.Inliner;
import jdk.incubator.code.dialect.java.ClassType;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.MethodRef;
import jdk.incubator.code.dialect.java.PrimitiveType;

/**
 * A {@link TransformationPipeline.Step} that inlines simple methods.
 */
@AutoService(TransformationPipeline.Step.class)
public class InliningStep implements TransformationPipeline.Step<InliningStep.Context> {
    /**
     * Identifier of this pipeline step.
     */
    public static final String IDENTIFIER = "inline";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Nullable
    @Override
    public Prepass<Context> beginPrepass(TransformationPipeline.PrepassConstructionContext context) {
        return new FindCandidates(context.codeModelCreator(), this::heuristic);
    }

    @Override
    public CoreOp.FuncOp apply(CoreOp.FuncOp op, Context context) {
        return op.transform(new Apply(context));
    }

    private static class FindCandidates extends TreeScanner<Void, Void> implements Prepass<Context> {
        private final Map<String, CoreOp.FuncOp> core = new HashMap<>();
        private final Map<JavaMethodId, CoreOp.FuncOp> java = new HashMap<>();
        private final CodeModelCreator codeModelCreator;
        private final BiPredicate<CoreOp.FuncOp, JavaMethodId> heuristic;

        public FindCandidates(CodeModelCreator codeModelCreator, BiPredicate<CoreOp.FuncOp, JavaMethodId> heuristic) {
            this.codeModelCreator = codeModelCreator;
            this.heuristic = heuristic;
        }

        @Override
        public Context finalizeContext() {
            return new Context(Collections.unmodifiableMap(core), Collections.unmodifiableMap(java));
        }

        @Override
        public Void visitMethod(MethodTree node, Void unused) {
            JavaMethodId id = JavaMethodId.of(((JCTree.JCMethodDecl) node).sym);
            if (id != null) {
                CoreOp.FuncOp op = codeModelCreator.toFunc(node).orElse(null);
                if (op != null && heuristic.test(op, id))
                    java.put(id, op);
            }
            return super.visitMethod(node, unused);
        }
    }

    private static class Apply implements CodeTransformer {
        private final Context context;

        public Apply(Context context) {
            this.context = context;
        }

        @Override
        public Block.Builder acceptOp(Block.Builder builder, Op op) {
            CoreOp.FuncOp candidate = switch (op) {
                case JavaOp.InvokeOp invokeOp -> context.javaCandidates().get(JavaMethodId.of(invokeOp.invokeReference()));
                case CoreOp.FuncCallOp funcCallOp -> context.coreCandidates().get(funcCallOp.funcName());
                default -> null;
            };
            if (candidate == null) {
                builder.add(op);
            } else if (op.resultType() == PrimitiveType.VOID) {
                Inliner.inline(builder, candidate, builder.context().getValues(op.operands()), (_, _) -> {
                });
            } else {
                Op.Result variable = builder.add(CoreOp.var(candidate.resultType()));
                Inliner.inline(builder, candidate, builder.context().getValues(op.operands()), (b, value) -> {
                    b.add(CoreOp.varStore(variable, value));
                });
                Op.Result load = builder.add(CoreOp.varLoad(variable));
                builder.context().mapValue(op.result(), load);
            }
            return builder;
        }
    }

    /**
     * Contains discovered inlining candidates.
     * @param coreCandidates candidates for inlining from {@link CoreOp.FuncCallOp}
     * @param javaCandidates candidates for inlining from {@link JavaOp.InvokeOp}
     */
    public record Context(Map<String, CoreOp.FuncOp> coreCandidates, Map<JavaMethodId, CoreOp.FuncOp> javaCandidates) {
    }

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

    protected boolean heuristic(CoreOp.FuncOp func, JavaMethodId methodId) {
        return complexity(func) < 10;
    }

    protected long complexity(Op op) {
        long result = 1;
        for (Body body : op.bodies()) {
            for (Block block : body.blocks()) {
                for (Op op1 : block.ops()) {
                    result += complexity(op1);
                }
            }
        }
        return result;
    }
}
