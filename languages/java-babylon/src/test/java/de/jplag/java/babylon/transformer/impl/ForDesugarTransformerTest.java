package de.jplag.java.babylon.transformer.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Unit test for {@link ForDesugarTransformer}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ForDesugarTransformerTest extends AbstractTransformerTest {
    /**
     * Unit test for {@link ForDesugarTransformer}.
     */
    @Test
    public void testTransformer() {
        CoreOp.FuncOp op = assertDoesNotThrow(() -> parseFile("For.java")).extractCodeModel();
        CoreOp.FuncOp transformedOp = op.transform(new EnhancedForDesugarTransformer()).transform(new ForDesugarTransformer());

        assertEquals(
                """
                        func @loc="1:1:For.java" @"main" (%0 : java.type:"For")java.type:"void" -> {
                            java.for @loc="2:5"
                                ()Var<java.type:"int"> -> {
                                    %1 : java.type:"int" = constant @loc="2:18" @0;
                                    %2 : Var<java.type:"int"> = var %1 @loc="2:10" @"i";
                                    yield %2 @loc="2:5";
                                }
                                (%3 : Var<java.type:"int">)java.type:"boolean" -> {
                                    %4 : java.type:"int" = var.load %3 @loc="2:21";
                                    %5 : java.type:"int" = constant @loc="2:25" @10;
                                    %6 : java.type:"boolean" = lt %4 %5 @loc="2:21";
                                    yield %6 @loc="2:5";
                                }
                                (%7 : Var<java.type:"int">)java.type:"void" -> {
                                    yield @loc="2:5";
                                }
                                (%8 : Var<java.type:"int">)java.type:"void" -> {
                                    java.break @loc="3:9";
                                };
                            java.for @loc="6:5"
                                ()Var<java.type:"int"> -> {
                                    %9 : java.type:"int" = constant @loc="6:18" @0;
                                    %10 : Var<java.type:"int"> = var %9 @loc="6:10" @"i";
                                    yield %10 @loc="6:5";
                                }
                                (%11 : Var<java.type:"int">)java.type:"boolean" -> {
                                    %12 : java.type:"boolean" = constant @loc="6:21" @true;
                                    yield %12 @loc="6:5";
                                }
                                (%13 : Var<java.type:"int">)java.type:"void" -> {
                                    %14 : java.type:"int" = var.load %13 @loc="6:27";
                                    %15 : java.type:"int" = constant @loc="6:27" @1;
                                    %16 : java.type:"int" = add %14 %15 @loc="6:27";
                                    var.store %13 %16 @loc="6:27";
                                    yield @loc="6:5";
                                }
                                (%17 : Var<java.type:"int">)java.type:"void" -> {
                                    java.if @loc="7:9"
                                        ()java.type:"boolean" -> {
                                            %18 : java.type:"int" = var.load %17 @loc="7:13";
                                            %19 : java.type:"int" = constant @loc="7:18" @10;
                                            %20 : java.type:"boolean" = ge %18 %19 @loc="7:13";
                                            yield %20 @loc="7:9";
                                        }
                                        ()java.type:"void" -> {
                                            java.break @loc="7:22";
                                        }
                                        ()java.type:"void" -> {
                                            yield;
                                        };
                                    java.continue @loc="6:5";
                                };
                            %21 : java.type:"int" = constant @loc="10:20" @4;
                            %22 : java.type:"int[]" = new %21 @loc="10:20" @java.ref:"int[]::(int)";
                            %23 : java.type:"int" = constant @loc="10:21" @1;
                            %24 : java.type:"int" = constant @loc="10:20" @0;
                            array.store %22 %24 %23 @loc="10:20";
                            %25 : java.type:"int" = constant @loc="10:24" @2;
                            %26 : java.type:"int" = constant @loc="10:20" @1;
                            array.store %22 %26 %25 @loc="10:20";
                            %27 : java.type:"int" = constant @loc="10:27" @3;
                            %28 : java.type:"int" = constant @loc="10:20" @2;
                            array.store %22 %28 %27 @loc="10:20";
                            %29 : java.type:"int" = constant @loc="10:30" @4;
                            %30 : java.type:"int" = constant @loc="10:20" @3;
                            array.store %22 %30 %29 @loc="10:20";
                            %31 : Var<java.type:"int[]"> = var %22 @loc="10:5" @"values";
                            %32 : java.type:"int" = constant @loc="11:13" @0;
                            %33 : Var<java.type:"int"> = var %32 @loc="11:5" @"i";
                            java.for @loc="12:5"
                                ()java.type:"void" -> {
                                    yield @loc="12:5";
                                }
                                ()java.type:"boolean" -> {
                                    %34 : java.type:"int" = var.load %33 @loc="12:12";
                                    %35 : java.type:"int[]" = var.load %31 @loc="12:16";
                                    %36 : java.type:"int" = array.length %35 @loc="12:16";
                                    %37 : java.type:"boolean" = lt %34 %36 @loc="12:12";
                                    yield %37 @loc="12:5";
                                }
                                ()java.type:"void" -> {
                                    %38 : java.type:"int" = var.load %33 @loc="12:31";
                                    %39 : java.type:"int" = constant @loc="12:31" @1;
                                    %40 : java.type:"int" = add %38 %39 @loc="12:31";
                                    var.store %33 %40 @loc="12:31";
                                    yield @loc="12:5";
                                }
                                ()java.type:"void" -> {
                                    %41 : java.type:"int[]" = var.load %31 @loc="13:20";
                                    %42 : java.type:"int" = var.load %33 @loc="13:27";
                                    %43 : java.type:"int" = array.load %41 %42 @loc="13:20";
                                    %44 : java.type:"java.lang.Integer" = invoke %43 @loc="13:9" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    invoke %44 @loc="13:9" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.continue @loc="12:5";
                                };
                            java.enhancedFor @loc="16:5"
                                ()java.type:"int[]" -> {
                                    %45 : java.type:"int[]" = var.load %31 @loc="16:22";
                                    yield %45 @loc="16:5";
                                }
                                (%46 : java.type:"int")Var<java.type:"int"> -> {
                                    %47 : Var<java.type:"int"> = var %46 @loc="16:5" @"value";
                                    yield %47 @loc="16:5";
                                }
                                (%48 : Var<java.type:"int">)java.type:"void" -> {
                                    %49 : java.type:"int" = var.load %48 @loc="17:20";
                                    %50 : java.type:"java.lang.Integer" = invoke %49 @loc="17:9" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    invoke %50 @loc="17:9" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.continue @loc="16:5";
                                };
                            return @loc="1:1";
                        };""",
                op.toText());
        assertEquals(
                """
                        func @loc="1:1:For.java" @"main" (%0 : java.type:"For")java.type:"void" -> {
                            %1 : java.type:"int" = constant @loc="2:18" @0;
                            %2 : Var<java.type:"int"> = var %1 @loc="2:10" @"i";
                            java.while @loc="2:5"
                                ()java.type:"boolean" -> {
                                    %3 : java.type:"int" = var.load %2 @loc="2:21";
                                    %4 : java.type:"int" = constant @loc="2:25" @10;
                                    %5 : java.type:"boolean" = lt %3 %4 @loc="2:21";
                                    yield %5 @loc="2:5";
                                }
                                ()java.type:"void" -> {
                                    java.break @loc="3:9";
                                };
                            %6 : java.type:"int" = constant @loc="6:18" @0;
                            %7 : Var<java.type:"int"> = var %6 @loc="6:10" @"i";
                            java.while @loc="6:5"
                                ()java.type:"boolean" -> {
                                    %8 : java.type:"boolean" = constant @loc="6:21" @true;
                                    yield %8 @loc="6:5";
                                }
                                ()java.type:"void" -> {
                                    java.if @loc="7:9"
                                        ()java.type:"boolean" -> {
                                            %9 : java.type:"int" = var.load %7 @loc="7:13";
                                            %10 : java.type:"int" = constant @loc="7:18" @10;
                                            %11 : java.type:"boolean" = ge %9 %10 @loc="7:13";
                                            yield %11 @loc="7:9";
                                        }
                                        ()java.type:"void" -> {
                                            java.break @loc="7:22";
                                        }
                                        ()java.type:"void" -> {
                                            yield;
                                        };
                                    %12 : java.type:"int" = var.load %7 @loc="6:27";
                                    %13 : java.type:"int" = constant @loc="6:27" @1;
                                    %14 : java.type:"int" = add %12 %13 @loc="6:27";
                                    var.store %7 %14 @loc="6:27";
                                    java.continue @loc="6:5";
                                };
                            %15 : java.type:"int" = constant @loc="10:20" @4;
                            %16 : java.type:"int[]" = new %15 @loc="10:20" @java.ref:"int[]::(int)";
                            %17 : java.type:"int" = constant @loc="10:21" @1;
                            %18 : java.type:"int" = constant @loc="10:20" @0;
                            array.store %16 %18 %17 @loc="10:20";
                            %19 : java.type:"int" = constant @loc="10:24" @2;
                            %20 : java.type:"int" = constant @loc="10:20" @1;
                            array.store %16 %20 %19 @loc="10:20";
                            %21 : java.type:"int" = constant @loc="10:27" @3;
                            %22 : java.type:"int" = constant @loc="10:20" @2;
                            array.store %16 %22 %21 @loc="10:20";
                            %23 : java.type:"int" = constant @loc="10:30" @4;
                            %24 : java.type:"int" = constant @loc="10:20" @3;
                            array.store %16 %24 %23 @loc="10:20";
                            %25 : Var<java.type:"int[]"> = var %16 @loc="10:5" @"values";
                            %26 : java.type:"int" = constant @loc="11:13" @0;
                            %27 : Var<java.type:"int"> = var %26 @loc="11:5" @"i";
                            java.while @loc="12:5"
                                ()java.type:"boolean" -> {
                                    %28 : java.type:"int" = var.load %27 @loc="12:12";
                                    %29 : java.type:"int[]" = var.load %25 @loc="12:16";
                                    %30 : java.type:"int" = array.length %29 @loc="12:16";
                                    %31 : java.type:"boolean" = lt %28 %30 @loc="12:12";
                                    yield %31 @loc="12:5";
                                }
                                ()java.type:"void" -> {
                                    %32 : java.type:"int[]" = var.load %25 @loc="13:20";
                                    %33 : java.type:"int" = var.load %27 @loc="13:27";
                                    %34 : java.type:"int" = array.load %32 %33 @loc="13:20";
                                    %35 : java.type:"java.lang.Integer" = invoke %34 @loc="13:9" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    invoke %35 @loc="13:9" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    %36 : java.type:"int" = var.load %27 @loc="12:31";
                                    %37 : java.type:"int" = constant @loc="12:31" @1;
                                    %38 : java.type:"int" = add %36 %37 @loc="12:31";
                                    var.store %27 %38 @loc="12:31";
                                    java.continue @loc="12:5";
                                };
                            java.while @loc="16:5"
                                ()java.type:"boolean" -> {
                                    %39 : java.type:"int[]" = var.load %25 @loc="16:5";
                                    %40 : java.type:"int[]" = var.load %25 @loc="16:5";
                                    %41 : java.type:"int" = array.length %39 @loc="16:5";
                                    %42 : java.type:"boolean" = lt %40 %41 @loc="16:5";
                                    yield %42 @loc="16:5";
                                }
                                ()java.type:"void" -> {
                                    %43 : java.type:"int[]" = var.load %25 @loc="16:5";
                                    %44 : java.type:"int[]" = var.load %25 @loc="16:5";
                                    %45 : java.type:"int" = array.load %43 %44 @loc="16:5";
                                    %46 : Var<java.type:"int"> = var %45 @loc="16:5" @"value";
                                    %47 : java.type:"int" = var.load %46 @loc="17:20";
                                    %48 : java.type:"java.lang.Integer" = invoke %47 @loc="17:9" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    invoke %48 @loc="17:9" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    %49 : java.type:"int[]" = var.load %25 @loc="16:5";
                                    %50 : java.type:"int" = constant @loc="16:5" @1;
                                    %51 : java.type:"int[]" = add %49 %50 @loc="16:5";
                                    var.store %25 %51 @loc="16:5";
                                    java.continue @loc="16:5";
                                };
                            return @loc="1:1";
                        };""",
                transformedOp.toText());
    }
}
