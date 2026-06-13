package de.jplag.java.babylon.tokenizer.impl;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Token;
import de.jplag.java.JavaTokenType;
import de.jplag.java.TokenGeneratingTreeScanner;
import de.jplag.java.babylon.ParserBabylon;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableAccessType;
import de.jplag.semantics.VariableScope;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.java.ArrayType;
import jdk.incubator.code.dialect.java.ClassType;
import jdk.incubator.code.dialect.java.JavaOp;

/**
 * {@link BabylonTokenizer} implementation intended for high-level code models.<br>
 * Deliberately ignores smaller {@link Op}s, aiming for a similar abstraction level to
 * {@link TokenGeneratingTreeScanner}.
 */
@AutoService(BabylonTokenizer.class)
public class HighLevelBabylonTokenizer extends AbstractBabylonTokenizer {
    /**
     * Identifier of this tokenizer.
     */
    public static final String IDENTIFIER = "high-level";

    protected static final Logger logger = LoggerFactory.getLogger(HighLevelBabylonTokenizer.class);

    /**
     * Create a new instance.
     */
    public HighLevelBabylonTokenizer() {
        super(IDENTIFIER);
    }

    @Override
    public BabylonTokenizer.AtFile atFile(ParserBabylon parser, File file) {
        return new AtFile(parser, file);
    }

