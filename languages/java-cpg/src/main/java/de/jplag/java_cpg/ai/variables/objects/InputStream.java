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
 * Representation of the java.io.InputStream class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/io/InputStream.html">Oracle Docs</a>
 */
public class InputStream extends JavaObject implements ISpecialObject {

    private static final java.lang.String PATH = "java.io";
    private static final java.lang.String NAME = "InputStream";

    /**
     * Representation of the java.io.InputStream class.
     */
    public InputStream() {
        super(new Type(Type.TypeEnum.OBJECT));
    }

    /**
     * @return The variable name representing java.io.InputStream.
     */
    @NotNull
    @Pure
    public static VariableName getName() {
        return new VariableName(PATH + "." + NAME);
    }

    @Override
    public IValue callMethod(@NotNull java.lang.String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "read" -> {
                if (paramVars == null || paramVars.isEmpty()) {
                    return Value.valueFactory(new Type(Type.TypeEnum.INT));
                } else {
                    throw new UnsupportedOperationException("InputStream.read() with parameters is not supported");
                }
            }
            default -> throw new UnsupportedOperationException(methodName);
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new InputStream();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        assert other instanceof InputStream;
        // nothing to merge
    }

}
