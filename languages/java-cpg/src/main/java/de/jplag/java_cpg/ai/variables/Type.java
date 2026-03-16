package de.jplag.java_cpg.ai.variables;

import static de.jplag.java_cpg.ai.variables.Type.TypeEnum.ARRAY;
import static de.jplag.java_cpg.ai.variables.Type.TypeEnum.BOOLEAN;
import static de.jplag.java_cpg.ai.variables.Type.TypeEnum.CHAR;
import static de.jplag.java_cpg.ai.variables.Type.TypeEnum.FLOAT;
import static de.jplag.java_cpg.ai.variables.Type.TypeEnum.FUNCTION;
import static de.jplag.java_cpg.ai.variables.Type.TypeEnum.INT;
import static de.jplag.java_cpg.ai.variables.Type.TypeEnum.LIST;
import static de.jplag.java_cpg.ai.variables.Type.TypeEnum.OBJECT;
import static de.jplag.java_cpg.ai.variables.Type.TypeEnum.STRING;
import static de.jplag.java_cpg.ai.variables.Type.TypeEnum.UNKNOWN;
import static de.jplag.java_cpg.ai.variables.Type.TypeEnum.VOID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.fraunhofer.aisec.cpg.graph.types.ObjectType;

/**
 * Enumeration of supported variable types.
 * @author ujiqk
 * @version 1.0
 */
public class Type {

    /**
     * Enumeration of supported variable types.
     * @author ujiqk
     * @version 1.0
     */
    public enum TypeEnum {
        INT,
        FLOAT,
        STRING,
        BOOLEAN,
        OBJECT,
        ARRAY,
        LIST,
        NULL,
        FUNCTION,
        CHAR,
        UNKNOWN,
        VOID;
    }

    private final @NotNull TypeEnum typeEnum;
    private final @Nullable String typeName;    // only used for the OBJECT type to save the class name
    private final @Nullable Type innerType;

    /**
     * Constructor for non-OBJECT, non-ARRAY, non-LIST types.
     * @param typeEnum the type enum of this type.
     */
    public Type(@NotNull TypeEnum typeEnum) {
        this(typeEnum, null, null);
        // assert typeEnum != OBJECT && typeEnum != ARRAY && typeEnum != LIST;
        assert typeEnum != ARRAY && typeEnum != LIST;
    }

    /**
     * Constructor for Array/List Types.
     * @param typeEnum the type enum of this type.
     * @param innerType the inner type of this type; only valid for the ARRAY and LIST types, otherwise null.
     */
    public Type(@NotNull TypeEnum typeEnum, @Nullable Type innerType) {
        this(typeEnum, null, innerType);
        assert typeEnum == ARRAY || typeEnum == LIST;
    }

    /**
     * Constructor for Object Types.
     * @param typeEnum the type enum of this type.
     * @param typeName the type name of this type; only valid for the OBJECT type, otherwise null.
     */
    public Type(@NotNull TypeEnum typeEnum, @Nullable String typeName) {
        this(typeEnum, typeName, null);
        assert typeEnum == OBJECT;
    }

    /**
     * Constructor for Types.
     * @param typeEnum the type enum of this type.
     * @param typeName the type name of this type; only valid for the OBJECT type, otherwise null.
     * @param innerType the inner type of this type; only valid for the ARRAY and LIST types, otherwise null.
     */
    public Type(@NotNull TypeEnum typeEnum, @Nullable String typeName, @Nullable Type innerType) {
        this.typeEnum = typeEnum;
        this.typeName = typeName;
        this.innerType = innerType;
        assert typeEnum == OBJECT || typeName == null;
        assert (typeEnum == ARRAY || typeEnum == LIST) || innerType == null;
    }

    /**
     * @return the type name of this type; only valid for the OBJECT type, otherwise null.
     */
    public String getTypeName() {
        assert this.typeEnum == OBJECT;
        assert typeName != null;
        return typeName;
    }

    /**
     * @return the inner type of this type; only valid for the ARRAY and LIST types, otherwise null.
     */
    public Type getInnerType() {
        assert this.typeEnum == ARRAY || this.typeEnum == LIST;
        if (this.innerType == null) {
            return new Type(UNKNOWN);
        }
        return innerType;
    }

    /**
     * @return the type enum of this type.
     */
    public @NotNull TypeEnum getTypeEnum() {
        return typeEnum;
    }

