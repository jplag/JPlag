package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.TranslationResult
import de.fraunhofer.aisec.cpg.graph.Node
import de.fraunhofer.aisec.cpg.passes.TranslationResultPass
import de.jplag.java_cpg.transformation.GraphTransformation
import de.jplag.java_cpg.transformation.matching.CpgIsomorphismDetector
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern.Match
import java.util.*
import kotlin.reflect.KClass

/**
 * This pass handles the transformations in the pipeline of the CPG process.
 */
class TransformationPass(ctx: TranslationContext) : TranslationResultPass(ctx) {

   companion object {
       @JvmStatic
       val KClass: KClass<TransformationPass> = TransformationPass::class

       @JvmStatic
       val transformations : MutableList<GraphTransformation<*>> = ArrayList()

       @JvmStatic
       fun registerTransformation(transformation: GraphTransformation<*>) {
           transformations.add(transformation);
       }

       @JvmStatic
       fun registerTransformations(newTransformations: Array<GraphTransformation<*>>) {
           transformations.addAll(newTransformations);
       }
   }

    override fun accept(t: TranslationResult) {
        val detector = CpgIsomorphismDetector();
        for (transformation : GraphTransformation<*> in transformations) {
            detector.loadGraph(t)
            instantiate(transformation, detector)
        }
    }

    /**
     * Applies the given transformation to all the matches that the detector can find.
     * @param <T> The concrete node type of the target node/GraphTransformation/Match
     */
    private fun <T : Node?> instantiate(transformation: GraphTransformation<T>, detector: CpgIsomorphismDetector) {
        val sourcePattern = transformation.sourcePattern
        val matches: Iterator<Match<T>> = detector.getMatches(sourcePattern)

        for (match in matches) {
            transformation.apply(match)
        }

    }

    override fun cleanup() {

    }
}