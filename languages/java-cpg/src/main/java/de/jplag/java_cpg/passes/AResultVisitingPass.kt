package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.TranslationResult
import de.fraunhofer.aisec.cpg.graph.Node
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker
import de.fraunhofer.aisec.cpg.passes.TranslationResultPass
import de.jplag.java_cpg.token.VisitorExitor
import de.jplag.java_cpg.visitor.NodeOrderStrategy
/**
 * An abstract pass that visits the nodes in the [TranslationResult] according to a given
 * [NodeOrderStrategy].
 */
abstract class AResultVisitingPass (ctx: TranslationContext) : TranslationResultPass(ctx){
    protected abstract val strategy: NodeOrderStrategy

    /** Traverses the [TranslationResult] and applies the visitor/exitor created by
     * [createListener] to each node according to the [strategy].
     *
     * @param translationResult The [TranslationResult] to traverse.
     */
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
    protected open fun doBeforeTraversal() {
        //optional for subclasses after traversal, do nothing per default
    }

    /**
     * This method creates the listener with visit and exit methods that will be applied to each node during traversal.
     *
     * @return A [VisitorExitor] for nodes.
     */
    protected abstract fun createListener() : VisitorExitor<Node>
    protected open fun doAfterTraversal() {
        //optional for subclasses after traversal, do nothing per default
    }


}