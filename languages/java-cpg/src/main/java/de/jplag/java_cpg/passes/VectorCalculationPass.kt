package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.passes.order.DependsOn
import de.jplag.java_cpg.token.ACpgNodeListener
import de.jplag.java_cpg.token.CalculationCpgNodeListener

@DependsOn(CpgTransformationPass::class)
class VectorCalculationPass (ctx: TranslationContext) : AResultVisitingPass (ctx)  {


    override fun createListener(): ACpgNodeListener {
        return CalculationCpgNodeListener()
    }

    override fun cleanup() {
        // do nothing
    }


}