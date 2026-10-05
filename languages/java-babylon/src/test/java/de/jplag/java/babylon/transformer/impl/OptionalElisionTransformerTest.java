package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.pipeline.TransformationPipeline;
import de.jplag.java.babylon.transformer.TransformerTest;

/**
 * Unit test for {@link OptionalElisionTransformer}.
 */
public class OptionalElisionTransformerTest extends TransformerTest {
    @Override
    protected String getFileName() {
        return "Optional.java";
    }

    @Override
    protected TransformationPipeline getPipeline() {
        return pipeline(step(new OptionalElisionTransformer()), step(new DeadCodeEliminationTransformer()));
    }

    @Override
    protected String getExpectedOriginal() {
        return """
                func @loc="1:1:Optional.java" @"main" (%0 : java.type:"Optional")java.type:"void" -> {
                    %1 : java.type:"java.lang.String" = constant @loc="2:42" @"";
                    %2 : java.type:"java.util.Optional<java.lang.String>" = invoke %1 @loc="2:30" @java.ref:"java.util.Optional::of(java.lang.Object):java.util.Optional";
                    %3 : Var<java.type:"java.util.Optional<java.lang.String>"> = var %2 @loc="2:5" @"oing1";
                    %4 : java.type:"java.lang.String" = constant @loc="3:50" @"";
                    %5 : java.type:"java.util.Optional<java.lang.String>" = invoke %4 @loc="3:30" @java.ref:"java.util.Optional::ofNullable(java.lang.Object):java.util.Optional";
                    %6 : Var<java.type:"java.util.Optional<java.lang.String>"> = var %5 @loc="3:5" @"oing2";
                    %7 : java.type:"java.util.Optional<java.lang.String>" = invoke @loc="4:30" @java.ref:"java.util.Optional::empty():java.util.Optional";
                    %8 : Var<java.type:"java.util.Optional<java.lang.String>"> = var %7 @loc="4:5" @"oing3";
                    %9 : java.type:"java.util.Optional<java.lang.String>" = var.load %3 @loc="6:5";
                    %10 : java.type:"java.lang.String" = invoke %9 @loc="6:5" @java.ref:"java.util.Optional::orElseThrow():java.lang.Object";
                    %11 : java.type:"java.util.Optional<java.lang.String>" = var.load %6 @loc="7:5";
                    %12 : java.type:"java.util.function.Function<java.lang.String, char[]>" = lambda @loc="7:15" @lambda.isReflectable=true (%13 : java.type:"java.lang.String")java.type:"char[]" -> {
                        %14 : Var<java.type:"java.lang.String"> = var %13 @loc="7:15" @"rec$";
                        %15 : java.type:"java.lang.String" = var.load %14 @loc="7:15";
                        %16 : java.type:"char[]" = invoke %15 @loc="7:15" @java.ref:"java.lang.String::toCharArray():char[]";
                        return %16 @loc="7:15";
                    };
                    %17 : java.type:"java.util.Optional<char[]>" = invoke %11 %12 @loc="7:5" @java.ref:"java.util.Optional::map(java.util.function.Function):java.util.Optional";
                    %18 : java.type:"boolean" = invoke %17 @loc="7:5" @java.ref:"java.util.Optional::isPresent():boolean";
                    %19 : java.type:"java.util.Optional<java.lang.String>" = var.load %8 @loc="8:5";
                    %20 : java.type:"java.util.function.Predicate<java.lang.String>" = lambda @loc="8:18" @lambda.isReflectable=true (%21 : java.type:"java.lang.String")java.type:"boolean" -> {
                        %22 : Var<java.type:"java.lang.String"> = var %21 @loc="8:18" @"rec$";
                        %23 : java.type:"java.lang.String" = var.load %22 @loc="8:18";
                        %24 : java.type:"boolean" = invoke %23 @loc="8:18" @java.ref:"java.lang.String::isBlank():boolean";
                        return %24 @loc="8:18";
                    };
                    %25 : java.type:"java.util.Optional<java.lang.String>" = invoke %19 %20 @loc="8:5" @java.ref:"java.util.Optional::filter(java.util.function.Predicate):java.util.Optional";
                    %26 : java.type:"java.lang.String" = constant @loc="8:42" @"";
                    %27 : java.type:"java.lang.String" = invoke %25 %26 @loc="8:5" @java.ref:"java.util.Optional::orElse(java.lang.Object):java.lang.Object";
                    %28 : java.type:"java.util.OptionalInt" = invoke @loc="10:25" @java.ref:"java.util.OptionalInt::empty():java.util.OptionalInt";
                    %29 : Var<java.type:"java.util.OptionalInt"> = var %28 @loc="10:5" @"oing4";
                    %30 : java.type:"java.util.OptionalInt" = var.load %29 @loc="11:5";
                    %31 : java.type:"int" = invoke %30 @loc="11:5" @java.ref:"java.util.OptionalInt::orElseThrow():int";
                    return @loc="1:1";
                };""";
    }

