package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.TranslationResult
import de.fraunhofer.aisec.cpg.passes.TranslationResultPass
import de.jplag.java_cpg.transformation.GraphTransformation
import de.jplag.java_cpg.transformation.matching.CpgIsomorphismDetector
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern
import de.jplag.java_cpg.transformation.matching.pattern.Match
import de.jplag.java_cpg.transformation.operations.DummyNeighbor
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * A ATransformationPass is an abstract transformation pass. All transformation passes function the same way, but need
 * to be separate classes to work with the CPG pipeline.
 */
abstract class ATransformationPass(ctx: TranslationContext) : TranslationResultPass(ctx) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun accept(t: TranslationResult) {
        val detector = CpgIsomorphismDetector()
        val transformations = getPhaseSpecificTransformations()
        for (transformation: GraphTransformation in transformations) {
            detector.loadGraph(t)
            instantiate(transformation, detector)
        }

    }

    abstract fun getPhaseSpecificTransformations(): List<GraphTransformation>

    /**
     * Applies the given transformation to all the matches that the detector can find.
     * @param <T> The concrete node type of the target node/GraphTransformation/Match
     */
    private fun instantiate(transformation: GraphTransformation, detector: CpgIsomorphismDetector) {
        val sourcePattern: GraphPattern = transformation.sourcePattern

        var count = 0
        var invalidated: Boolean
        do {
            invalidated = false
            var matches: List<Match> = detector.getMatches(sourcePattern)

            if (transformation.executionOrder == GraphTransformation.ExecutionOrder.DESCENDING_LOCATION) {
                matches = matches.reversed();
            }

            for (match: Match in matches) {
                // transformations may lead to other matches being invalidated
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