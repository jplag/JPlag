package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.TranslationResult
import de.fraunhofer.aisec.cpg.graph.Node
import de.fraunhofer.aisec.cpg.passes.TranslationResultPass
import de.fraunhofer.aisec.cpg.passes.order.ExecuteBefore
import de.jplag.java_cpg.transformation.GraphTransformation
import de.jplag.java_cpg.transformation.matching.CpgIsomorphismDetector
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern.Match
import de.jplag.java_cpg.transformation.operations.DummyNeighbor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This pass handles the transformations in the pipeline of the CPG process.
 */
@ExecuteBefore(TokenizationPass::class)
class CleanupTransformationPass(ctx: TranslationContext) : TranslationResultPass(ctx) {

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
        val LOGGER: Logger = LoggerFactory.getLogger(CleanupTransformationPass::class.java)
    }

    override fun accept(t: TranslationResult) {
        val detector = CpgIsomorphismDetector()
        for (transformation: GraphTransformation<*> in transformations) {
            detector.loadGraph(t)
            instantiate(transformation, detector)
        }
    }

    /**
     * Applies the given transformation to all the matches that the detector can find.
     * @param <T> The concrete node type of the target node/GraphTransformation/Match
     */
    private fun <T : Node?> instantiate(transformation: GraphTransformation<T>, detector: CpgIsomorphismDetector) {
        val sourcePattern: GraphPattern<T> = transformation.sourcePattern
        val matches: Iterator<Match<T>> = detector.getMatches(sourcePattern)

        for (match in matches) {
            transformation.apply(match)
        }

    }

    override fun cleanup() {
        val DUMMY = DummyNeighbor.getInstance()
        DUMMY.nextEOGEdges.removeIf { it.end == DUMMY }
        DUMMY.prevEOGEdges.removeIf { it.start == DUMMY }

        DUMMY.nextEOGEdges.map { it.end }.toList().forEach {
            val successors = it.nextEOG.distinct()
            val predecessors = it.prevEOG.distinct()
            if (successors.size == 1 && successors[0] == DUMMY
                && predecessors.size == 1 && predecessors[0] == DUMMY
            ) {
                LOGGER.info("The node %s got isolated and will likely be removed.".format(it))
                DUMMY.nextEOGEdges.removeIf { e -> e.end == it }
                DUMMY.prevEOGEdges.removeIf { e -> e.start == it }
            }
        }
    }
}