package de.jplag.java_cpg.ai.variables.objects;

import java.util.List;
import java.util.Map;

import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.VariableName;
import de.jplag.java_cpg.ai.variables.values.BooleanValue;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.JavaObject;
import de.jplag.java_cpg.ai.variables.values.Value;

/**
 * Representation of the java.util.regex.Pattern class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html">Oracle Docs</a>
 */
public class Pattern extends JavaObject implements ISpecialObject {

    private static final java.lang.String PATH = "java.util.regex";
    private static final java.lang.String NAME = "Pattern";

    /**
     * Representation of the java.util.regex.Pattern class.
     */
    public Pattern() {
        super(new Type(Type.TypeEnum.OBJECT, getName().toString()));
    }

    /**
     * @return The variable name representing java.util.regex.Pattern.
     */
    @NotNull
    @Pure
    public static VariableName getName() {
        return new VariableName(PATH + "." + NAME);
    }

    @Override
    public IValue callMethod(@NotNull java.lang.String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "matches", "find" -> {
                return new BooleanValue();
            }
            case "compile", "matcher" -> {
                assert paramVars.size() == 1 || paramVars.size() == 2;
                return new Pattern();
            }
            case "toString" -> {
                return Value.valueFactory(new Type(Type.TypeEnum.STRING));
            }
            case "group" -> {
                assert paramVars == null || paramVars.isEmpty();
                return Value.valueFactory(new Type(Type.TypeEnum.STRING));
            }
            default -> throw new UnsupportedOperationException(methodName);
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new Pattern();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        assert other instanceof Pattern;
        // Nothing to merge
    }

}
