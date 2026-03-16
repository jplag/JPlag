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
import de.jplag.java_cpg.ai.variables.values.Value;

/**
 * Representation of the static java.util.Random class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Random.html">Oracle Docs</a>
 */
public class Random extends JavaObject implements ISpecialObject {

    private static final java.lang.String PATH = "java.util";
    private static final java.lang.String NAME = "Random";

    /**
     * Representation of the java.util.Random class.
     */
    public Random() {
        super(new Type(Type.TypeEnum.OBJECT, getName().toString()));
    }

    /**
     * @return The variable name representing java.util.Random.
     */
    @Pure
    @NotNull
    public static VariableName getName() {
        return new VariableName(PATH + "." + NAME);
    }

    @Override
    public IValue callMethod(@NotNull java.lang.String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "nextInt" -> {
                if (paramVars == null || paramVars.isEmpty()) {
                    return Value.valueFactory(new Type(Type.TypeEnum.INT));
                }
                assert paramVars.size() == 1;
                return Value.valueFactory(Value.valueFactory(0), paramVars.getFirst());
            }
            default -> throw new UnsupportedOperationException(methodName + " is not supported in " + PATH + "." + NAME);
        }
    }

    @Override
    public Value accessField(@NotNull java.lang.String fieldName, @NotNull Type expectedType) {
        switch (fieldName) {
            default -> throw new UnsupportedOperationException("Field " + fieldName + " is not supported in " + PATH + "." + NAME);
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new Random();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        assert other instanceof Random;
        // Nothing to merge
    }

}
