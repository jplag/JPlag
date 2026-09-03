package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.pipeline.TransformationPipeline;
import de.jplag.java.babylon.transformer.TransformerTest;
import de.jplag.java.babylon.transformer.impl.util.DelegatePipelineStep;

/**
 * Unit test for the combination of {@link ConstantPropagationStep}, {@link CopyElisionTransformer}, and
 * {@link DeadCodeEliminationTransformer}.
 */
public class DeadCodeEliminationTest extends TransformerTest {
    @Override
    protected String getFileName() {
        return "CopyElision.java";
    }

    @Override
    protected TransformationPipeline getPipeline() {
        DelegatePipelineStep copyElision = step(new CopyElisionTransformer());
        DelegatePipelineStep deadCodeElimination = step(new DeadCodeEliminationTransformer());
        return pipeline(new ConstantPropagationStep(), copyElision, deadCodeElimination, copyElision, deadCodeElimination);
    }

    @Override
    protected String getExpectedOriginal() {
        return """
                func @loc="1:1:CopyElision.java" @"main" (%0 : java.type:"CopyElision")java.type:"void" -> {
                    %1 : java.type:"int" = constant @loc="2:13" @0;
                    %2 : Var<java.type:"int"> = var %1 @loc="2:5" @"a";
                    java.for @loc="3:5"
                        ()Var<java.type:"int"> -> {
                            %3 : java.type:"int" = constant @loc="3:18" @0;
                            %4 : Var<java.type:"int"> = var %3 @loc="3:10" @"i";
                            yield %4 @loc="3:5";
                        }
                        (%5 : Var<java.type:"int">)java.type:"boolean" -> {
                            %6 : java.type:"int" = var.load %5 @loc="3:21";
                            %7 : java.type:"int" = constant @loc="3:25" @10;
                            %8 : java.type:"boolean" = lt %6 %7 @loc="3:21";
                            yield %8 @loc="3:5";
                        }
                        (%9 : Var<java.type:"int">)java.type:"void" -> {
                            %10 : java.type:"int" = var.load %9 @loc="3:29";
                            %11 : java.type:"int" = constant @loc="3:29" @1;
                            %12 : java.type:"int" = add %10 %11 @loc="3:29";
                            var.store %9 %12 @loc="3:29";
                            yield @loc="3:5";
                        }
                        (%13 : Var<java.type:"int">)java.type:"void" -> {
                            %14 : java.type:"int" = var.load %13 @loc="4:13";
                            var.store %2 %14 @loc="4:9";
                            java.continue @loc="3:5";
                        };
                    %15 : java.type:"int" = constant @loc="7:13" @0;
                    %16 : Var<java.type:"int"> = var %15 @loc="7:5" @"b";
                    %17 : java.type:"int" = var.load %16 @loc="8:13";
                    %18 : Var<java.type:"int"> = var %17 @loc="8:5" @"c";
                    %19 : java.type:"int" = var.load %18 @loc="9:16";
                    %20 : java.type:"java.lang.Integer" = invoke %19 @loc="9:5" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    invoke %20 @loc="9:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    %21 : java.type:"java.lang.Class" = constant @loc="11:13" @java.type:"java.lang.IO";
                    %22 : java.type:"int" = invoke %21 @loc="11:13" @java.ref:"java.lang.Object::hashCode():int";
                    %23 : Var<java.type:"int"> = var %22 @loc="11:5" @"d";
                    %24 : java.type:"int" = var.load %23 @loc="12:13";
                    %25 : Var<java.type:"int"> = var %24 @loc="12:5" @"e";
                    %26 : java.type:"int" = var.load %25 @loc="13:16";
                    %27 : java.type:"java.lang.Integer" = invoke %26 @loc="13:5" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    invoke %27 @loc="13:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    java.for @loc="15:5"
                        ()Var<java.type:"int"> -> {
                            %28 : java.type:"int" = constant @loc="15:18" @0;
                            %29 : Var<java.type:"int"> = var %28 @loc="15:10" @"i";
                            yield %29 @loc="15:5";
                        }
                        (%30 : Var<java.type:"int">)java.type:"boolean" -> {
                            %31 : java.type:"int" = var.load %30 @loc="15:21";
                            %32 : java.type:"int" = constant @loc="15:25" @10;
                            %33 : java.type:"boolean" = lt %31 %32 @loc="15:21";
                            yield %33 @loc="15:5";
                        }
                        (%34 : Var<java.type:"int">)java.type:"void" -> {
                            %35 : java.type:"int" = var.load %34 @loc="15:29";
                            %36 : java.type:"int" = constant @loc="15:29" @1;
                            %37 : java.type:"int" = add %35 %36 @loc="15:29";
                            var.store %34 %37 @loc="15:29";
                            yield @loc="15:5";
                        }
                        (%38 : Var<java.type:"int">)java.type:"void" -> {
                            java.if @loc="16:9"
                                ()java.type:"boolean" -> {
                                    %39 : java.type:"int" = var.load %38 @loc="16:13";
                                    %40 : java.type:"int" = constant @loc="16:17" @5;
                                    %41 : java.type:"boolean" = lt %39 %40 @loc="16:13";
                                    yield %41 @loc="16:9";
                                }
                                ()java.type:"void" -> {
                                    %42 : java.type:"int" = var.load %16 @loc="17:13";
                                    %43 : java.type:"int" = constant @loc="17:13" @1;
                                    %44 : java.type:"int" = add %42 %43 @loc="17:13";
                                    var.store %16 %44 @loc="17:13";
                                    yield @loc="16:9";
                                }
                                ()java.type:"void" -> {
                                    %45 : java.type:"int" = var.load %25 @loc="19:13";
                                    %46 : java.type:"int" = constant @loc="19:13" @1;
                                    %47 : java.type:"int" = add %45 %46 @loc="19:13";
                                    var.store %25 %47 @loc="19:13";
                                    yield @loc="16:9";
                                };
                            java.continue @loc="15:5";
                        };
                    return @loc="1:1";
                };""";
    }

    @Override
    protected String getExpectedTransformed() {
        return """
                func @loc="1:1:CopyElision.java" @"main" (%0 : java.type:"CopyElision")java.type:"void" -> {
                    %1 : java.type:"int" = constant @0;
                    %2 : java.type:"java.lang.Integer" = invoke %1 @loc="9:5" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    invoke %2 @loc="9:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    %3 : java.type:"java.lang.Class" = constant @loc="11:13" @java.type:"java.lang.IO";
                    %4 : java.type:"int" = invoke %3 @loc="11:13" @java.ref:"java.lang.Object::hashCode():int";
                    %5 : java.type:"java.lang.Integer" = invoke %4 @loc="13:5" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    invoke %5 @loc="13:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    return @loc="1:1";
                };""";
    }
}
