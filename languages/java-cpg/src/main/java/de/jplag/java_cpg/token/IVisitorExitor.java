package de.jplag.java_cpg.token;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.fraunhofer.aisec.cpg.processing.IVisitable;
import de.fraunhofer.aisec.cpg.processing.IVisitor;

/**
 * This class adds exit methods to the {@link IVisitor} class. This implementation is supposed to be a close replication
 * of {@link IVisitor}.
 */
public abstract class IVisitorExitor<V extends IVisitable<V>> extends IVisitor<V> {

    public static final String EXIT_METHOD_IDENTIFIER = "exit";

    public void exit(V t) {
        try {
            Method mostSpecificExit = this.getClass().getMethod(EXIT_METHOD_IDENTIFIER, t.getClass());
            mostSpecificExit.setAccessible(true);
            mostSpecificExit.invoke(this, t);
        } catch (NoSuchMethodException e) {
            // This method is not implemented, that is okay
        } catch (InvocationTargetException | IllegalAccessException e) {
        }
    }

}