package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.passes.PassConfiguration
import de.jplag.java_cpg.transformation.GraphTransformation

/**
 * Immutable selection of transformations for one pass of one translation.
 * @param transformations transformations to copy in execution order
 */
class TransformationPassConfiguration(transformations: Collection<GraphTransformation>) : PassConfiguration() {
    val transformations: List<GraphTransformation> = java.util.List.copyOf(transformations)
}
