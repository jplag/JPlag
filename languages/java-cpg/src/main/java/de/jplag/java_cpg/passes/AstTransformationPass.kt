package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.passes.EvaluationOrderGraphPass
import de.fraunhofer.aisec.cpg.passes.ImportResolver
import de.fraunhofer.aisec.cpg.passes.configuration.DependsOn
import de.fraunhofer.aisec.cpg.passes.configuration.ExecuteBefore
import de.jplag.java_cpg.transformation.GraphTransformation

/**
 * This pass handles the transformations working on the AST, i.e. before the EOG or DFG are built.
 */
@DependsOn(ImportResolver::class)
@ExecuteBefore(EvaluationOrderGraphPass::class)
class AstTransformationPass(ctx: TranslationContext) : ATransformationPass(ctx) {

    override fun getPhaseSpecificTransformations(): List<GraphTransformation> {
        return passConfig<TransformationPassConfiguration>()?.transformations ?: emptyList()
    }
}
