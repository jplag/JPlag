package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.TranslationResult
import de.fraunhofer.aisec.cpg.graph.Node
import de.fraunhofer.aisec.cpg.passes.TranslationResultPass
import de.fraunhofer.aisec.cpg.passes.order.ExecuteBefore
import de.jplag.java_cpg.transformation.GraphTransformation
import de.jplag.java_cpg.transformation.matching.CpgIsomorphismDetector
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern
import de.jplag.java_cpg.transformation.matching.pattern.Match
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
        val logger: Logger = LoggerFactory.getLogger(CleanupTransformationPass::class.java)
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
        val sourcePattern: GraphPattern = transformation.sourcePattern

        var count = 0
        var invalidated: Boolean
        do {
            invalidated = false
            val matches: Iterator<Match> = detector.getMatches(sourcePattern)

            while (matches.hasNext()) {
                val match = matches.next()
                if (detector.validateMatch(match, sourcePattern)) {
                    count++
                    transformation.apply(match, ctx)
                } else {
                    invalidated = true
                }
            }
        } while (invalidated)

        logger.info("%s: Found %d matches".format(transformation.name, count))
    }

    override fun cleanup() {
        val dummy = DummyNeighbor.getInstance()
        dummy.nextEOGEdges.removeIf { it.end == dummy }
        dummy.prevEOGEdges.removeIf { it.start == dummy }

        dummy.nextEOGEdges.map { it.end }.toList().forEach {
            val successors = it.nextEOG.distinct()
            val predecessors = it.prevEOG.distinct()
            if (successors.size == 1 && successors[0] == dummy
                && predecessors.size == 1 && predecessors[0] == dummy
            ) {
                logger.debug("The node %s got isolated and will likely be removed.".format(it))
                dummy.nextEOGEdges.removeIf { e -> e.end == it }
                dummy.prevEOGEdges.removeIf { e -> e.start == it }
            }
        }
    }
}