package de.jplag.java_cpg.passes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.TranslationResult;
import de.fraunhofer.aisec.cpg.graph.Name;
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker;
import de.fraunhofer.aisec.cpg.passes.TranslationResultPass;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.java_cpg.token.CpgNodeListener;
import de.jplag.java_cpg.token.CpgToken;
import de.jplag.java_cpg.token.CpgTokenConsumer;
import de.jplag.java_cpg.visitor.NodeOrderStrategy;

/**
 * This pass tokenizes the {@link de.fraunhofer.aisec.cpg.TranslationResult}. It is a duplicate of
 * de.jplag.java_cpg.passes.TokenizationPass implemented in Java as a workaround to make JavaDoc work. If the package
 * 'passes' contains only Kotlin files, JavaDoc fails.
 */
public class JTokenizationPass extends TranslationResultPass {

    private static final Logger logger = LoggerFactory.getLogger(JTokenizationPass.class);
    private final ArrayList<Token> tokenList = new ArrayList<>();
    private final CpgTokenConsumer consumer = new ConcreteCpgTokenConsumer();
    private final NodeOrderStrategy strategy = new NodeOrderStrategy();
    Consumer<List<Token>> callback = null;

    /**
     * <p>
     * Constructor for JTokenizationPass.
     * </p>
     * @param ctx a {@link de.fraunhofer.aisec.cpg.TranslationContext} object
     */
    public JTokenizationPass(@NotNull TranslationContext ctx) {
        super(ctx);
    }

    /** {@inheritDoc} */
    @Override
    public void accept(TranslationResult translationResult) {
        tokenList.clear();
        CpgNodeListener listener = new CpgNodeListener(consumer);
        SubgraphWalker.IterativeGraphWalker walker = new SubgraphWalker.IterativeGraphWalker();
        walker.setStrategy(strategy::getIterator);
        walker.registerOnNodeVisit(listener::visit);
        walker.registerOnNodeExit(listener::exit);
        walker.iterate(translationResult);
        callback.accept(tokenList);
    }

    /**
     * <p>
     * cleanup.
     * </p>
     */
    public void cleanup() {
        logger.info("Found {} tokens", tokenList.size());
    }

    private class ConcreteCpgTokenConsumer extends CpgTokenConsumer {
        public void addToken(TokenType type, File file, int rowBegin, int colBegin, int length, Name name) {
            CpgToken token = new CpgToken(type, file, rowBegin, colBegin, length, name);
            tokenList.add(token);
        }
    }

}
