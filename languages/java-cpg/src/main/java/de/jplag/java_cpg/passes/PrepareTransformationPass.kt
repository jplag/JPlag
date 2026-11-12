package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.passes.ImportResolver
import de.fraunhofer.aisec.cpg.passes.configuration.DependsOn
import de.fraunhofer.aisec.cpg.passes.configuration.ExecuteBefore
import de.jplag.java_cpg.transformation.GraphTransformation

/**
 * This pass handles the transformations that are required in the pipeline of the Tokenization process.
 */
@DependsOn(ImportResolver::class)
@ExecuteBefore(AstTransformationPass::class)
class PrepareTransformationPass(ctx: TranslationContext) : ATransformationPass(ctx) {

    override fun getPhaseSpecificTransformations(): List<GraphTransformation> {
        return passConfig<TransformationPassConfiguration>()?.transformations ?: emptyList()
    }
}
