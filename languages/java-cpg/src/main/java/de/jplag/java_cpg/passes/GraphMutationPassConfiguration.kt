package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.passes.PassConfiguration
import de.jplag.java_cpg.transformation.operations.GraphMutationContext

/**
 * Supplies the execution state shared by the sequential passes of one translation.
 */
open class GraphMutationPassConfiguration(val mutationContext: GraphMutationContext) : PassConfiguration()
