package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.passes.configuration.DependsOn
import de.fraunhofer.aisec.cpg.passes.configuration.ExecuteBefore
import de.jplag.java_cpg.transformation.GraphTransformation

/**
 * This pass handles the transformations working on the CPG.
 */
@DependsOn(FixAstPass::class)
@ExecuteBefore(TokenizationPass::class)
class CpgTransformationPass(ctx: TranslationContext) : ATransformationPass(ctx) {

    override fun getPhaseSpecificTransformations(): List<GraphTransformation> {
        return passConfig<TransformationPassConfiguration>()?.transformations ?: emptyList()
    }
}
