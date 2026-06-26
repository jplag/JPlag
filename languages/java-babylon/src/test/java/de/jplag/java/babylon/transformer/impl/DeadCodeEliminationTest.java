package de.jplag.java.babylon.transformer.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.jplag.java.babylon.transformer.TransformationPipeline;
import de.jplag.java.babylon.transformer.impl.util.DelegatePipelineStep;

import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Unit test for the combination of {@link ConstantPropagationStep}, {@link CopyElisionTransformer}, and
 * {@link DeadCodeEliminationTransformer}.
 */
public class DeadCodeEliminationTest extends AbstractTransformerTest {
    /**
     * Unit test for the combination of {@link ConstantPropagationStep}, {@link CopyElisionTransformer}, and
     * {@link DeadCodeEliminationTransformer}.
     */
    @Test
    public void testTransformer() {
        ParseResult parseResult = assertDoesNotThrow(() -> parseFile("CopyElision.java"));
        CoreOp.FuncOp op = parseResult.extractCodeModel();
        var copyElision = new DelegatePipelineStep(new CopyElisionTransformer());
        var deadCodeElimination = new DelegatePipelineStep(new DeadCodeEliminationTransformer());
        CoreOp.FuncOp transformedOp = parseResult.extractCodeModel(new TransformationPipeline(
                List.of(new ConstantPropagationStep(), copyElision, deadCodeElimination, copyElision, deadCodeElimination)));
        assertEquals("""
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
                    %24 : java.type:"int" = var.load %16 @loc="12:13";
                    %25 : Var<java.type:"int"> = var %24 @loc="12:5" @"e";
                    %26 : java.type:"int" = var.load %18 @loc="13:16";
                    %27 : java.type:"java.lang.Integer" = invoke %26 @loc="13:5" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    invoke %27 @loc="13:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    return @loc="1:1";
                };""", op.toText());
        assertEquals("""
                func @loc="1:1:CopyElision.java" @"main" (%0 : java.type:"CopyElision")java.type:"void" -> {
                    java.for @loc="3:5"
                        ()Var<java.type:"int"> -> {
                            %1 : java.type:"int" = constant @0;
                            %2 : Var<java.type:"int"> = var %1 @loc="3:10" @"i";
                            yield %2 @loc="3:5";
                        }
                        (%3 : Var<java.type:"int">)java.type:"boolean" -> {
                            %4 : java.type:"int" = var.load %3 @loc="3:21";
                            %5 : java.type:"int" = constant @10;
                            %6 : java.type:"boolean" = lt %4 %5 @loc="3:21";
                            yield %6 @loc="3:5";
                        }
                        (%7 : Var<java.type:"int">)java.type:"void" -> {
                            %8 : java.type:"int" = var.load %7 @loc="3:29";
                            %9 : java.type:"int" = constant @1;
                            %10 : java.type:"int" = add %8 %9 @loc="3:29";
                            var.store %7 %10 @loc="3:29";
                            yield @loc="3:5";
                        }
                        (%11 : Var<java.type:"int">)java.type:"void" -> {
                            java.continue @loc="3:5";
                        };
                    %12 : java.type:"int" = constant @0;
                    %13 : java.type:"java.lang.Integer" = invoke %12 @loc="9:5" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    invoke %13 @loc="9:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    %14 : java.type:"java.lang.Class" = constant @loc="11:13" @java.type:"java.lang.IO";
                    %15 : java.type:"int" = invoke %14 @loc="11:13" @java.ref:"java.lang.Object::hashCode():int";
                    %16 : java.type:"int" = constant @0;
                    %17 : java.type:"java.lang.Integer" = invoke %16 @loc="13:5" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    invoke %17 @loc="13:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    return @loc="1:1";
                };""", transformedOp.toText());
    }
}
