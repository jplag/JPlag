package de.jplag.java_cpg.ai.variables.values;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.jplag.java_cpg.ai.AbstractInterpretation;
import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.Variable;

/**
 * Interface for Java object representations in the abstract interpretation.
 * @author ujiqk
 * @version 1.0
 */
public interface IJavaObject extends IValue {

    /**
     * Calls a method on this object. The method is performed inside the abstract interpretation of this object.
     * @param methodName the name of the method to call.
     * @param paramVars the parameters to pass to the method.
     * @param method the cpg method declaration node.
     * @param expectedType the expected return type of the method; used for type checking and to determine the return type
     * of this method.
     * @return the return value of the method.
     */
    IValue callMethod(@NotNull String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType);

    /**
     * Accesses a field of this object.
     * @param fieldName the name of the field to access.
     * @param expectedType the expected type of the field; used for type checking and to determine the return type of this
     * method.
     * @return the value of the field.
     */
    IValue accessField(@NotNull String fieldName, @NotNull Type expectedType);

    /**
     * Changes a field of this object.
     * @param fieldName the name of the field to change.
     * @param value the new value of the field.
     */
    void changeField(@NotNull String fieldName, IValue value);

    /**
     * Sets the variable representing this object field.
     * @param field the variable representing this object field.
     */
    void setField(@NotNull Variable field);

    /**
     * Sets the abstract interpretation this object belongs to.
     * @param abstractInterpretation the abstract interpretation this object belongs to.
     */
    void setAbstractInterpretation(@Nullable AbstractInterpretation abstractInterpretation);

    /**
     * @return true if this object has an abstract interpretation assigned.
     */
    boolean hasAbstractInterpretation();

    /**
     * @return whether the object is known to be null.
     */
    boolean isNull();

}
