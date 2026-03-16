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

/**
 * Representation of the java.util.Scanner class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html">Oracle Docs</a>
 */
public class Scanner extends JavaObject implements ISpecialObject {

    private static final java.lang.String PATH = "java.util";
    private static final java.lang.String NAME = "Scanner";

    /**
     * Creates a new Scanner object representation.
     */
    public Scanner() {
        super(new Type(Type.TypeEnum.OBJECT));
    }

    /**
     * @return The variable name representing java.util.Scanner.
     */
    @NotNull
    @Pure
    public static VariableName getName() {
        return new VariableName(PATH + "." + NAME);
    }

    @Override
    public IValue callMethod(@NotNull java.lang.String methodName, List<IValue> paramVars, MethodDeclaration method, @NotNull Type expectedType) {
        switch (methodName) {
            case "nextLine", "next" -> {
                assert paramVars == null || paramVars.isEmpty();
                return Value.valueFactory(new Type(Type.TypeEnum.STRING));
            }
            case "close" -> {
                assert paramVars == null || paramVars.isEmpty();
                return new VoidValue();
            }
            case "nextInt", "nextLong", "nextBigInteger" -> {
                assert paramVars == null || paramVars.isEmpty();
                return Value.valueFactory(new Type(Type.TypeEnum.INT));
            }
            case "nextDouble", "nextFloat", "nextBigDecimal" -> {
                assert paramVars == null || paramVars.isEmpty();
                return Value.valueFactory(new Type(Type.TypeEnum.FLOAT));
            }
            case "hasNextInt", "hasNext", "hasNextLine" -> {
                assert paramVars == null || paramVars.isEmpty()
                        || (paramVars.size() == 1 && paramVars.getFirst().getType().getTypeEnum() == Type.TypeEnum.STRING);
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
            case "useLocale" -> {
                assert paramVars.size() == 1;
                // We don't model Locale, so just return this
                return this;
            }
            case "useDelimiter" -> {
                assert paramVars.size() == 1;
                // We don't model Pattern, so just return this
                return this;
            }
            case "nextByte" -> {
                assert paramVars == null || paramVars.isEmpty();
                throw new JavaLanguageFeatureNotSupportedException("byte is not supported");
            }
            default -> throw new UnsupportedOperationException(methodName + " is not supported in Scanner.");
        }
    }

    @Override
    public Value accessField(@NotNull java.lang.String fieldName, @NotNull Type expectedType) {
        switch (fieldName) {
            default -> throw new UnsupportedOperationException("Field " + fieldName + " is not supported in Scanner.");
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new Scanner();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        assert other instanceof Scanner;
        // Nothing to merge
    }

}
