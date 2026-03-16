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
 * Representation of the static java.lang.StringBuilder class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html">Oracle Docs</a>
 */
public class StringBuilder extends JavaObject implements ISpecialObject {

    private static final java.lang.String PATH = "java.lang";
    private static final java.lang.String NAME = "StringBuilder";

    /**
     * Creates a new representation of the java.lang.StringBuilder class.
     */
    public StringBuilder() {
        super(new Type(Type.TypeEnum.OBJECT));
    }

    /**
     * @return The variable name java.lang.StringBuilder
     */
    @NotNull
    @Pure
    public static VariableName getName() {
        return new VariableName(PATH + "." + NAME);
    }

    @Override
    public IValue callMethod(@NotNull java.lang.String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "append" -> {
                assert !paramVars.isEmpty();
                return this;
            }
            case "length" -> {
                assert paramVars == null || paramVars.isEmpty();
                return Value.valueFactory(new Type(Type.TypeEnum.INT));
            }
            case "toString" -> {
                assert paramVars == null || paramVars.isEmpty();
                return Value.valueFactory(new Type(Type.TypeEnum.STRING));
            }
            case "deleteCharAt" -> {
                assert paramVars.size() == 1;
                return this;
            }
            case "delete", "setCharAt" -> {
                assert paramVars.size() == 2;
                return this;
            }
            case "substring" -> {
                assert paramVars.size() == 1 || paramVars.size() == 2;
                return Value.valueFactory(new Type(Type.TypeEnum.STRING));
            }
            default -> {
                return Value.valueFactory(expectedType);
            }
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new StringBuilder();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        assert other instanceof StringBuilder;
        // Nothing to merge
    }

}