    /**
     * @param cpgType CPG type to convert.
     * @return the corresponding Type enum.
     * @throws IllegalArgumentException if the CPG type is not supported.
     */
    public static @NotNull Type fromCpgType(@NotNull de.fraunhofer.aisec.cpg.graph.types.Type cpgType) {
        if (cpgType.getClass() == de.fraunhofer.aisec.cpg.graph.types.IntegerType.class) {
            return new Type(INT);
        } else if (cpgType.getClass() == de.fraunhofer.aisec.cpg.graph.types.StringType.class) {
            return new Type(STRING);
        } else if (cpgType.getClass() == de.fraunhofer.aisec.cpg.graph.types.BooleanType.class) {
            return new Type(BOOLEAN);
        } else if (cpgType.getClass() == de.fraunhofer.aisec.cpg.graph.types.ObjectType.class && cpgType.getName().toString().contains("[]")) {
            Type innerType = stringToType(cpgType.getTypeName().split("\\[")[0]);
            return new Type(ARRAY, innerType);
        } else if (cpgType.getClass() == de.fraunhofer.aisec.cpg.graph.types.ObjectType.class) {
            String name = cpgType.getName().toString();
            name = name.split("<")[0]; // remove generics
            switch (name) {
                case "java.util.ArrayList", "java.util.List", "java.util.Vector", "java.util.LinkedList", "java.util.PriorityQueue", "java.util.Deque", "java.util.Stack", "java.util.ListIterator", "java.util.Queue", "java.util.Set", "java.util.HashSet" -> {
                    ObjectType objectType = (ObjectType) cpgType;
                    Type innerCpgType;
                    if (objectType.getGenerics().isEmpty()) {
                        innerCpgType = new Type(UNKNOWN);
                    } else {
                        assert objectType.getGenerics().size() == 1 : "Expected exactly one generic type for List, but got "
                                + objectType.getGenerics().size();
                        de.fraunhofer.aisec.cpg.graph.types.Type innerType = objectType.getGenerics().getFirst();
                        innerCpgType = de.jplag.java_cpg.ai.variables.Type.fromCpgType(innerType);
                    }
                    return new Type(LIST, innerCpgType);
                }
                case "java.lang.String" -> {
                    return new Type(STRING);
                }
                case "java.lang.Boolean" -> {
                    return new Type(BOOLEAN);
                }
                case "java.lang.Character" -> {
                    return new Type(CHAR);
                }
                case "java.lang.Integer", "java.lang.Long", "java.lang.Short", "java.lang.Byte" -> {
                    return new Type(INT);
                }
                case "java.lang.Float", "java.lang.Double" -> {
                    return new Type(FLOAT);
                }
                case "T", "E", "K", "V" -> {
                    return new Type(UNKNOWN);
                }
                default -> {
                    return new Type(OBJECT, name);   // we lose generics here
                }
            }
        } else if (cpgType.getClass() == de.fraunhofer.aisec.cpg.graph.types.PointerType.class) {
            Type elementType = fromCpgType(((de.fraunhofer.aisec.cpg.graph.types.PointerType) cpgType).getElementType());
            return new Type(ARRAY, elementType);   // in java pointer types are used only for arrays
        } else if (cpgType.getClass() == de.fraunhofer.aisec.cpg.graph.types.FloatingPointType.class) {
            return new Type(FLOAT);
        } else if ((cpgType.getClass() == de.fraunhofer.aisec.cpg.graph.types.IncompleteType.class && cpgType.getName().getLocalName().equals("void"))
                || cpgType.getClass() == de.fraunhofer.aisec.cpg.graph.types.UnknownType.class) {
            return new Type(VOID);
        } else if (cpgType.getClass() == de.fraunhofer.aisec.cpg.graph.types.ParameterizedType.class) {
            return new Type(VOID);
        } else if (cpgType.getClass() == de.fraunhofer.aisec.cpg.graph.types.FunctionType.class) {
            return new Type(FUNCTION);
        } else {
            throw new IllegalArgumentException("Unsupported CPG type: " + cpgType);
        }
    }

    private static @NotNull Type stringToType(@NotNull String typeStr) {
        return switch (typeStr) {
            case "int" -> new Type(INT);
            case "float", "double" -> new Type(FLOAT);
            case "boolean" -> new Type(BOOLEAN);
            case "char" -> new Type(CHAR);
            case "void" -> new Type(VOID);
            case "String", "java.lang.String" -> new Type(STRING);
            case "T", "E", "K", "V" -> new Type(UNKNOWN);
            default -> new Type(OBJECT, typeStr);
        };
    }

    @Override
    public @NotNull String toString() {
        return switch (typeEnum) {
            case INT -> "int";
            case FLOAT -> "float";
            case STRING -> "String";
            case BOOLEAN -> "boolean";
            case OBJECT -> typeName != null ? typeName : "Object";
            case ARRAY -> innerType != null ? innerType + "[]" : "Array";
            case LIST -> innerType != null ? "List<" + innerType + ">" : "List";
            case NULL -> "null";
            case FUNCTION -> "function";
            case CHAR -> "char";
            case UNKNOWN -> "unknown";
            case VOID -> "void";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Type type = (Type) o;
        if (typeEnum != type.typeEnum)
            return false;
        if (typeEnum == OBJECT) {
            return typeName != null && typeName.equals(type.typeName);
        } else if (typeEnum == ARRAY || typeEnum == LIST) {
            return innerType != null && innerType.equals(type.innerType);
        } else {
            return true;    // for primitive types, only the type enum matters
        }
    }

    @Override
    public int hashCode() {
        int result = typeEnum.hashCode();
        if (typeEnum == OBJECT) {
            result = 31 * result + (typeName != null ? typeName.hashCode() : 0);
        } else if (typeEnum == ARRAY || typeEnum == LIST) {
            result = 31 * result + (innerType != null ? innerType.hashCode() : 0);
        }
        return result;
    }

}
