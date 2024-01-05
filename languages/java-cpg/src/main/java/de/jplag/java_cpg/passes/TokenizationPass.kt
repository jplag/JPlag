package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.TranslationResult
import de.fraunhofer.aisec.cpg.graph.Node
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

/**
 * This pass tokenizes a TranslationResult.
 */
class TokenizationPass(ctx: TranslationContext?) : TranslationResultPass(ctx!!) {

    private val tokenList = ArrayList<Token>()
    private val consumer: CpgTokenConsumer

    init {
        this.consumer = ConcreteCpgTokenConsumer()
    }

    override fun cleanup() {
        LoggerFactory.getLogger(TokenizationPass::class.java).info("Found %d tokens".format(tokenList.size))
    }

    override fun accept(translationResult: TranslationResult) {
        val listener = CpgTokenListener(consumer)
        val walker: SubgraphWalker.IterativeGraphWalker = SubgraphWalker.IterativeGraphWalker()
        walker.strategy = { node: Node? -> NodeOrderStrategy().getIterator(node) }
        walker.registerOnNodeVisit(Consumer { node: Node? -> listener.visit(node) })
        walker.registerOnNodeExit(Consumer { t: Node -> listener.exit(t) })
        walker.iterate(translationResult)
        callback!!.accept(tokenList)
    }

    private inner class ConcreteCpgTokenConsumer : CpgTokenConsumer() {
        override fun addToken(type: TokenType, file: File, rowBegin: Int, colBegin: Int, length: Int) {
            val token = CpgToken(type, file, rowBegin, colBegin, length)
            tokenList.add(token)
        }
    }

    companion object {
        var callback: Consumer<List<Token>>? = null
    }
}
