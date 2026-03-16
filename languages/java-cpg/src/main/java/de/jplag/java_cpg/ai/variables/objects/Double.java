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
import de.jplag.java_cpg.ai.variables.values.VoidValue;
import de.jplag.java_cpg.ai.variables.values.numbers.INumberValue;
import de.jplag.java_cpg.ai.variables.values.string.IStringValue;

/**
 * Representation of the static java.lang.Double class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Double.html">Oracle Docs</a>
 */
public class Double extends JavaObject implements ISpecialObject {

    private static final java.lang.String PATH = "java.lang";
    private static final java.lang.String NAME = "Double";

    /**
     * Representation of the static java.lang.Double class.
     */
    public Double() {
        super(new Type(Type.TypeEnum.OBJECT, getName().toString()));
    }

    /**
     * @return Gets the variable name of this object.
     */
    @NotNull
    @Pure
    public static VariableName getName() {
        return new VariableName(PATH + "." + NAME);
    }

    @Override
    public IValue callMethod(@NotNull java.lang.String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "parseDouble", "valueOf" -> {
                assert paramVars.size() == 1;
                IValue value = paramVars.getFirst();
                switch (value) {
                    case IStringValue str -> {
                        return str.callMethod("parseDouble", paramVars, null, expectedType);
                    }
                    case INumberValue num -> {
                        if (num.getInformation()) {
                            return Value.getNewFloatValue(num.getValue());
                        } else {
                            return Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
                        }
                    }
                    case VoidValue _ -> {
                        return Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + value);
                }
            }
            case "compare" -> {
                assert paramVars.size() == 2;
                IValue first = paramVars.get(0);
                IValue second = paramVars.get(1);
                if (first instanceof INumberValue && second instanceof INumberValue) {
                    return first.binaryOperation("compareTo", second);
                } else {
                    throw new IllegalStateException("Unexpected values: " + first + ", " + second);
                }
            }
            default -> throw new UnsupportedOperationException(methodName);
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new Double();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        assert other instanceof Double;
        // nothing to merge
    }

}
