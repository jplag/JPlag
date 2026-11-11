package de.jplag.java_cpg.passes

import de.jplag.java_cpg.transformation.GraphTransformation
import de.jplag.java_cpg.transformation.operations.GraphMutationContext

/**
 * Immutable selection of transformations and an execution-state reference for one translation.
 * @param transformations transformations to copy in execution order
 * @param mutationContext execution state belonging to this translation
 */
class TransformationPassConfiguration(
    transformations: Collection<GraphTransformation>,
    mutationContext: GraphMutationContext
) : GraphMutationPassConfiguration(mutationContext) {
    val transformations: List<GraphTransformation> = java.util.List.copyOf(transformations)
}
