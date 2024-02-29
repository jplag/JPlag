package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.TranslationResult
import de.fraunhofer.aisec.cpg.graph.Node
import de.fraunhofer.aisec.cpg.passes.EvaluationOrderGraphPass
import de.fraunhofer.aisec.cpg.passes.TranslationResultPass
import de.fraunhofer.aisec.cpg.passes.order.ExecuteBefore
import de.jplag.java_cpg.transformation.GraphTransformation
import de.jplag.java_cpg.transformation.matching.CpgIsomorphismDetector
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern
import de.jplag.java_cpg.transformation.matching.pattern.Match
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This pass handles the transformations in the pipeline of the CPG process.
 */
@ExecuteBefore(EvaluationOrderGraphPass::class)
class TransformationPass(ctx: TranslationContext) : TranslationResultPass(ctx) {

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

        @JvmStatic
        val LOGGER: Logger = LoggerFactory.getLogger(TransformationPass::class.java)
    }

    override fun accept(t: TranslationResult) {
        val detector = CpgIsomorphismDetector()
        for (transformation: GraphTransformation<*> in transformations) {
            detector.loadGraph(t)
            instantiate(transformation, detector, ctx)
        }
    }

    /**
     * Applies the given transformation to all the matches that the detector can find.
     * @param <T> The concrete node type of the target node/GraphTransformation/Match
     */
    private fun <T : Node?> instantiate(
        transformation: GraphTransformation<T>,
        detector: CpgIsomorphismDetector,
        ctx: TranslationContext
    ) {
        val sourcePattern: GraphPattern = transformation.sourcePattern
        val matches: Iterator<Match> = detector.getMatches(sourcePattern)

        var count = 0;
        while (matches.hasNext()) {
            val match = matches.next()
            count++;
            transformation.apply(match, ctx)
        }
        LOGGER.info("%s: Found %d matches".format(transformation.name, count))

    }

    override fun cleanup() {
    }
}