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
 * Representation of the java.util.HashMap class.
 * @author ujiqk
 * @version 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html">Oracle Docs</a>
 */
public class HashMap extends JavaObject implements ISpecialObject {

    private static final java.lang.String PATH = "java.util";
    private static final java.lang.String NAME = "HashMap";

    /**
     * Representation of the java.util.HashMap class.
     */
    public HashMap() {
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
            case "put" -> {
                assert paramVars.size() == 2;
                return Value.valueFactory(expectedType);
            }
            case "get" -> {
                assert paramVars.size() == 1;
                return Value.valueFactory(expectedType);
            }
            case "containsKey" -> {
                assert paramVars.size() == 1;
                return Value.valueFactory(expectedType);
            }
            case "size" -> {
                assert paramVars == null || paramVars.size() == 0;
                return Value.valueFactory(new Type(Type.TypeEnum.INT));
            }
            case "remove" -> {
                assert paramVars.size() == 1;
                return Value.valueFactory(expectedType);
            }
            case "putAll" -> {
                assert paramVars.size() == 1;
                return Value.valueFactory(expectedType);
            }
            case "clear" -> {
                assert paramVars == null || paramVars.size() == 0;
                return Value.valueFactory(expectedType);
            }
            case "keySet" -> {
                assert paramVars == null || paramVars.size() == 0;
                return Value.valueFactory(expectedType);
            }
            case "clone" -> {
                assert paramVars == null || paramVars.size() == 0;
                return new HashMap();
            }
            case "entrySet" -> {
                assert paramVars == null || paramVars.size() == 0;
                return Value.valueFactory(expectedType);
            }
            case "getOrDefault" -> {
                assert paramVars.size() == 2;
                return Value.valueFactory(expectedType);
            }
            case "replace" -> {
                assert paramVars.size() == 2 || paramVars.size() == 3;
                return Value.valueFactory(expectedType);
            }
            case "replaceAll" -> {
                assert paramVars.size() == 1;
                return Value.valueFactory(expectedType);
            }
            case "putIfAbsent" -> {
                assert paramVars.size() == 2;
                return Value.valueFactory(expectedType);
            }
            case "isEmpty" -> {
                assert paramVars == null || paramVars.size() == 0;
                assert expectedType.getTypeEnum() == Type.TypeEnum.BOOLEAN || expectedType.getTypeEnum() == Type.TypeEnum.UNKNOWN;
                return Value.valueFactory(new Type(Type.TypeEnum.BOOLEAN));
            }
            default -> {
                return Value.valueFactory(expectedType);
            }
        }
    }

    @NotNull
    @Override
    public JavaObject copy() {
        return new HashMap();
    }

    @NotNull
    @Override
    public JavaObject copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy();
    }

    @Override
    public void merge(@NotNull IValue other) {
        assert other instanceof HashMap;
        // Nothing to merge yet
    }

}
