package de.jplag.java_cpg.ai.variables.objects;

import java.util.List;
import java.util.Map;

import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.VariableName;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.JavaObject;
import de.jplag.java_cpg.ai.variables.values.string.IStringValue;

/**
 * Representation of the static java.lang.Boolean class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Boolean.html">Oracle Docs</a>
 */
public class Boolean extends JavaObject implements ISpecialObject {

    private static final java.lang.String PATH = "java.lang";
    private static final java.lang.String NAME = "Boolean";

    /**
     * Representation of the static java.lang.Boolean class.
     */
    public Boolean() {
        super(new Type(Type.TypeEnum.OBJECT, getName().toString()));
    }

    /**
     * @return The variable name representing java.lang.Boolean.
     */
    @NotNull
    @Pure
    public static VariableName getName() {
        return new VariableName(PATH + "." + NAME);
    }

    @Override
    public IValue callMethod(@NotNull java.lang.String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "parseBoolean" -> {
                assert paramVars.size() == 1;
                IValue value = paramVars.getFirst();
                switch (value) {
                    case IStringValue str -> {
                        return str.callMethod("parseBoolean", paramVars, null, expectedType);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + value);
                }
            }
            default -> throw new UnsupportedOperationException(methodName);
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new Boolean();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        assert other instanceof Boolean;
        // nothing to merge
    }

}
