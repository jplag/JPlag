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

/**
 * Representation of the static java.lang.Math class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html">Oracle Docs</a>
 */
public class Math extends JavaObject implements ISpecialObject {

    private static final java.lang.String PATH = "java.lang";
    private static final java.lang.String NAME = "Math";

    /**
     * Representation of the static java.lang.Math class.
     */
    public Math() {
        super(new Type(Type.TypeEnum.OBJECT, getName().toString()));
    }

    /**
     * @return The variable name representing java.lang.Math.
     */
    @NotNull
    @Pure
    public static VariableName getName() {
        return new VariableName(PATH + "." + NAME);
    }

    @Override
    public IValue callMethod(@NotNull java.lang.String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "abs" -> {
                assert paramVars.size() == 1;
                assert paramVars.getFirst() instanceof INumberValue || paramVars.getFirst() instanceof VoidValue;
                return paramVars.getFirst().unaryOperation("abs");
            }
            case "min" -> {
                assert paramVars.size() == 2;
                if (paramVars.getFirst() instanceof VoidValue || paramVars.getLast() instanceof VoidValue) {
                    return new VoidValue();
                }
                assert paramVars.get(0) instanceof INumberValue : "Expected first parameter of min to be a number, but got "
                        + paramVars.get(0).getType();
                assert paramVars.get(1) instanceof INumberValue;
                return paramVars.get(0).binaryOperation("min", paramVars.get(1));
            }
            case "max" -> {
                assert paramVars.size() == 2;
                if (paramVars.getFirst() instanceof VoidValue || paramVars.getLast() instanceof VoidValue) {
                    return new VoidValue();
                }
                assert paramVars.get(0) instanceof INumberValue;
                assert paramVars.get(1) instanceof INumberValue || paramVars.get(1) instanceof VoidValue;
                return paramVars.get(0).binaryOperation("max", paramVars.get(1));
            }
            case "sqrt" -> {
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    return new VoidValue();
                }
                assert paramVars.getFirst() instanceof INumberValue;
                return paramVars.getFirst().unaryOperation("sqrt");
            }
            case "pow" -> {
                assert paramVars.size() == 2;
                if (paramVars.getFirst() instanceof VoidValue || paramVars.getLast() instanceof VoidValue) {
                    return new VoidValue();
                }
                assert paramVars.get(0) instanceof INumberValue;
                assert paramVars.get(1) instanceof INumberValue;
                return paramVars.get(0).binaryOperation("pow", paramVars.get(1));
            }
            case "sin" -> {
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    return Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
                }
                assert paramVars.getFirst() instanceof INumberValue;
                return paramVars.getFirst().unaryOperation("sin");
            }
            case "random" -> {
                assert paramVars == null || paramVars.isEmpty();
                return Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
            }
            case "ceil" -> {
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    return Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
                }
                assert paramVars.getFirst() instanceof INumberValue;
                return paramVars.getFirst().unaryOperation("ceil");
            }
            case "floorMod" -> {
                assert paramVars.size() == 2;
                return Value.valueFactory(new Type(Type.TypeEnum.INT));
            }
            case "floor" -> {
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    return Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
                }
                assert paramVars.getFirst() instanceof INumberValue;
                return paramVars.getFirst().unaryOperation("floor");
            }
            case "round" -> {
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    return Value.valueFactory(new Type(Type.TypeEnum.INT));
                }
                assert paramVars.getFirst() instanceof INumberValue;
                return paramVars.getFirst().unaryOperation("round");
            }
            default -> {
                return Value.valueFactory(expectedType);
            }
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new Math();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        assert other instanceof Math;
        // Nothing to merge
    }

}