    /**
     * Tokenizer bound to a particular file, defaulting to that file for output {@link Token}s.
     */
    public static class AtFile extends AbstractBabylonTokenizer.AtFile {
        /**
         * Create a new instance.
         * @param parser the parser to output to
         * @param file the current file
         */
        public AtFile(ParserBabylon parser, File file) {
            super(parser, file);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(Op op) {
            switch (op) {
                case CoreOp.FuncOp func -> {
                    addToken(JavaTokenType.J_METHOD_BEGIN, func.location(), CodeSemantics.createControl());
                    handle(func.body());
                    addToken(JavaTokenType.J_METHOD_END, CodeSemantics.createControl());
                }
                case JavaOp.LambdaOp lambda -> {
                    addToken(JavaTokenType.J_METHOD_BEGIN, lambda.location(), CodeSemantics.createControl());
                    handle(lambda.body());
                    addToken(JavaTokenType.J_METHOD_END, CodeSemantics.createControl());
                }
                case JavaOp.SynchronizedOp sync -> {
                    addToken(JavaTokenType.J_SYNC_BEGIN, sync.location(), CodeSemantics.createControl());
                    handle(sync.blockBody());
                    addToken(JavaTokenType.J_SYNC_END, CodeSemantics.createControl());
                }
                case JavaOp.DoWhileOp doWhileOp -> {
                    addToken(JavaTokenType.J_LOOP_BEGIN, doWhileOp.location(), CodeSemantics.createLoopBegin());
                    handle(doWhileOp.loopBody());
                    addToken(JavaTokenType.J_LOOP_END, CodeSemantics.createLoopEnd());
                    handle(doWhileOp.predicateBody());
                }
                case JavaOp.WhileOp whileOp -> {
                    addToken(JavaTokenType.J_LOOP_BEGIN, whileOp.location(), CodeSemantics.createLoopBegin());
                    for (Body body : whileOp.bodies())
                        handle(body);
                    addToken(JavaTokenType.J_LOOP_END, CodeSemantics.createLoopEnd());
                }
                case JavaOp.ForOp forOp -> {
                    parser.getVariableRegistry().enterLocalScope();
                    addToken(JavaTokenType.J_LOOP_BEGIN, forOp.location(), CodeSemantics.createLoopBegin());
                    for (Body body : forOp.bodies())
                        handle(body);
                    addToken(JavaTokenType.J_LOOP_END, CodeSemantics.createLoopEnd());
                    parser.getVariableRegistry().exitLocalScope();
                }
                case JavaOp.EnhancedForOp enhancedForOp -> {
                    parser.getVariableRegistry().enterLocalScope();
                    addToken(JavaTokenType.J_LOOP_BEGIN, enhancedForOp.location(), CodeSemantics.createLoopBegin());
                    for (Body body : enhancedForOp.bodies())
                        handle(body);
                    addToken(JavaTokenType.J_LOOP_END, CodeSemantics.createLoopEnd());
                    parser.getVariableRegistry().exitLocalScope();
                }
                case JavaOp.JavaSwitchOp javaSwitchOp -> {
                    addToken(JavaTokenType.J_SWITCH_BEGIN, javaSwitchOp.location(), CodeSemantics.createControl());
                    for (Iterator<Body> iterator = javaSwitchOp.bodies().iterator(); iterator.hasNext();) {
                        Body predicateBody = iterator.next();
                        Body actionBody = iterator.next();
                        addToken(JavaTokenType.J_CASE, location(predicateBody), CodeSemantics.createControl());
                        handle(predicateBody);
                        handle(actionBody);
                    }
                    addToken(JavaTokenType.J_SWITCH_END, CodeSemantics.createControl());
                }
                case JavaOp.TryOp tryOp -> {
                    addToken(JavaTokenType.J_TRY_BEGIN, tryOp.location(), CodeSemantics.createControl());
                    for (Body resourceBody : tryOp.resourceBodies())
                        handle(resourceBody);
                    handle(tryOp.body());
                    for (Body catchBody : tryOp.catchBodies()) {
                        parser.getVariableRegistry().enterLocalScope();
                        addToken(JavaTokenType.J_CATCH_BEGIN, location(catchBody), CodeSemantics.createControl());
                        handle(catchBody);
                        addToken(JavaTokenType.J_CATCH_END, CodeSemantics.createControl());
                        parser.getVariableRegistry().exitLocalScope();
                    }
                    Body fin = tryOp.finallyBody();
                    if (fin != null) {
                        addToken(JavaTokenType.J_FINALLY_BEGIN, location(fin), CodeSemantics.createControl());
                        handle(fin);
                        addToken(JavaTokenType.J_FINALLY_END, CodeSemantics.createControl());
                    }
                    addToken(JavaTokenType.J_TRY_END, CodeSemantics.createControl());
                }
                case JavaOp.IfOp ifOp -> {
                    for (Iterator<Body> iterator = ifOp.bodies().iterator(); iterator.hasNext();) {
                        Body body = iterator.next();
                        // if there is just one body left, it is an else branch
                        // if there are multiple, it is else-if
                        if (iterator.hasNext()) {
                            addToken(JavaTokenType.J_IF_BEGIN, ifOp.location(), CodeSemantics.createControl());
                            handle(body);
                            handle(iterator.next());
                            addToken(JavaTokenType.J_IF_END, CodeSemantics.createControl());
                        } else {
                            // if this is an implicit else branch (ie it consists only of yield),
                            // do not emit a new IF{, }IF pair
                            if (body.blocks().size() == 1) {
                                List<Op> ops = body.blocks().getFirst().children();
                                if (ops.size() == 1 && ops.getFirst() instanceof CoreOp.YieldOp) {
                                    break;
                                }
                            }

                            addToken(JavaTokenType.J_IF_BEGIN, ifOp.location(), CodeSemantics.createControl());
                            handle(body);
                            addToken(JavaTokenType.J_IF_END, CodeSemantics.createControl());
                        }
                    }
                }
                case JavaOp.BreakOp breakOp -> addToken(JavaTokenType.J_BREAK, breakOp.location(), CodeSemantics.createControl());
                case JavaOp.ContinueOp continueOp -> addToken(JavaTokenType.J_CONTINUE, continueOp.location(), CodeSemantics.createControl());
                case CoreOp.ReturnOp returnOp -> addToken(JavaTokenType.J_RETURN, returnOp.location(), CodeSemantics.createControl());
                case JavaOp.ThrowOp throwOp -> addToken(JavaTokenType.J_THROW, throwOp.location(), CodeSemantics.createControl());
                case JavaOp.NewOp newOp -> {
                    switch (newOp.resultType()) {
                        case ArrayType _:
                            addToken(JavaTokenType.J_NEWARRAY, newOp.location(), new CodeSemantics());
                            break;
                        case ClassType ct when !ct.typeArguments().isEmpty():
                            addToken(JavaTokenType.J_GENERIC, newOp.location(), new CodeSemantics());
                            // fall-through
                        default:
                            addToken(JavaTokenType.J_NEWCLASS, newOp.location(), new CodeSemantics());
                            break;
                    }
                }
                case CoreOp.VarOp varOp -> {
                    String name = varOp.varName();
                    boolean inLocalScope = parser.getVariableRegistry().inLocalScope();
                    // this presents a problem when classes are declared in local scopes, which can happen in ad-hoc implementations
                    CodeSemantics semantics;
                    if (inLocalScope) {
                        boolean mutable = true; // final does not seem to get persisted into the code model
                        parser.getVariableRegistry().registerVariable(name, VariableScope.LOCAL, mutable);
                        semantics = new CodeSemantics();
                    } else {
                        semantics = CodeSemantics.createKeep();
                    }
                    addToken(JavaTokenType.J_VARDEF, location(varOp), semantics);
                    if (!varOp.isUninitialized() && !(varOp.operands().getFirst() instanceof Block.Parameter)) {
                        // manually add variable to semantics since identifier isn't visited
                        parser.getVariableRegistry().setNextVariableAccessType(VariableAccessType.WRITE);
                        parser.getVariableRegistry().registerVariableAccess(name, !inLocalScope);
                        addToken(JavaTokenType.J_ASSIGN, location(varOp), new CodeSemantics());
                    }
                }
                case CoreOp.VarAccessOp.VarStoreOp varStoreOp -> {
                    boolean inLocalScope = parser.getVariableRegistry().inLocalScope();
                    parser.getVariableRegistry().setNextVariableAccessType(VariableAccessType.WRITE);
                    parser.getVariableRegistry().registerVariableAccess(name(varStoreOp.varOperand()), !inLocalScope);
                    addToken(JavaTokenType.J_ASSIGN, location(varStoreOp), new CodeSemantics());
                }
                case JavaOp.FieldAccessOp.FieldStoreOp fieldStoreOp -> {
                    parser.getVariableRegistry().setNextVariableAccessType(VariableAccessType.WRITE);
                    parser.getVariableRegistry().registerVariableAccess(fieldStoreOp.fieldReference().name(), true);
                    addToken(JavaTokenType.J_ASSIGN, location(fieldStoreOp), new CodeSemantics());
                }
                case JavaOp.ArrayAccessOp.ArrayStoreOp arrayStoreOp -> {
                    parser.getVariableRegistry().setNextVariableAccessType(VariableAccessType.WRITE);
                    parser.getVariableRegistry().registerVariableAccess(name(arrayStoreOp.arrayOperand()), true);
                    addToken(JavaTokenType.J_ASSIGN, location(arrayStoreOp), new CodeSemantics());
                }
                case JavaOp.AssertOp assertOp -> addToken(JavaTokenType.J_ASSERT, location(assertOp), CodeSemantics.createControl());
                case JavaOp.ConditionalExpressionOp conditionalOp -> {
                    addToken(JavaTokenType.J_COND, conditionalOp.location(), new CodeSemantics());
                    for (Body body : conditionalOp.bodies())
                        handle(body);
                }
                case JavaOp.InvokeOp invokeOp -> {
                    boolean hasNoArguments = invokeOp.operands().size() == 1 && invokeOp.hasReceiver();
                    boolean isImplicitSuper = hasNoArguments && invokeOp.invokeReference().name().equals("<init>");
                    if (!isImplicitSuper)
                        addToken(JavaTokenType.J_APPLY, location(invokeOp), CodeSemantics.createControl());
                }
                case CoreOp.FuncCallOp funcCallOp -> addToken(JavaTokenType.J_APPLY, location(funcCallOp), CodeSemantics.createControl());
                case JavaOp.YieldOp yieldOp -> addToken(JavaTokenType.J_YIELD, yieldOp.location(), CodeSemantics.createControl());
                case CoreOp.ModuleOp moduleOp -> {
                    parser.getVariableRegistry().enterClass();
                    addToken(JavaTokenType.J_CLASS_BEGIN, moduleOp.location(), CodeSemantics.createControl());
                    for (Body body : moduleOp.bodies())
                        handle(body);
                    addToken(JavaTokenType.J_CLASS_END, moduleOp.location(), CodeSemantics.createControl());
                    parser.getVariableRegistry().exitClass();
                }
                default -> {
                    logger.debug("Unsupported op: {} with content: {}", op.getClass(), op.toText());
                    for (Body body : op.bodies())
                        handle(body);
                }
            }
        }
    }
}
