package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.graph.Node
import de.fraunhofer.aisec.cpg.passes.order.DependsOn
import de.jplag.java_cpg.token.CalculationCpgNodeListener
import de.jplag.java_cpg.token.VisitorExitor
import de.jplag.java_cpg.visitor.NodeOrderStrategy

@DependsOn(CpgTransformationPass::class)
class VectorCalculationPass (ctx: TranslationContext) : AResultVisitingPass (ctx)  {

    override val strategy = NodeOrderStrategy(true)

    override fun createListener(): VisitorExitor<Node> {
        return CalculationCpgNodeListener(doSemanticAnalysis)
    }

    override fun cleanup() {
        // do nothing
    }

    companion object {
        @JvmStatic
        var doSemanticAnalysis: Boolean = true
    }

}