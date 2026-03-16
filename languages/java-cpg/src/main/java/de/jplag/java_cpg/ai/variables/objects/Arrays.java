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

/**
 * Representation of the static java.util.Arrays class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html">Oracle Docs</a>
 */
public class Arrays extends JavaObject implements ISpecialObject {

    private static final java.lang.String PATH = "java.util";
    private static final java.lang.String NAME = "Arrays";

    /**
     * Representation of the static java.util.Arrays class.
     */
    public Arrays() {
        super(new Type(Type.TypeEnum.OBJECT, getName().toString()));
    }

    /**
     * @return The variable name representing java.util.Arrays.
     */
    @NotNull
    @Pure
    public static VariableName getName() {
        return new VariableName(PATH + "." + NAME);
    }

    @Override
    public IValue callMethod(@NotNull java.lang.String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "toString" -> {
                assert paramVars.size() == 1;
                IJavaArray array = (IJavaArray) paramVars.getFirst();
                return array.callMethod("toString", List.of(), null, expectedType);
            }
            case "fill" -> {        // void fill(int[] a, int val) or void fill(int[] a, int fromIndex, int toIndex, int val)
                assert paramVars.size() == 2 || paramVars.size() == 4;
                IJavaArray array = (IJavaArray) paramVars.getFirst();
                return array.callMethod("fill", paramVars.subList(1, paramVars.size()), null, expectedType);
            }
            case "sort" -> {        // void sort(int[] a) or void sort(int[] a, int fromIndex, int toIndex)
                assert paramVars.size() == 1 || paramVars.size() == 3 || paramVars.size() == 2;
                if (paramVars.getFirst() instanceof VoidValue) {
                    paramVars.set(0, Value.getNewArayValue());
                }
                IJavaArray array = (IJavaArray) paramVars.getFirst();
                return array.callMethod("sort", paramVars.subList(1, paramVars.size()), null, expectedType);
            }
            case "copyOfRange" -> { // int[] copyOfRange(int[] original, int from, int to)
                assert paramVars.size() == 3;
                IJavaArray array = (IJavaArray) paramVars.getFirst();
                return array.callMethod("copyOfRange", paramVars.subList(1, paramVars.size()), null, expectedType);
            }
            case "asList" -> {      // <T> List<T> asList(T... a)
                return Value.getNewArayValue();
            }
            case "stream" -> {      // <T> Stream<T> stream(T[] array)
                assert paramVars.size() == 1;
                if (paramVars.getFirst() instanceof VoidValue) {
                    paramVars.set(0, Value.getNewArayValue());
                }
                IJavaArray array = (IJavaArray) paramVars.getFirst();
                return array.callMethod(methodName, List.of(), null, expectedType);
            }
            case "copyOf" -> {
                assert paramVars.size() == 2 || paramVars.size() == 3;
                return Value.getNewArayValue();
            }
            default -> {
                return Value.valueFactory(expectedType);
            }
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new Arrays();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        assert other instanceof Arrays;
        // nothing to merge
    }

}
