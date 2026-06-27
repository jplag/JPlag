package de.jplag.java.babylon.transformer.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.jplag.java.babylon.pipeline.TransformationPipeline;

import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Unit test for {@link StringConcatDesugarStep}.
 */
public class StringConcatDesugarStepTest extends AbstractTransformerTest {
    /**
     * Unit test for {@link StringConcatDesugarStep}.
     */
    @Test
    public void testTransformer() {
        AbstractTransformerTest.ParseResult parseResult = assertDoesNotThrow(() -> parseFile("StringConcat.java"));
        CoreOp.FuncOp op = parseResult.extractCodeModel();
        CoreOp.FuncOp transformedOp = parseResult.extractCodeModel(new TransformationPipeline(List.of(new StringConcatDesugarStep())));
        assertEquals(
                """
                        func @loc="1:1:StringConcat.java" @"main" (%0 : java.type:"StringConcat")java.type:"void" -> {
                            %1 : java.type:"java.lang.String" = constant @loc="2:16" @"Hello World ";
                            %2 : java.type:"int" = constant @loc="2:38" @12;
                            %3 : java.type:"java.lang.String" = concat %1 %2 @loc="2:16";
                            %4 : java.type:"java.lang.String" = constant @loc="2:43" @" (";
                            %5 : java.type:"java.lang.String" = concat %3 %4 @loc="2:16";
                            %6 : java.type:"boolean" = java.cor @loc="2:51"
                                ()java.type:"boolean" -> {
                                    %7 : java.type:"boolean" = constant @loc="2:51" @true;
                                    yield %7 @loc="2:51";
                                }
                                ()java.type:"boolean" -> {
                                    %8 : java.type:"boolean" = constant @loc="2:59" @false;
                                    yield %8 @loc="2:51";
                                };
                            %9 : java.type:"java.lang.String" = concat %5 %6 @loc="2:16";
                            %10 : java.type:"java.lang.String" = constant @loc="2:68" @")";
                            %11 : java.type:"java.lang.String" = concat %9 %10 @loc="2:16";
                            invoke %11 @loc="2:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                            %12 : java.type:"java.lang.String" = constant @loc="4:21" @"";
                            %13 : Var<java.type:"java.lang.String"> = var %12 @loc="4:5" @"result";
                            java.for @loc="5:5"
                                ()Var<java.type:"int"> -> {
                                    %14 : java.type:"int" = constant @loc="5:18" @0;
                                    %15 : Var<java.type:"int"> = var %14 @loc="5:10" @"i";
                                    yield %15 @loc="5:5";
                                }
                                (%16 : Var<java.type:"int">)java.type:"boolean" -> {
                                    %17 : java.type:"int" = var.load %16 @loc="5:21";
                                    %18 : java.type:"int" = constant @loc="5:25" @100;
                                    %19 : java.type:"boolean" = lt %17 %18 @loc="5:21";
                                    yield %19 @loc="5:5";
                                }
                                (%20 : Var<java.type:"int">)java.type:"void" -> {
                                    %21 : java.type:"int" = var.load %20 @loc="5:30";
                                    %22 : java.type:"int" = constant @loc="5:30" @1;
                                    %23 : java.type:"int" = add %21 %22 @loc="5:30";
                                    var.store %20 %23 @loc="5:30";
                                    yield @loc="5:5";
                                }
                                (%24 : Var<java.type:"int">)java.type:"void" -> {
                                    %25 : java.type:"java.lang.String" = var.load %13 @loc="6:9";
                                    %26 : java.type:"java.lang.String" = constant @loc="6:19" @"\\n";
                                    %27 : java.type:"java.lang.String" = java.cexpression @loc="6:27"
                                        ()java.type:"boolean" -> {
                                            %28 : java.type:"int" = var.load %24 @loc="6:27";
                                            %29 : java.type:"int" = constant @loc="6:31" @15;
                                            %30 : java.type:"int" = mod %28 %29 @loc="6:27";
                                            %31 : java.type:"int" = constant @loc="6:37" @0;
                                            %32 : java.type:"boolean" = eq %30 %31 @loc="6:27";
                                            yield %32 @loc="6:27";
                                        }
                                        ()java.type:"java.lang.String" -> {
                                            %33 : java.type:"java.lang.String" = constant @loc="6:41" @"FizzBuzz";
                                            yield %33 @loc="6:27";
                                        }
                                        ()java.type:"java.lang.String" -> {
                                            %34 : java.type:"java.lang.String" = java.cexpression @loc="6:54"
                                                ()java.type:"boolean" -> {
                                                    %35 : java.type:"int" = var.load %24 @loc="6:54";
                                                    %36 : java.type:"int" = constant @loc="6:58" @5;
                                                    %37 : java.type:"int" = mod %35 %36 @loc="6:54";
                                                    %38 : java.type:"int" = constant @loc="6:63" @0;
                                                    %39 : java.type:"boolean" = eq %37 %38 @loc="6:54";
                                                    yield %39 @loc="6:54";
                                                }
                                                ()java.type:"java.lang.String" -> {
                                                    %40 : java.type:"java.lang.String" = constant @loc="6:67" @"Buzz";
                                                    yield %40 @loc="6:54";
                                                }
                                                ()java.type:"java.lang.String" -> {
                                                    %41 : java.type:"java.lang.String" = java.cexpression @loc="6:76"
                                                        ()java.type:"boolean" -> {
                                                            %42 : java.type:"int" = var.load %24 @loc="6:76";
                                                            %43 : java.type:"int" = constant @loc="6:80" @3;
                                                            %44 : java.type:"int" = mod %42 %43 @loc="6:76";
                                                            %45 : java.type:"int" = constant @loc="6:85" @0;
                                                            %46 : java.type:"boolean" = eq %44 %45 @loc="6:76";
                                                            yield %46 @loc="6:76";
                                                        }
                                                        ()java.type:"java.lang.String" -> {
                                                            %47 : java.type:"java.lang.String" = constant @loc="6:89" @"Fizz";
                                                            yield %47 @loc="6:76";
                                                        }
                                                        ()java.type:"java.lang.String" -> {
                                                            %48 : java.type:"int" = var.load %24 @loc="6:115";
                                                            %49 : java.type:"java.lang.String" = invoke %48 @loc="6:98" @java.ref:"java.lang.Integer::toString(int):java.lang.String";
                                                            yield %49 @loc="6:76";
                                                        };
                                                    yield %41 @loc="6:54";
                                                };
                                            yield %34 @loc="6:27";
                                        };
                                    %50 : java.type:"java.lang.String" = concat %26 %27 @loc="6:19";
                                    %51 : java.type:"java.lang.String" = concat %25 %50 @loc="6:9";
                                    var.store %13 %51 @loc="6:9";
                                    java.continue @loc="5:5";
                                };
                            %52 : java.type:"java.lang.String" = var.load %13 @loc="9:16";
                            invoke %52 @loc="9:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                            return @loc="1:1";
                        };""",
                op.toText());
        assertEquals(
                """
                        func @loc="1:1:StringConcat.java" @"main" (%0 : java.type:"StringConcat")java.type:"void" -> {
                            %1 : java.type:"java.lang.String" = constant @loc="2:16" @"Hello World ";
                            %2 : java.type:"int" = constant @loc="2:38" @12;
                            %3 : java.type:"java.lang.String" = constant @loc="2:43" @" (";
                            %4 : java.type:"boolean" = java.cor @loc="2:51"
                                ()java.type:"boolean" -> {
                                    %5 : java.type:"boolean" = constant @loc="2:51" @true;
                                    yield %5 @loc="2:51";
                                }
                                ()java.type:"boolean" -> {
                                    %6 : java.type:"boolean" = constant @loc="2:59" @false;
                                    yield %6 @loc="2:51";
                                };
                            %7 : java.type:"java.lang.String" = constant @loc="2:68" @")";
                            %8 : java.type:"java.lang.StringBuilder" = new @java.ref:"java.lang.StringBuilder::()";
                            %9 : java.type:"java.lang.StringBuilder" = invoke %8 %1 @java.ref:"java.lang.StringBuilder::append(java.lang.String):java.lang.StringBuilder";
                            %10 : java.type:"java.lang.StringBuilder" = invoke %8 %2 @java.ref:"java.lang.StringBuilder::append(int):java.lang.StringBuilder";
                            %11 : java.type:"java.lang.StringBuilder" = invoke %8 %3 @java.ref:"java.lang.StringBuilder::append(java.lang.String):java.lang.StringBuilder";
                            %12 : java.type:"java.lang.StringBuilder" = invoke %8 %4 @java.ref:"java.lang.StringBuilder::append(boolean):java.lang.StringBuilder";
                            %13 : java.type:"java.lang.StringBuilder" = invoke %8 %7 @java.ref:"java.lang.StringBuilder::append(java.lang.String):java.lang.StringBuilder";
                            %14 : java.type:"java.lang.String" = invoke %8 @java.ref:"java.lang.StringBuilder::toString():java.lang.String";
                            invoke %14 @loc="2:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                            %15 : java.type:"java.lang.String" = constant @loc="4:21" @"";
                            %16 : Var<java.type:"java.lang.String"> = var %15 @loc="4:5" @"result";
                            java.for @loc="5:5"
                                ()Var<java.type:"int"> -> {
                                    %17 : java.type:"int" = constant @loc="5:18" @0;
                                    %18 : Var<java.type:"int"> = var %17 @loc="5:10" @"i";
                                    yield %18 @loc="5:5";
                                }
                                (%19 : Var<java.type:"int">)java.type:"boolean" -> {
                                    %20 : java.type:"int" = var.load %19 @loc="5:21";
                                    %21 : java.type:"int" = constant @loc="5:25" @100;
                                    %22 : java.type:"boolean" = lt %20 %21 @loc="5:21";
                                    yield %22 @loc="5:5";
                                }
                                (%23 : Var<java.type:"int">)java.type:"void" -> {
                                    %24 : java.type:"int" = var.load %23 @loc="5:30";
                                    %25 : java.type:"int" = constant @loc="5:30" @1;
                                    %26 : java.type:"int" = add %24 %25 @loc="5:30";
                                    var.store %23 %26 @loc="5:30";
                                    yield @loc="5:5";
                                }
                                (%27 : Var<java.type:"int">)java.type:"void" -> {
                                    %28 : java.type:"java.lang.String" = var.load %16 @loc="6:9";
                                    %29 : java.type:"java.lang.String" = constant @loc="6:19" @"\\n";
                                    %30 : java.type:"java.lang.String" = java.cexpression @loc="6:27"
                                        ()java.type:"boolean" -> {
                                            %31 : java.type:"int" = var.load %27 @loc="6:27";
                                            %32 : java.type:"int" = constant @loc="6:31" @15;
                                            %33 : java.type:"int" = mod %31 %32 @loc="6:27";
                                            %34 : java.type:"int" = constant @loc="6:37" @0;
                                            %35 : java.type:"boolean" = eq %33 %34 @loc="6:27";
                                            yield %35 @loc="6:27";
                                        }
                                        ()java.type:"java.lang.String" -> {
                                            %36 : java.type:"java.lang.String" = constant @loc="6:41" @"FizzBuzz";
                                            yield %36 @loc="6:27";
                                        }
                                        ()java.type:"java.lang.String" -> {
                                            %37 : java.type:"java.lang.String" = java.cexpression @loc="6:54"
                                                ()java.type:"boolean" -> {
                                                    %38 : java.type:"int" = var.load %27 @loc="6:54";
                                                    %39 : java.type:"int" = constant @loc="6:58" @5;
                                                    %40 : java.type:"int" = mod %38 %39 @loc="6:54";
                                                    %41 : java.type:"int" = constant @loc="6:63" @0;
                                                    %42 : java.type:"boolean" = eq %40 %41 @loc="6:54";
                                                    yield %42 @loc="6:54";
                                                }
                                                ()java.type:"java.lang.String" -> {
                                                    %43 : java.type:"java.lang.String" = constant @loc="6:67" @"Buzz";
                                                    yield %43 @loc="6:54";
                                                }
                                                ()java.type:"java.lang.String" -> {
                                                    %44 : java.type:"java.lang.String" = java.cexpression @loc="6:76"
                                                        ()java.type:"boolean" -> {
                                                            %45 : java.type:"int" = var.load %27 @loc="6:76";
                                                            %46 : java.type:"int" = constant @loc="6:80" @3;
                                                            %47 : java.type:"int" = mod %45 %46 @loc="6:76";
                                                            %48 : java.type:"int" = constant @loc="6:85" @0;
                                                            %49 : java.type:"boolean" = eq %47 %48 @loc="6:76";
                                                            yield %49 @loc="6:76";
                                                        }
                                                        ()java.type:"java.lang.String" -> {
                                                            %50 : java.type:"java.lang.String" = constant @loc="6:89" @"Fizz";
                                                            yield %50 @loc="6:76";
                                                        }
                                                        ()java.type:"java.lang.String" -> {
                                                            %51 : java.type:"int" = var.load %27 @loc="6:115";
                                                            %52 : java.type:"java.lang.String" = invoke %51 @loc="6:98" @java.ref:"java.lang.Integer::toString(int):java.lang.String";
                                                            yield %52 @loc="6:76";
                                                        };
                                                    yield %44 @loc="6:54";
                                                };
                                            yield %37 @loc="6:27";
                                        };
                                    %53 : java.type:"java.lang.StringBuilder" = new @java.ref:"java.lang.StringBuilder::()";
                                    %54 : java.type:"java.lang.StringBuilder" = invoke %53 %28 @java.ref:"java.lang.StringBuilder::append(java.lang.String):java.lang.StringBuilder";
                                    %55 : java.type:"java.lang.StringBuilder" = invoke %53 %29 @java.ref:"java.lang.StringBuilder::append(java.lang.String):java.lang.StringBuilder";
                                    %56 : java.type:"java.lang.StringBuilder" = invoke %53 %30 @java.ref:"java.lang.StringBuilder::append(java.lang.String):java.lang.StringBuilder";
                                    %57 : java.type:"java.lang.String" = invoke %53 @java.ref:"java.lang.StringBuilder::toString():java.lang.String";
                                    var.store %16 %57 @loc="6:9";
                                    java.continue @loc="5:5";
                                };
                            %58 : java.type:"java.lang.String" = var.load %16 @loc="9:16";
                            invoke %58 @loc="9:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                            return @loc="1:1";
                        };""",
                transformedOp.toText());
    }
}
