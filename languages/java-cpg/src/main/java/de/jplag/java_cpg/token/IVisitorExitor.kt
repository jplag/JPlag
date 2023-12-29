package de.jplag.java_cpg.token;

import de.fraunhofer.aisec.cpg.processing.IVisitable;
import de.fraunhofer.aisec.cpg.processing.IVisitor;
import java.lang.reflect.InvocationTargetException

private const val EXIT_METHOD_IDENTIFIER = "exit"

/**
 * This class adds exit methods to the [IVisitor] class.
 */
open class IVisitorExitor<V : IVisitable<V>> : IVisitor<V>() {

    open fun exit(t: V) {
        try {
            val mostSpecificExit = this.javaClass.getMethod(EXIT_METHOD_IDENTIFIER, t::class.java)
            mostSpecificExit.isAccessible = true
            mostSpecificExit.invoke(this, t)
        } catch (e: NoSuchMethodException) {
            // Nothing to do here
        } catch (e: InvocationTargetException) {} catch (e: IllegalAccessException) {}
    }

}
