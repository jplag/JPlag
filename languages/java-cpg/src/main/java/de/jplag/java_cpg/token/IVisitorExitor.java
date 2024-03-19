package de.jplag.java_cpg.token;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.processing.IVisitable;
import de.fraunhofer.aisec.cpg.processing.IVisitor;

/**
 * This class adds exit methods to the {@link IVisitor} class. This implementation is supposed to be a close replication
 * of {@link IVisitor}.
 * @param <V> the object type to visit and exit
 */
public abstract class IVisitorExitor<V extends IVisitable<V>> extends IVisitor<V> {

    private static final String EXIT_METHOD_IDENTIFIER = "exit";

    /**
     * Calls the most specific implementation of exit for the given {@link Node}.
     * @param node the node
     */
    public void exit(V node) {
        try {
            Method mostSpecificExit = this.getClass().getMethod(EXIT_METHOD_IDENTIFIER, node.getClass());
            mostSpecificExit.setAccessible(true);
            mostSpecificExit.invoke(this, node);
        } catch (NoSuchMethodException e) {
            // This method is not implemented, that is okay
        } catch (InvocationTargetException | IllegalAccessException e) {
        }
    }

}
