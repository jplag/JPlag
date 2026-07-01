package de.jplag.java.babylon.transformer.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Unit test for {@link EnhancedForDesugarTransformer}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EnhancedForDesugarTransformerTest extends AbstractTransformerTest {
    /**
     * Unit test for {@link EnhancedForDesugarTransformer}.
     */
    @Test
    public void testTransformer() {
        ParseResult parseResult = assertDoesNotThrow(() -> parseFile("EnhancedFor.java"));
        CoreOp.FuncOp op = parseResult.extractCodeModel();
        CoreOp.FuncOp transformedOp = parseResult.extractCodeModel(pipeline(step(new EnhancedForDesugarTransformer())));

        assertEquals(
                """
                        func @loc="1:1:EnhancedFor.java" @"main" (%0 : java.type:"EnhancedFor")java.type:"void" -> {
                            %1 : java.type:"int" = constant @loc="2:20" @3;
                            %2 : java.type:"int[]" = new %1 @loc="2:20" @java.ref:"int[]::(int)";
                            %3 : java.type:"int" = constant @loc="2:21" @1;
                            %4 : java.type:"int" = constant @loc="2:20" @0;
                            array.store %2 %4 %3 @loc="2:20";
                            %5 : java.type:"int" = constant @loc="2:24" @2;
                            %6 : java.type:"int" = constant @loc="2:20" @1;
                            array.store %2 %6 %5 @loc="2:20";
                            %7 : java.type:"int" = constant @loc="2:27" @3;
                            %8 : java.type:"int" = constant @loc="2:20" @2;
                            array.store %2 %8 %7 @loc="2:20";
                            %9 : Var<java.type:"int[]"> = var %2 @loc="2:5" @"values";
                            java.enhancedFor @loc="3:5"
                                ()java.type:"int[]" -> {
                                    %10 : java.type:"int[]" = var.load %9 @loc="3:22";
                                    yield %10 @loc="3:5";
                                }
                                (%11 : java.type:"int")Var<java.type:"int"> -> {
                                    %12 : Var<java.type:"int"> = var %11 @loc="3:5" @"value";
                                    yield %12 @loc="3:5";
                                }
                                (%13 : Var<java.type:"int">)java.type:"void" -> {
                                    %14 : java.type:"int" = var.load %13 @loc="4:20";
                                    %15 : java.type:"java.lang.Integer" = invoke %14 @loc="4:9" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    invoke %15 @loc="4:9" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.continue @loc="3:5";
                                };
                            java.for @loc="6:5"
                                ()Var<java.type:"int"> -> {
                                    %16 : java.type:"int" = constant @loc="6:18" @0;
                                    %17 : Var<java.type:"int"> = var %16 @loc="6:10" @"i";
                                    yield %17 @loc="6:5";
                                }
                                (%18 : Var<java.type:"int">)java.type:"boolean" -> {
                                    %19 : java.type:"int" = var.load %18 @loc="6:21";
                                    %20 : java.type:"int[]" = var.load %9 @loc="6:25";
                                    %21 : java.type:"int" = array.length %20 @loc="6:25";
                                    %22 : java.type:"boolean" = lt %19 %21 @loc="6:21";
                                    yield %22 @loc="6:5";
                                }
                                (%23 : Var<java.type:"int">)java.type:"void" -> {
                                    %24 : java.type:"int" = var.load %23 @loc="6:40";
                                    %25 : java.type:"int" = constant @loc="6:40" @1;
                                    %26 : java.type:"int" = add %24 %25 @loc="6:40";
                                    var.store %23 %26 @loc="6:40";
                                    yield @loc="6:5";
                                }
                                (%27 : Var<java.type:"int">)java.type:"void" -> {
                                    %28 : java.type:"int[]" = var.load %9 @loc="7:20";
                                    %29 : java.type:"int" = var.load %27 @loc="7:27";
                                    %30 : java.type:"int" = array.load %28 %29 @loc="7:20";
                                    %31 : java.type:"java.lang.Integer" = invoke %30 @loc="7:9" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    invoke %31 @loc="7:9" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.continue @loc="6:5";
                                };
                            java.for @loc="9:5"
                                ()Tuple<Var<java.type:"int[]">, Var<java.type:"int">> -> {
                                    %32 : java.type:"int" = constant @loc="9:20" @2;
                                    %33 : java.type:"int[]" = new %32 @loc="9:20" @java.ref:"int[]::(int)";
                                    %34 : java.type:"int" = constant @loc="9:21" @1;
                                    %35 : java.type:"int" = constant @loc="9:20" @0;
                                    array.store %33 %35 %34 @loc="9:20";
                                    %36 : java.type:"int" = constant @loc="9:24" @2;
                                    %37 : java.type:"int" = constant @loc="9:20" @1;
                                    array.store %33 %37 %36 @loc="9:20";
                                    %38 : Var<java.type:"int[]"> = var %33 @loc="9:10" @"a";
                                    %39 : java.type:"int" = constant @loc="9:32" @0;
                                    %40 : Var<java.type:"int"> = var %39 @loc="9:10" @"i";
                                    %41 : Tuple<Var<java.type:"int[]">, Var<java.type:"int">> = tuple %38 %40 @loc="9:5";
                                    yield %41 @loc="9:5";
                                }
                                (%42 : Var<java.type:"int[]">, %43 : Var<java.type:"int">)java.type:"boolean" -> {
                                    %44 : java.type:"int" = var.load %43 @loc="9:35";
                                    %45 : java.type:"int[]" = var.load %42 @loc="9:39";
                                    %46 : java.type:"int" = array.length %45 @loc="9:39";
                                    %47 : java.type:"boolean" = lt %44 %46 @loc="9:35";
                                    yield %47 @loc="9:5";
                                }
                                (%48 : Var<java.type:"int[]">, %49 : Var<java.type:"int">)java.type:"void" -> {
                                    %50 : java.type:"int" = var.load %49 @loc="9:49";
                                    %51 : java.type:"int" = constant @loc="9:49" @1;
                                    %52 : java.type:"int" = add %50 %51 @loc="9:49";
                                    var.store %49 %52 @loc="9:49";
                                    yield @loc="9:5";
                                }
                                (%53 : Var<java.type:"int[]">, %54 : Var<java.type:"int">)java.type:"void" -> {
                                    %55 : java.type:"int[]" = var.load %53 @loc="10:20";
                                    %56 : java.type:"int" = var.load %54 @loc="10:22";
                                    %57 : java.type:"int" = array.load %55 %56 @loc="10:20";
                                    %58 : java.type:"java.lang.Integer" = invoke %57 @loc="10:9" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    invoke %58 @loc="10:9" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.continue @loc="9:5";
                                };
                            java.enhancedFor @loc="13:5"
                                ()java.type:"java.util.List<java.lang.Integer>" -> {
                                    %59 : java.type:"int" = constant @loc="13:30" @1;
                                    %60 : java.type:"java.lang.Integer" = invoke %59 @loc="13:22" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    %61 : java.type:"int" = constant @loc="13:33" @2;
                                    %62 : java.type:"java.lang.Integer" = invoke %61 @loc="13:22" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    %63 : java.type:"int" = constant @loc="13:36" @3;
                                    %64 : java.type:"java.lang.Integer" = invoke %63 @loc="13:22" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    %65 : java.type:"java.util.List<java.lang.Integer>" = invoke %60 %62 %64 @loc="13:22" @java.ref:"java.util.List::of(java.lang.Object, java.lang.Object, java.lang.Object):java.util.List";
                                    yield %65 @loc="13:5";
                                }
                                (%66 : java.type:"java.lang.Integer")Var<java.type:"int"> -> {
                                    %67 : java.type:"int" = invoke %66 @loc="13:5" @java.ref:"java.lang.Integer::intValue():int";
                                    %68 : Var<java.type:"int"> = var %67 @loc="13:5" @"value";
                                    yield %68 @loc="13:5";
                                }
                                (%69 : Var<java.type:"int">)java.type:"void" -> {
                                    %70 : java.type:"int" = var.load %69 @loc="14:20";
                                    %71 : java.type:"java.lang.Integer" = invoke %70 @loc="14:9" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    invoke %71 @loc="14:9" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.continue @loc="13:5";
                                };
                            return @loc="1:1";
                        };""",
                op.toText());

        assertEquals(
                """
                        func @loc="1:1:EnhancedFor.java" @"main" (%0 : java.type:"EnhancedFor")java.type:"void" -> {
                            %1 : java.type:"int" = constant @loc="2:20" @3;
                            %2 : java.type:"int[]" = new %1 @loc="2:20" @java.ref:"int[]::(int)";
                            %3 : java.type:"int" = constant @loc="2:21" @1;
                            %4 : java.type:"int" = constant @loc="2:20" @0;
                            array.store %2 %4 %3 @loc="2:20";
                            %5 : java.type:"int" = constant @loc="2:24" @2;
                            %6 : java.type:"int" = constant @loc="2:20" @1;
                            array.store %2 %6 %5 @loc="2:20";
                            %7 : java.type:"int" = constant @loc="2:27" @3;
                            %8 : java.type:"int" = constant @loc="2:20" @2;
                            array.store %2 %8 %7 @loc="2:20";
                            %9 : Var<java.type:"int[]"> = var %2 @loc="2:5" @"values";
                            java.for @loc="3:5"
                                ()Var<java.type:"int"> -> {
                                    yield %9 @loc="3:22";
                                }
                                (%10 : Var<java.type:"int">)java.type:"boolean" -> {
                                    %11 : java.type:"int[]" = var.load %9 @loc="3:5";
                                    %12 : java.type:"int" = var.load %10 @loc="3:5";
                                    %13 : java.type:"int" = array.length %11 @loc="3:5";
                                    %14 : java.type:"boolean" = lt %12 %13 @loc="3:5";
                                    yield %14 @loc="3:5";
                                }
                                (%15 : Var<java.type:"int">)java.type:"void" -> {
                                    %16 : java.type:"int" = var.load %15 @loc="3:5";
                                    %17 : java.type:"int" = constant @loc="3:5" @1;
                                    %18 : java.type:"int" = add %16 %17 @loc="3:5";
                                    var.store %15 %18 @loc="3:5";
                                    yield @loc="3:5";
                                }
                                (%19 : Var<java.type:"int">)java.type:"void" -> {
                                    %20 : java.type:"int[]" = var.load %9 @loc="3:5";
                                    %21 : java.type:"int" = var.load %19 @loc="3:5";
                                    %22 : java.type:"int" = array.load %20 %21 @loc="3:5";
                                    %23 : Var<java.type:"int"> = var %22 @loc="3:5" @"value";
                                    %24 : java.type:"int" = var.load %23 @loc="4:20";
                                    %25 : java.type:"java.lang.Integer" = invoke %24 @loc="4:9" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    invoke %25 @loc="4:9" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.continue @loc="3:5";
                                };
                            java.for @loc="6:5"
                                ()Var<java.type:"int"> -> {
                                    %26 : java.type:"int" = constant @loc="6:18" @0;
                                    %27 : Var<java.type:"int"> = var %26 @loc="6:10" @"i";
                                    yield %27 @loc="6:5";
                                }
                                (%28 : Var<java.type:"int">)java.type:"boolean" -> {
                                    %29 : java.type:"int" = var.load %28 @loc="6:21";
                                    %30 : java.type:"int[]" = var.load %9 @loc="6:25";
                                    %31 : java.type:"int" = array.length %30 @loc="6:25";
                                    %32 : java.type:"boolean" = lt %29 %31 @loc="6:21";
                                    yield %32 @loc="6:5";
                                }
                                (%33 : Var<java.type:"int">)java.type:"void" -> {
                                    %34 : java.type:"int" = var.load %33 @loc="6:40";
                                    %35 : java.type:"int" = constant @loc="6:40" @1;
                                    %36 : java.type:"int" = add %34 %35 @loc="6:40";
                                    var.store %33 %36 @loc="6:40";
                                    yield @loc="6:5";
                                }
                                (%37 : Var<java.type:"int">)java.type:"void" -> {
                                    %38 : java.type:"int[]" = var.load %9 @loc="7:20";
                                    %39 : java.type:"int" = var.load %37 @loc="7:27";
                                    %40 : java.type:"int" = array.load %38 %39 @loc="7:20";
                                    %41 : java.type:"java.lang.Integer" = invoke %40 @loc="7:9" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    invoke %41 @loc="7:9" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.continue @loc="6:5";
                                };
                            java.for @loc="9:5"
                                ()Tuple<Var<java.type:"int[]">, Var<java.type:"int">> -> {
                                    %42 : java.type:"int" = constant @loc="9:20" @2;
                                    %43 : java.type:"int[]" = new %42 @loc="9:20" @java.ref:"int[]::(int)";
                                    %44 : java.type:"int" = constant @loc="9:21" @1;
                                    %45 : java.type:"int" = constant @loc="9:20" @0;
                                    array.store %43 %45 %44 @loc="9:20";
                                    %46 : java.type:"int" = constant @loc="9:24" @2;
                                    %47 : java.type:"int" = constant @loc="9:20" @1;
                                    array.store %43 %47 %46 @loc="9:20";
                                    %48 : Var<java.type:"int[]"> = var %43 @loc="9:10" @"a";
                                    %49 : java.type:"int" = constant @loc="9:32" @0;
                                    %50 : Var<java.type:"int"> = var %49 @loc="9:10" @"i";
                                    %51 : Tuple<Var<java.type:"int[]">, Var<java.type:"int">> = tuple %48 %50 @loc="9:5";
                                    yield %51 @loc="9:5";
                                }
                                (%52 : Var<java.type:"int[]">, %53 : Var<java.type:"int">)java.type:"boolean" -> {
                                    %54 : java.type:"int" = var.load %53 @loc="9:35";
                                    %55 : java.type:"int[]" = var.load %52 @loc="9:39";
                                    %56 : java.type:"int" = array.length %55 @loc="9:39";
                                    %57 : java.type:"boolean" = lt %54 %56 @loc="9:35";
                                    yield %57 @loc="9:5";
                                }
                                (%58 : Var<java.type:"int[]">, %59 : Var<java.type:"int">)java.type:"void" -> {
                                    %60 : java.type:"int" = var.load %59 @loc="9:49";
                                    %61 : java.type:"int" = constant @loc="9:49" @1;
                                    %62 : java.type:"int" = add %60 %61 @loc="9:49";
                                    var.store %59 %62 @loc="9:49";
                                    yield @loc="9:5";
                                }
                                (%63 : Var<java.type:"int[]">, %64 : Var<java.type:"int">)java.type:"void" -> {
                                    %65 : java.type:"int[]" = var.load %63 @loc="10:20";
                                    %66 : java.type:"int" = var.load %64 @loc="10:22";
                                    %67 : java.type:"int" = array.load %65 %66 @loc="10:20";
                                    %68 : java.type:"java.lang.Integer" = invoke %67 @loc="10:9" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    invoke %68 @loc="10:9" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.continue @loc="9:5";
                                };
                            java.for @loc="13:5"
                                ()Var<java.type:"java.util.Iterator<java.lang.Integer>"> -> {
                                    %69 : java.type:"int" = constant @loc="13:30" @1;
                                    %70 : java.type:"java.lang.Integer" = invoke %69 @loc="13:22" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    %71 : java.type:"int" = constant @loc="13:33" @2;
                                    %72 : java.type:"java.lang.Integer" = invoke %71 @loc="13:22" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    %73 : java.type:"int" = constant @loc="13:36" @3;
                                    %74 : java.type:"java.lang.Integer" = invoke %73 @loc="13:22" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    %75 : java.type:"java.util.List<java.lang.Integer>" = invoke %70 %72 %74 @loc="13:22" @java.ref:"java.util.List::of(java.lang.Object, java.lang.Object, java.lang.Object):java.util.List";
                                    %76 : java.type:"java.util.Iterator<java.lang.Integer>" = invoke %75 @loc="13:5" @java.ref:"java.lang.Iterable::iterator():java.util.Iterator";
                                    %77 : Var<java.type:"java.util.Iterator<java.lang.Integer>"> = var %76 @loc="13:5";
                                    yield %77 @loc="13:5";
                                }
                                (%78 : Var<java.type:"java.util.Iterator<java.lang.Integer>">)java.type:"boolean" -> {
                                    %79 : java.type:"java.util.Iterator<java.lang.Integer>" = var.load %78 @loc="13:5";
                                    %80 : java.type:"boolean" = invoke %79 @loc="13:5" @java.ref:"java.util.Iterator::hasNext():boolean";
                                    yield %80 @loc="13:5";
                                }
                                (%81 : Var<java.type:"java.util.Iterator<java.lang.Integer>">)java.type:"void" -> {
                                    yield @loc="13:5";
                                }
                                (%82 : Var<java.type:"java.util.Iterator<java.lang.Integer>">)java.type:"void" -> {
                                    %83 : java.type:"java.util.Iterator<java.lang.Integer>" = var.load %82 @loc="13:5";
                                    %84 : java.type:"java.lang.Object" = invoke %83 @loc="13:5" @java.ref:"java.util.Iterator::next():java.lang.Object";
                                    %85 : java.type:"int" = invoke %84 @loc="13:5" @java.ref:"java.lang.Integer::intValue():int";
                                    %86 : Var<java.type:"int"> = var %85 @loc="13:5" @"value";
                                    %87 : java.type:"int" = var.load %86 @loc="14:20";
                                    %88 : java.type:"java.lang.Integer" = invoke %87 @loc="14:9" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                                    invoke %88 @loc="14:9" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.continue @loc="13:5";
                                };
                            return @loc="1:1";
                        };""",
                transformedOp.toText());
    }
}
