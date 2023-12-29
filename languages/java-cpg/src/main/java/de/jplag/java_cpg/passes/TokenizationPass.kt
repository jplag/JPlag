package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.TranslationResult
import de.fraunhofer.aisec.cpg.helpers.MeasurementHolder
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker
import de.fraunhofer.aisec.cpg.passes.TranslationResultPass
import de.jplag.Token
import de.jplag.TokenType
import de.jplag.java_cpg.token.CpgToken
import de.jplag.java_cpg.token.CpgTokenConsumer
import de.jplag.java_cpg.token.CpgTokenListener
import de.jplag.java_cpg.visitorStrategy.NodeOrderStrategy
import org.slf4j.LoggerFactory
import java.io.File
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * This pass tokenizes a TranslationResult.
 */
class TokenizationPass(ctx: TranslationContext) : TranslationResultPass(ctx), CpgTokenConsumer {

    var tokenList: MutableList<Token> = ArrayList()
        private set

    override fun cleanup() {
        LoggerFactory.getLogger(MeasurementHolder::class.java).info("Found %d tokens".format(tokenList.size))
    }

    override fun accept(translationResult: TranslationResult) {
        val listener = CpgTokenListener(this)
        val walker = SubgraphWalker.IterativeGraphWalker()
        walker.strategy = NodeOrderStrategy()::getIterator
        walker.registerOnNodeVisit(listener::visit)
        walker.registerOnNodeExit(listener::exit)
        walker.iterate(translationResult)
        callback?.accept(tokenList);
    }

    companion object {
        @JvmStatic
        val KClass: KClass<TokenizationPass> = TokenizationPass::class

        @JvmStatic
        var callback: Consumer<List<Token>>? = null;
    }

    override fun addToken(type: TokenType, file: File, rowBegin: Int, colBegin: Int, length: Int) {
        val token = CpgToken(type, file, rowBegin, colBegin, length)
        tokenList.add(token)
    }

}
