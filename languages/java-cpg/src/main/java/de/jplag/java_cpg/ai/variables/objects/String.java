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
import de.jplag.java_cpg.ai.variables.values.string.IStringValue;
import de.jplag.java_cpg.ai.variables.values.string.StringValue;

/**
 * Representation of the static java.lang.String class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/String.html">Oracle Docs</a>
 */
public class String extends JavaObject implements ISpecialObject {

    private static final java.lang.String PATH = "java.lang";
    private static final java.lang.String NAME = "String";

    /**
     * Creates a new representation of the java.lang.String class.
     */
    public String() {
        super(new Type(Type.TypeEnum.OBJECT, getName().toString()));
    }

    /**
     * @return The variable name java.lang.String
     */
    @NotNull
    @Pure
    public static VariableName getName() {
        return new VariableName(PATH + "." + NAME);
    }

    @Override
    public IValue callMethod(@NotNull java.lang.String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "format" -> {
                assert !paramVars.isEmpty();
                return Value.getNewStringValue();
            }
            case "join" -> {
                assert paramVars.size() >= 2;
                assert paramVars.stream().map(IStringValue.class::isInstance).reduce(true, (a, b) -> a && b);
                // Possibility 1: first delimiter, then strings to join
                if (paramVars.stream().allMatch(x -> x instanceof StringValue stringValue && stringValue.getInformation())) {
                    java.lang.String joinedString = java.lang.String.join(((IStringValue) paramVars.get(0)).getValue(),
                            paramVars.subList(1, paramVars.size()).stream().map(x -> ((StringValue) x).getValue()).toArray(java.lang.String[]::new));
                    return Value.getNewStringValue(joinedString);
                }
                // ToDo
                // Possibility 2: first delimiter, then iterable of strings to join
                // Possibility 3: (String prefix, String suffix, String delimiter, String[] elements, int size)
                return Value.getNewStringValue();
            }
            case "valueOf" -> {
                assert paramVars.size() == 1;
                return Value.getNewStringValue();
            }
            default -> throw new UnsupportedOperationException(methodName);
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new String();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        assert other instanceof String;
        // Nothing to merge
    }

}
