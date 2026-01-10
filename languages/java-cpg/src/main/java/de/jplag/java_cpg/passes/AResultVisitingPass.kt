package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.TranslationResult
import de.fraunhofer.aisec.cpg.graph.Node
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker
import de.fraunhofer.aisec.cpg.passes.TranslationResultPass
import de.jplag.java_cpg.token.VisitorExitor
import de.jplag.java_cpg.visitor.NodeOrderStrategy

abstract class AResultVisitingPass (ctx: TranslationContext) : TranslationResultPass(ctx){
    protected abstract val strategy: NodeOrderStrategy

    final override fun accept(translationResult: TranslationResult) {

        doBeforeTraversal()

        val walker = SubgraphWalker.IterativeGraphWalker()
        val listener = createListener()
        walker.strategy = { strategy.getIterator(it) }
        walker.registerOnNodeVisit { listener.visit(it) }
        walker.registerOnNodeExit { listener.exit(it) }
        walker.iterate(translationResult)

        doAfterTraversal()

    }

    protected open fun doBeforeTraversal() {}
    protected abstract fun createListener() : VisitorExitor<Node>
    protected open fun doAfterTraversal() {}


}