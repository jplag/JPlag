package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.TranslationResult
import de.fraunhofer.aisec.cpg.graph.Name
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker
import de.fraunhofer.aisec.cpg.passes.TranslationResultPass
import de.fraunhofer.aisec.cpg.passes.order.DependsOn
import de.jplag.Token
import de.jplag.TokenType
import de.jplag.java_cpg.token.CpgNodeListener
import de.jplag.java_cpg.token.CpgToken
import de.jplag.java_cpg.token.CpgTokenConsumer
import de.jplag.java_cpg.visitor.NodeOrderStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.function.Consumer

/**
 * This pass tokenizes the [TranslationResult].
 */
@DependsOn(CpgTransformationPass::class)
class TokenizationPass(ctx: TranslationContext) : TranslationResultPass(ctx) {

    private val tokenList = ArrayList<Token>()
    private val consumer: CpgTokenConsumer

    init {
        this.consumer = ConcreteCpgTokenConsumer()
    }

    override fun cleanup() {
        logger.info("Found %d tokens".format(tokenList.size))
    }

    private val strategy = NodeOrderStrategy()

    override fun accept(translationResult: TranslationResult) {
        tokenList.clear()
        val listener = CpgNodeListener(consumer)
        val walker: SubgraphWalker.IterativeGraphWalker = SubgraphWalker.IterativeGraphWalker()
        walker.strategy = { strategy.getIterator(it) }
        walker.registerOnNodeVisit { listener.visit(it) }
        walker.registerOnNodeExit { listener.exit(it) }
        walker.iterate(translationResult)
        callback!!.accept(tokenList)
    }

    private inner class ConcreteCpgTokenConsumer : CpgTokenConsumer() {
        override fun addToken(
            type: TokenType,
            file: File,
            rowBegin: Int,
            colBegin: Int,
            length: Int,
            name: Name
        ) {
            val token = CpgToken(type, file, rowBegin, colBegin, length, name)
            tokenList.add(token)
        }
    }

    companion object {
        var callback: Consumer<List<Token>>? = null
        val logger: Logger = LoggerFactory.getLogger(TokenizationPass::class.java)
    }
}