    @Override
    protected String getExpectedTransformed() {
        return """
                func @loc="1:1:Optional.java" @"main" (%0 : java.type:"Optional")java.type:"void" -> {
                    %1 : Var<java.type:"java.lang.String"> = var @loc="2:5" @"oing1";
                    %2 : Var<java.type:"java.lang.String"> = var @loc="3:5" @"oing2";
                    %3 : Var<java.type:"java.lang.String"> = var @loc="4:5" @"oing3";
                    %4 : java.type:"java.lang.String" = var.load %1 @loc="6:5";
                    %5 : java.type:"java.lang.String" = invoke %4 @loc="6:5" @java.ref:"java.util.Objects::requireNonNull(java.lang.Object):java.lang.Object";
                    %6 : java.type:"java.lang.String" = var.load %2 @loc="7:5";
                    %7 : Var<java.type:"java.lang.String"> = var %6 @loc="7:5";
                    %8 : java.type:"char[]" = java.cexpression @loc="7:5"
                        ()java.type:"boolean" -> {
                            %9 : java.type:"java.lang.String" = var.load %7 @loc="7:5";
                            %10 : java.type:"char[]" = constant @loc="7:5" @null;
                            %11 : java.type:"boolean" = neq %9 %10 @loc="7:5";
                            yield %11 @loc="7:5";
                        }
                        ()java.type:"char[]" -> {
                            %12 : java.type:"java.lang.String" = var.load %7 @loc="7:5";
                            %13 : Var<java.type:"java.lang.String"> = var %12 @loc="7:15" @"rec$";
                            %14 : java.type:"java.lang.String" = var.load %13 @loc="7:15";
                            %15 : java.type:"char[]" = invoke %14 @loc="7:15" @java.ref:"java.lang.String::toCharArray():char[]";
                            yield %15 @loc="7:5";
                        }
                        ()java.type:"char[]" -> {
                            %16 : java.type:"char[]" = constant @loc="7:5" @null;
                            yield %16 @loc="7:5";
                        };
                    %17 : java.type:"char[]" = constant @loc="7:5" @null;
                    %18 : java.type:"java.lang.String" = var.load %3 @loc="8:5";
                    %19 : Var<java.type:"java.lang.String"> = var %18 @loc="8:5";
                    %20 : java.type:"java.lang.String" = java.cexpression @loc="8:5"
                        ()java.type:"boolean" -> {
                            %21 : java.type:"java.lang.String" = var.load %19 @loc="8:5";
                            %22 : java.type:"boolean" = java.cand @loc="8:5"
                                ()java.type:"boolean" -> {
                                    %23 : java.type:"java.lang.String" = var.load %19 @loc="8:5";
                                    %24 : java.type:"java.lang.String" = constant @loc="8:5" @null;
                                    %25 : java.type:"boolean" = neq %23 %24 @loc="8:5";
                                    yield %25 @loc="8:5";
                                }
                                ()java.type:"boolean" -> {
                                    %26 : Var<java.type:"java.lang.String"> = var %21 @loc="8:18" @"rec$";
                                    %27 : java.type:"java.lang.String" = var.load %26 @loc="8:18";
                                    %28 : java.type:"boolean" = invoke %27 @loc="8:18" @java.ref:"java.lang.String::isBlank():boolean";
                                    yield %28 @loc="8:5";
                                };
                            yield %22 @loc="8:5";
                        }
                        ()java.type:"java.lang.String" -> {
                            %29 : java.type:"java.lang.String" = var.load %19 @loc="8:5";
                            yield %29 @loc="8:5";
                        }
                        ()java.type:"java.lang.String" -> {
                            %30 : java.type:"java.lang.String" = constant @loc="8:5" @null;
                            yield %30 @loc="8:5";
                        };
                    %31 : java.type:"java.lang.String" = constant @loc="8:42" @"";
                    %32 : java.type:"java.lang.String" = invoke %20 %31 @loc="8:5" @java.ref:"java.util.Objects::requireNonNullElse(java.lang.Object, java.lang.Object):java.lang.Object";
                    %33 : java.type:"java.util.OptionalInt" = invoke @loc="10:25" @java.ref:"java.util.OptionalInt::empty():java.util.OptionalInt";
                    %34 : Var<java.type:"java.util.OptionalInt"> = var %33 @loc="10:5" @"oing4";
                    %35 : java.type:"java.util.OptionalInt" = var.load %34 @loc="11:5";
                    %36 : java.type:"int" = invoke %35 @loc="11:5" @java.ref:"java.util.OptionalInt::orElseThrow():int";
                    return @loc="1:1";
                };""";
    }
}
