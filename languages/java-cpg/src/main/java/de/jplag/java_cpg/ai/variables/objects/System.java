package de.jplag.java_cpg.ai.variables.objects;

import java.util.List;
import java.util.Map;

import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.jplag.java_cpg.ai.JavaLanguageFeatureNotSupportedException;
import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.VariableName;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.JavaObject;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.VoidValue;
import de.jplag.java_cpg.ai.variables.values.arrays.IJavaArray;

/**
 * Representation of the static java.lang.System class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/System.html">Oracle Docs</a>
 */
public class System extends JavaObject implements ISpecialObject {

    private static final java.lang.String PATH = "java.lang";
    private static final java.lang.String NAME = "System";

    /**
     * Creates a new representation of the java.lang.System class.
     */
    public System() {
        super(new Type(Type.TypeEnum.OBJECT, getName().toString()));
    }

    /**
     * @return The variable name java.lang.System
     */
    @Pure
    @NotNull
    public static VariableName getName() {
        return new VariableName(PATH + "." + NAME);
    }

    @Override
    public IValue callMethod(@NotNull java.lang.String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "lineSeparator" -> {
                assert paramVars == null || paramVars.isEmpty();
                return Value.getNewStringValue("\n");
            }
            case "exit" -> throw new JavaLanguageFeatureNotSupportedException("System.exit() called");

            case "currentTimeMillis" -> {
                assert paramVars == null || paramVars.isEmpty();
                return Value.valueFactory(new Type(Type.TypeEnum.INT));
            }
            case "arraycopy" -> {   // void arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
                IJavaArray srcArray = (IJavaArray) paramVars.getFirst();
                IJavaArray destArray = (IJavaArray) paramVars.get(2);
                srcArray.setToUnknown();
                destArray.setToUnknown();
                return new VoidValue();
            }
            case "getProperty" -> {
                assert paramVars.size() == 1;
                return Value.valueFactory(new Type(Type.TypeEnum.STRING));
            }
            default -> throw new UnsupportedOperationException(methodName + " is not supported in " + PATH + "." + NAME);
        }
    }

    @Override
    public Value accessField(@NotNull java.lang.String fieldName, @NotNull Type expectedType) {
        switch (fieldName) {
            case "out", "err" -> {
                return new PrintStream();
            }
            case "in" -> {
                return new InputStream();
            }
            default -> throw new UnsupportedOperationException("Field " + fieldName + " is not supported in " + PATH + "." + NAME);
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new System();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        assert other instanceof System;
        // Nothing to merge
    }

}
