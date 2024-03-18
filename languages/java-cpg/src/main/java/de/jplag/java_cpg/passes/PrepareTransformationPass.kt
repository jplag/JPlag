package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.passes.ImportResolver
import de.fraunhofer.aisec.cpg.passes.order.DependsOn
import de.fraunhofer.aisec.cpg.passes.order.ExecuteBefore
import de.jplag.java_cpg.transformation.GraphTransformation

/**
 * This pass handles the transformations that are required in the pipeline of the Tokenization process.
 */
@DependsOn(ImportResolver::class)
@ExecuteBefore(AstTransformationPass::class)
class PrepareTransformationPass(ctx: TranslationContext) : ATransformationPass(ctx) {

    override fun getPhaseSpecificTransformations(): List<GraphTransformation<*>> {
        return transformations.toList();
    }

    companion object {
        @JvmStatic
        val transformations: MutableList<GraphTransformation<*>> = ArrayList()

        @JvmStatic
        fun registerTransformation(transformation: GraphTransformation<*>) {
            transformations.add(transformation)
        }

        @JvmStatic
        fun registerTransformations(newTransformations: Array<GraphTransformation<*>>) {
            transformations.addAll(newTransformations)
        }

        @JvmStatic
        fun clearTransformations() {
            transformations.clear()
        }
    }

}