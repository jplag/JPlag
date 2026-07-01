package de.jplag.java.babylon.transformer.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Unit test for {@link ConditionalExpressionDesugarTransformer}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConditionalExpressionDesugarTransformerTest extends AbstractTransformerTest {
    /**
     * Unit test for {@link ConditionalExpressionDesugarTransformer}.
     */
    @Test
    public void testTransformer() {
        ParseResult parseResult = assertDoesNotThrow(() -> parseFile("ConditionalExpression.java"));
        CoreOp.FuncOp op = parseResult.extractCodeModel();
        CoreOp.FuncOp transformedOp = parseResult.extractCodeModel(pipeline(step(new ConditionalExpressionDesugarTransformer())));

        assertEquals("""
                func @loc="1:1:ConditionalExpression.java" @"main" (%0 : java.type:"ConditionalExpression")java.type:"void" -> {
                    %1 : java.type:"int" = java.cexpression @loc="2:18"
                        ()java.type:"boolean" -> {
                            %2 : java.type:"boolean" = invoke %0 @loc="2:18" @java.ref:"ConditionalExpression::condition():boolean";
                            yield %2 @loc="2:18";
                        }
                        ()java.type:"int" -> {
                            %3 : java.type:"int" = constant @loc="2:32" @5;
                            yield %3 @loc="2:18";
                        }
                        ()java.type:"int" -> {
                            %4 : java.type:"int" = constant @loc="2:36" @10;
                            yield %4 @loc="2:18";
                        };
                    %5 : Var<java.type:"int"> = var %1 @loc="2:5" @"result";
                    %6 : java.type:"int" = java.cexpression @loc="4:19"
                        ()java.type:"boolean" -> {
                            %7 : java.type:"int" = var.load %5 @loc="4:19";
                            %8 : java.type:"int" = constant @loc="4:28" @5;
                            %9 : java.type:"boolean" = gt %7 %8 @loc="4:19";
                            yield %9 @loc="4:19";
                        }
                        ()java.type:"int" -> {
                            %10 : java.type:"int" = var.load %5 @loc="4:32";
                            %11 : java.type:"int" = constant @loc="4:41" @5;
                            %12 : java.type:"int" = sub %10 %11 @loc="4:32";
                            yield %12 @loc="4:19";
                        }
                        ()java.type:"int" -> {
                            %13 : java.type:"int" = var.load %5 @loc="4:45";
                            %14 : java.type:"int" = constant @loc="4:54" @5;
                            %15 : java.type:"int" = add %13 %14 @loc="4:45";
                            yield %15 @loc="4:19";
                        };
                    %16 : Var<java.type:"int"> = var %6 @loc="4:5" @"result2";
                    %17 : Var<java.type:"int"> = var @loc="6:5" @"result3";
                    java.if @loc="7:5"
                        ()java.type:"boolean" -> {
                            %18 : java.type:"int" = var.load %16 @loc="7:9";
                            %19 : java.type:"int" = constant @loc="7:19" @5;
                            %20 : java.type:"boolean" = gt %18 %19 @loc="7:9";
                            yield %20 @loc="7:5";
                        }
                        ()java.type:"void" -> {
                            %21 : java.type:"int" = var.load %16 @loc="8:19";
                            %22 : java.type:"int" = constant @loc="8:29" @5;
                            %23 : java.type:"int" = sub %21 %22 @loc="8:19";
                            var.store %17 %23 @loc="8:9";
                            yield @loc="7:5";
                        }
                        ()java.type:"void" -> {
                            %24 : java.type:"int" = var.load %16 @loc="10:19";
                            %25 : java.type:"int" = constant @loc="10:29" @5;
                            %26 : java.type:"int" = add %24 %25 @loc="10:19";
                            var.store %17 %26 @loc="10:9";
                            yield @loc="7:5";
                        };
                    %27 : java.type:"int" = var.load %17 @loc="13:16";
                    %28 : java.type:"java.lang.Integer" = invoke %27 @loc="13:5" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    invoke %28 @loc="13:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    return @loc="1:1";
                };""", op.toText());

        assertEquals("""
                func @loc="1:1:ConditionalExpression.java" @"main" (%0 : java.type:"ConditionalExpression")java.type:"void" -> {
                    %1 : Var<java.type:"int"> = var @loc="2:18";
                    java.if @loc="2:18"
                        ()java.type:"boolean" -> {
                            %2 : java.type:"boolean" = invoke %0 @loc="2:18" @java.ref:"ConditionalExpression::condition():boolean";
                            yield %2 @loc="2:18";
                        }
                        ()java.type:"void" -> {
                            %3 : java.type:"int" = constant @loc="2:32" @5;
                            var.store %1 %3 @loc="2:18";
                            yield @loc="2:18";
                        }
                        ()java.type:"void" -> {
                            %4 : java.type:"int" = constant @loc="2:36" @10;
                            var.store %1 %4 @loc="2:18";
                            yield @loc="2:18";
                        };
                    %5 : java.type:"int" = var.load %1 @loc="2:18";
                    %6 : Var<java.type:"int"> = var %5 @loc="2:5" @"result";
                    %7 : Var<java.type:"int"> = var @loc="4:19";
                    java.if @loc="4:19"
                        ()java.type:"boolean" -> {
                            %8 : java.type:"int" = var.load %6 @loc="4:19";
                            %9 : java.type:"int" = constant @loc="4:28" @5;
                            %10 : java.type:"boolean" = gt %8 %9 @loc="4:19";
                            yield %10 @loc="4:19";
                        }
                        ()java.type:"void" -> {
                            %11 : java.type:"int" = var.load %6 @loc="4:32";
                            %12 : java.type:"int" = constant @loc="4:41" @5;
                            %13 : java.type:"int" = sub %11 %12 @loc="4:32";
                            var.store %7 %13 @loc="4:19";
                            yield @loc="4:19";
                        }
                        ()java.type:"void" -> {
                            %14 : java.type:"int" = var.load %6 @loc="4:45";
                            %15 : java.type:"int" = constant @loc="4:54" @5;
                            %16 : java.type:"int" = add %14 %15 @loc="4:45";
                            var.store %7 %16 @loc="4:19";
                            yield @loc="4:19";
                        };
                    %17 : java.type:"int" = var.load %7 @loc="4:19";
                    %18 : Var<java.type:"int"> = var %17 @loc="4:5" @"result2";
                    %19 : Var<java.type:"int"> = var @loc="6:5" @"result3";
                    java.if @loc="7:5"
                        ()java.type:"boolean" -> {
                            %20 : java.type:"int" = var.load %18 @loc="7:9";
                            %21 : java.type:"int" = constant @loc="7:19" @5;
                            %22 : java.type:"boolean" = gt %20 %21 @loc="7:9";
                            yield %22 @loc="7:5";
                        }
                        ()java.type:"void" -> {
                            %23 : java.type:"int" = var.load %18 @loc="8:19";
                            %24 : java.type:"int" = constant @loc="8:29" @5;
                            %25 : java.type:"int" = sub %23 %24 @loc="8:19";
                            var.store %19 %25 @loc="8:9";
                            yield @loc="7:5";
                        }
                        ()java.type:"void" -> {
                            %26 : java.type:"int" = var.load %18 @loc="10:19";
                            %27 : java.type:"int" = constant @loc="10:29" @5;
                            %28 : java.type:"int" = add %26 %27 @loc="10:19";
                            var.store %19 %28 @loc="10:9";
                            yield @loc="7:5";
                        };
                    %29 : java.type:"int" = var.load %19 @loc="13:16";
                    %30 : java.type:"java.lang.Integer" = invoke %29 @loc="13:5" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    invoke %30 @loc="13:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    return @loc="1:1";
                };""", transformedOp.toText());
    }
}
