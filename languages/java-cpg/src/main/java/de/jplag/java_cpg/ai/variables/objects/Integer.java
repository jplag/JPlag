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
import de.jplag.java_cpg.ai.variables.values.numbers.IIntNumber;
import de.jplag.java_cpg.ai.variables.values.numbers.INumberValue;
import de.jplag.java_cpg.ai.variables.values.string.IStringValue;

/**
 * Representation of the static java.lang.Integer class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Integer.html">Oracle Docs</a>
 */
public class Integer extends JavaObject implements ISpecialObject {

    private static final java.lang.String PATH = "java.lang";
    private static final java.lang.String NAME = "Integer";

    /**
     * Representation of the static java.lang.Integer class.
     */
    public Integer() {
        super(new Type(Type.TypeEnum.OBJECT, getName().toString()));
    }

    /**
     * @return The variable name representing java.lang.Integer.
     */
    @NotNull
    @Pure
    public static VariableName getName() {
        return new VariableName(PATH + "." + NAME);
    }

    @Override
    public IValue callMethod(@NotNull java.lang.String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "parseInt", "valueOf" -> {
                assert paramVars.size() == 1;
                IValue value = paramVars.getFirst();
                switch (value) {
                    case IStringValue str -> {
                        return str.callMethod("parseInt", paramVars, null, expectedType);
                    }
                    case VoidValue _ -> {
                        return Value.valueFactory(new Type(Type.TypeEnum.INT));
                    }
                    case IIntNumber intNumber -> {
                        return intNumber;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + value);
                }
            }
            case "toString" -> {
                if (paramVars.size() == 2) {    // with radix
                    return Value.valueFactory(new Type(Type.TypeEnum.STRING));
                }
                assert paramVars.size() == 1 : "toString method of Integer expects exactly one parameter but got " + paramVars.size();
                IValue value = paramVars.getFirst();
                switch (value) {
                    case INumberValue intValue -> {
                        if (intValue.getInformation()) {
                            return Value.valueFactory(java.lang.Double.toString(intValue.getValue()));
                        } else {
                            return Value.valueFactory(new Type(Type.TypeEnum.STRING));
                        }
                    }
                    case VoidValue _ -> {
                        return Value.valueFactory(new Type(Type.TypeEnum.STRING));
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + value);
                }
            }
            default -> {
                return new VoidValue();
            }
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new Integer();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        // Nothing to merge
        assert other instanceof Integer;
    }

}
