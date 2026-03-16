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
import de.jplag.java_cpg.ai.variables.values.arrays.IJavaArray;
import de.jplag.java_cpg.ai.variables.values.numbers.INumberValue;

/**
 * Representation of the java.util.HashSet class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/HashSet.html">Oracle Docs</a>
 */
public class HashSet extends JavaObject implements ISpecialObject, IJavaArray {

    private static final java.lang.String PATH = "java.util";
    private static final java.lang.String NAME = "HashSet";

    /**
     * Representation of the java.util.HashMap class.
     */
    public HashSet() {
        super(new Type(Type.TypeEnum.OBJECT));
    }

    /**
     * @return The variable name representing java.util.HashMap.
     */
    @NotNull
    @Pure
    public static VariableName getName() {
        return new VariableName(PATH + "." + NAME);
    }

    @Override
    public IValue callMethod(@NotNull java.lang.String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        // ToDo: not yet implemented
        switch (methodName) {
            case "add" -> {
                assert paramVars.size() == 1 : "add expects 1 parameter";
                return Value.valueFactory(expectedType);
            }
            case "remove" -> {
                assert paramVars.size() == 1 : "remove expects 1 parameter";
                return Value.valueFactory(expectedType);
            }
            case "contains" -> {
                assert paramVars.size() == 1 : "contains expects 1 parameter";
                return Value.valueFactory(expectedType);
            }
            case "size" -> {
                assert paramVars == null || paramVars.isEmpty() : "size expects 0 parameters";
                return Value.valueFactory(expectedType);
            }
            case "first" -> {
                assert paramVars == null || paramVars.isEmpty() : "first expects 0 parameters";
                return Value.valueFactory(expectedType);
            }
            default -> {
                return Value.valueFactory(expectedType);
            }
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new HashSet();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        assert other instanceof HashSet;
        // Nothing to merge yet
    }

    @Override
    public IValue arrayAccess(INumberValue index) {
        return new VoidValue();
    }

    @Override
    public void arrayAssign(INumberValue index, IValue value) {
        // Do nothing
    }

}
