package de.jplag.java.babylon.transformer.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Unit test for {@link SwitchExpressionDesugarTransformer}.
 */
public class SwitchExpressionDesugarTransformerTest extends AbstractTransformerTest {
    /**
     * Unit test for {@link SwitchExpressionDesugarTransformer}.
     */
    @Test
    public void testTransformer() {
        CoreOp.FuncOp op = assertDoesNotThrow(() -> parseFile("Switch.java")).extractCodeModel();
        CoreOp.FuncOp transformedOp = op.transform(new SwitchExpressionDesugarTransformer());

        assertEquals(
                """
                        func @loc="1:1:Switch.java" @"main" (%0 : java.type:"Switch")java.type:"void" -> {
                            %1 : java.type:"boolean" = invoke %0 @loc="2:27" @java.ref:"Switch::condition():boolean";
                            %2 : java.type:"Switch$ADT" = java.switch.expression %1 @loc="2:19"
                                (%3 : java.type:"boolean")java.type:"boolean" -> {
                                    %4 : java.type:"boolean" = constant @loc="3:14" @true;
                                    %5 : java.type:"boolean" = eq %3 %4 @loc="2:19";
                                    yield %5 @loc="2:19";
                                }
                                ()java.type:"Switch$ADT" -> {
                                    %6 : java.type:"int" = constant @loc="3:32" @10;
                                    %7 : java.type:"Switch$ADT$A" = new %6 @loc="3:22" @java.ref:"Switch$ADT$A::(int)";
                                    yield %7 @loc="2:19";
                                }
                                (%8 : java.type:"boolean")java.type:"boolean" -> {
                                    %9 : java.type:"boolean" = constant @loc="4:14" @false;
                                    %10 : java.type:"boolean" = eq %8 %9 @loc="2:19";
                                    yield %10 @loc="2:19";
                                }
                                ()java.type:"Switch$ADT" -> {
                                    %11 : java.type:"Switch$ADT$B" = new @loc="4:23" @java.ref:"Switch$ADT$B::()";
                                    yield %11 @loc="2:19";
                                }
                                ()java.type:"boolean" -> {
                                    %12 : java.type:"boolean" = constant @loc="2:19" @true;
                                    yield %12 @loc="2:19";
                                }
                                ()java.type:"Switch$ADT" -> {
                                    %13 : java.type:"java.lang.MatchException" = new @loc="2:19" @java.ref:"java.lang.MatchException::()";
                                    throw %13 @loc="2:19";
                                };
                            %14 : Var<java.type:"Switch$ADT"> = var %2 @loc="2:5" @"result1";
                            %15 : java.type:"Switch$ADT" = var.load %14 @loc="7:27";
                            %16 : java.type:"int" = constant @loc="7:19" @0;
                            %17 : java.type:"byte" = conv %16 @loc="7:19";
                            %18 : Var<java.type:"byte"> = var %17 @loc="7:19";
                            %19 : java.type:"int" = constant @loc="7:19" @0;
                            %20 : java.type:"short" = conv %19 @loc="7:19";
                            %21 : Var<java.type:"short"> = var %20 @loc="7:19" @"v";
                            %22 : java.type:"Switch$ADT" = java.switch.expression %15 @loc="7:19"
                                (%23 : java.type:"Switch$ADT")java.type:"boolean" -> {
                                    %24 : java.type:"boolean" = pattern.match %23 @loc="7:19"
                                        ()java.type:"jdk.incubator.code.dialect.java.JavaOp$Pattern$Record<Switch$ADT$A>" -> {
                                            %25 : java.type:"jdk.incubator.code.dialect.java.JavaOp$Pattern$Type<byte>" = pattern.type @loc="7:19";
                                            %26 : java.type:"jdk.incubator.code.dialect.java.JavaOp$Pattern$Record<Switch$ADT$A>" = pattern.record %25 @loc="7:19" @java.ref:"(int v)Switch$ADT$A";
                                            yield %26 @loc="7:19";
                                        }
                                        (%27 : java.type:"byte")java.type:"void" -> {
                                            var.store %18 %27;
                                            yield;
                                        };
                                    yield %24 @loc="7:19";
                                }
                                ()java.type:"Switch$ADT" -> {
                                    %28 : java.type:"int" = constant @loc="8:41" @0;
                                    %29 : java.type:"Switch$ADT$A" = new %28 @loc="8:31" @java.ref:"Switch$ADT$A::(int)";
                                    yield %29 @loc="7:19";
                                }
                                (%30 : java.type:"Switch$ADT")java.type:"boolean" -> {
                                    %31 : java.type:"boolean" = pattern.match %30 @loc="7:19"
                                        ()java.type:"jdk.incubator.code.dialect.java.JavaOp$Pattern$Record<Switch$ADT$A>" -> {
                                            %32 : java.type:"jdk.incubator.code.dialect.java.JavaOp$Pattern$Type<short>" = pattern.type @loc="7:19" @"v";
                                            %33 : java.type:"jdk.incubator.code.dialect.java.JavaOp$Pattern$Record<Switch$ADT$A>" = pattern.record %32 @loc="7:19" @java.ref:"(int v)Switch$ADT$A";
                                            yield %33 @loc="7:19";
                                        }
                                        (%34 : java.type:"short")java.type:"void" -> {
                                            var.store %21 %34;
                                            yield;
                                        };
                                    yield %31 @loc="7:19";
                                }
                                ()java.type:"Switch$ADT" -> {
                                    %35 : java.type:"short" = var.load %21 @loc="9:42";
                                    %36 : java.type:"int" = conv %35 @loc="9:42";
                                    %37 : java.type:"int" = constant @loc="9:46" @10;
                                    %38 : java.type:"int" = add %36 %37 @loc="9:42";
                                    %39 : java.type:"Switch$ADT$A" = new %38 @loc="9:32" @java.ref:"Switch$ADT$A::(int)";
                                    yield %39 @loc="7:19";
                                }
                                ()java.type:"boolean" -> {
                                    %40 : java.type:"boolean" = constant @loc="7:19" @true;
                                    yield %40 @loc="7:19";
                                }
                                ()java.type:"Switch$ADT" -> {
                                    %41 : java.type:"Switch$ADT$B" = new @loc="10:20" @java.ref:"Switch$ADT$B::()";
                                    yield %41 @loc="7:19";
                                };
                            %42 : Var<java.type:"Switch$ADT"> = var %22 @loc="7:5" @"result2";
                            %43 : java.type:"Switch$ADT" = var.load %42 @loc="13:13";
                            %44 : java.type:"java.lang.String" = invoke %43 @loc="13:13" @java.ref:"Switch$ADT::toString():java.lang.String";
                            java.switch.statement %44 @loc="13:5"
                                (%45 : java.type:"java.lang.String")java.type:"boolean" -> {
                                    %46 : java.type:"java.lang.String" = constant @loc="14:14" @"B[]";
                                    %47 : java.type:"boolean" = invoke %45 %46 @loc="13:5" @java.ref:"java.util.Objects::equals(java.lang.Object, java.lang.Object):boolean";
                                    yield %47 @loc="13:5";
                                }
                                ()java.type:"void" -> {
                                    %48 : java.type:"java.lang.String" = constant @loc="15:24" @"B";
                                    invoke %48 @loc="15:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.break @loc="16:13";
                                }
                                (%49 : java.type:"java.lang.String")java.type:"boolean" -> {
                                    %50 : java.type:"java.lang.String" = constant @loc="17:14" @"A[v=0]";
                                    %51 : java.type:"boolean" = invoke %49 %50 @loc="13:5" @java.ref:"java.util.Objects::equals(java.lang.Object, java.lang.Object):boolean";
                                    yield %51 @loc="13:5";
                                }
                                ()java.type:"void" -> {
                                    %52 : java.type:"java.lang.String" = constant @loc="18:24" @"Byte";
                                    invoke %52 @loc="18:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.break @loc="19:13";
                                }
                                ()java.type:"boolean" -> {
                                    %53 : java.type:"boolean" = constant @loc="13:5" @true;
                                    yield %53 @loc="13:5";
                                }
                                ()java.type:"void" -> {
                                    java.break @loc="21:13";
                                };
                            %54 : java.type:"java.lang.String" = constant @loc="24:13" @"A";
                            java.switch.statement %54 @loc="24:5"
                                (%55 : java.type:"java.lang.String")java.type:"boolean" -> {
                                    %56 : java.type:"java.lang.String" = constant @loc="25:14" @"A";
                                    %57 : java.type:"boolean" = invoke %55 %56 @loc="24:5" @java.ref:"java.util.Objects::equals(java.lang.Object, java.lang.Object):boolean";
                                    yield %57 @loc="24:5";
                                }
                                ()java.type:"void" -> {
                                    java.switch.fallthrough @loc="24:5";
                                }
                                (%58 : java.type:"java.lang.String")java.type:"boolean" -> {
                                    %59 : java.type:"java.lang.String" = constant @loc="26:14" @"B";
                                    %60 : java.type:"boolean" = invoke %58 %59 @loc="24:5" @java.ref:"java.util.Objects::equals(java.lang.Object, java.lang.Object):boolean";
                                    yield %60 @loc="24:5";
                                }
                                ()java.type:"void" -> {
                                    java.break @loc="27:13";
                                };
                            return @loc="1:1";
                        };""",
                op.toText());
        assertEquals(
                """
                        func @loc="1:1:Switch.java" @"main" (%0 : java.type:"Switch")java.type:"void" -> {
                            %1 : java.type:"boolean" = invoke %0 @loc="2:27" @java.ref:"Switch::condition():boolean";
                            %2 : Var<java.type:"Switch$ADT"> = var @loc="2:19";
                            java.if @loc="2:19"
                                ()java.type:"boolean" -> {
                                    %3 : java.type:"boolean" = constant @loc="3:14" @true;
                                    %4 : java.type:"boolean" = eq %1 %3 @loc="2:19";
                                    yield %4 @loc="2:19";
                                }
                                ()java.type:"void" -> {
                                    %5 : java.type:"int" = constant @loc="3:32" @10;
                                    %6 : java.type:"Switch$ADT$A" = new %5 @loc="3:22" @java.ref:"Switch$ADT$A::(int)";
                                    var.store %2 %6 @loc="2:19";
                                    yield @loc="2:19";
                                }
                                ()java.type:"boolean" -> {
                                    %7 : java.type:"boolean" = constant @loc="4:14" @false;
                                    %8 : java.type:"boolean" = eq %1 %7 @loc="2:19";
                                    yield %8 @loc="2:19";
                                }
                                ()java.type:"void" -> {
                                    %9 : java.type:"Switch$ADT$B" = new @loc="4:23" @java.ref:"Switch$ADT$B::()";
                                    var.store %2 %9 @loc="2:19";
                                    yield @loc="2:19";
                                }
                                ()java.type:"boolean" -> {
                                    %10 : java.type:"boolean" = constant @loc="2:19" @true;
                                    yield %10 @loc="2:19";
                                }
                                ()java.type:"void" -> {
                                    %11 : java.type:"java.lang.MatchException" = new @loc="2:19" @java.ref:"java.lang.MatchException::()";
                                    throw %11 @loc="2:19";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            %12 : java.type:"Switch$ADT" = var.load %2 @loc="2:19";
                            %13 : Var<java.type:"Switch$ADT"> = var %12 @loc="2:5" @"result1";
                            %14 : java.type:"Switch$ADT" = var.load %13 @loc="7:27";
                            %15 : java.type:"int" = constant @loc="7:19" @0;
                            %16 : java.type:"byte" = conv %15 @loc="7:19";
                            %17 : Var<java.type:"byte"> = var %16 @loc="7:19";
                            %18 : java.type:"int" = constant @loc="7:19" @0;
                            %19 : java.type:"short" = conv %18 @loc="7:19";
                            %20 : Var<java.type:"short"> = var %19 @loc="7:19" @"v";
                            %21 : Var<java.type:"Switch$ADT"> = var @loc="7:19";
                            java.if @loc="7:19"
                                ()java.type:"boolean" -> {
                                    %22 : java.type:"boolean" = pattern.match %14 @loc="7:19"
                                        ()java.type:"jdk.incubator.code.dialect.java.JavaOp$Pattern$Record<Switch$ADT$A>" -> {
                                            %23 : java.type:"jdk.incubator.code.dialect.java.JavaOp$Pattern$Type<byte>" = pattern.type @loc="7:19";
                                            %24 : java.type:"jdk.incubator.code.dialect.java.JavaOp$Pattern$Record<Switch$ADT$A>" = pattern.record %23 @loc="7:19" @java.ref:"(int v)Switch$ADT$A";
                                            yield %24 @loc="7:19";
                                        }
                                        (%25 : java.type:"byte")java.type:"void" -> {
                                            var.store %17 %25;
                                            yield;
                                        };
                                    yield %22 @loc="7:19";
                                }
                                ()java.type:"void" -> {
                                    %26 : java.type:"int" = constant @loc="8:41" @0;
                                    %27 : java.type:"Switch$ADT$A" = new %26 @loc="8:31" @java.ref:"Switch$ADT$A::(int)";
                                    var.store %21 %27 @loc="7:19";
                                    yield @loc="7:19";
                                }
                                ()java.type:"boolean" -> {
                                    %28 : java.type:"boolean" = pattern.match %14 @loc="7:19"
                                        ()java.type:"jdk.incubator.code.dialect.java.JavaOp$Pattern$Record<Switch$ADT$A>" -> {
                                            %29 : java.type:"jdk.incubator.code.dialect.java.JavaOp$Pattern$Type<short>" = pattern.type @loc="7:19" @"v";
                                            %30 : java.type:"jdk.incubator.code.dialect.java.JavaOp$Pattern$Record<Switch$ADT$A>" = pattern.record %29 @loc="7:19" @java.ref:"(int v)Switch$ADT$A";
                                            yield %30 @loc="7:19";
                                        }
                                        (%31 : java.type:"short")java.type:"void" -> {
                                            var.store %20 %31;
                                            yield;
                                        };
                                    yield %28 @loc="7:19";
                                }
                                ()java.type:"void" -> {
                                    %32 : java.type:"short" = var.load %20 @loc="9:42";
                                    %33 : java.type:"int" = conv %32 @loc="9:42";
                                    %34 : java.type:"int" = constant @loc="9:46" @10;
                                    %35 : java.type:"int" = add %33 %34 @loc="9:42";
                                    %36 : java.type:"Switch$ADT$A" = new %35 @loc="9:32" @java.ref:"Switch$ADT$A::(int)";
                                    var.store %21 %36 @loc="7:19";
                                    yield @loc="7:19";
                                }
                                ()java.type:"boolean" -> {
                                    %37 : java.type:"boolean" = constant @loc="7:19" @true;
                                    yield %37 @loc="7:19";
                                }
                                ()java.type:"void" -> {
                                    %38 : java.type:"Switch$ADT$B" = new @loc="10:20" @java.ref:"Switch$ADT$B::()";
                                    var.store %21 %38 @loc="7:19";
                                    yield @loc="7:19";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            %39 : java.type:"Switch$ADT" = var.load %21 @loc="7:19";
                            %40 : Var<java.type:"Switch$ADT"> = var %39 @loc="7:5" @"result2";
                            %41 : java.type:"Switch$ADT" = var.load %40 @loc="13:13";
                            %42 : java.type:"java.lang.String" = invoke %41 @loc="13:13" @java.ref:"Switch$ADT::toString():java.lang.String";
                            java.if @loc="13:5"
                                ()java.type:"boolean" -> {
                                    %43 : java.type:"java.lang.String" = constant @loc="14:14" @"B[]";
                                    %44 : java.type:"boolean" = invoke %42 %43 @loc="13:5" @java.ref:"java.util.Objects::equals(java.lang.Object, java.lang.Object):boolean";
                                    yield %44 @loc="13:5";
                                }
                                ()java.type:"void" -> {
                                    %45 : java.type:"java.lang.String" = constant @loc="15:24" @"B";
                                    invoke %45 @loc="15:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.break @loc="16:13";
                                }
                                ()java.type:"boolean" -> {
                                    %46 : java.type:"java.lang.String" = constant @loc="17:14" @"A[v=0]";
                                    %47 : java.type:"boolean" = invoke %42 %46 @loc="13:5" @java.ref:"java.util.Objects::equals(java.lang.Object, java.lang.Object):boolean";
                                    yield %47 @loc="13:5";
                                }
                                ()java.type:"void" -> {
                                    %48 : java.type:"java.lang.String" = constant @loc="18:24" @"Byte";
                                    invoke %48 @loc="18:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    java.break @loc="19:13";
                                }
                                ()java.type:"boolean" -> {
                                    %49 : java.type:"boolean" = constant @loc="13:5" @true;
                                    yield %49 @loc="13:5";
                                }
                                ()java.type:"void" -> {
                                    java.break @loc="21:13";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            %50 : java.type:"java.lang.String" = constant @loc="24:13" @"A";
                            java.switch.statement %50 @loc="24:5"
                                (%51 : java.type:"java.lang.String")java.type:"boolean" -> {
                                    %52 : java.type:"java.lang.String" = constant @loc="25:14" @"A";
                                    %53 : java.type:"boolean" = invoke %51 %52 @loc="24:5" @java.ref:"java.util.Objects::equals(java.lang.Object, java.lang.Object):boolean";
                                    yield %53 @loc="24:5";
                                }
                                ()java.type:"void" -> {
                                    java.switch.fallthrough @loc="24:5";
                                }
                                (%54 : java.type:"java.lang.String")java.type:"boolean" -> {
                                    %55 : java.type:"java.lang.String" = constant @loc="26:14" @"B";
                                    %56 : java.type:"boolean" = invoke %54 %55 @loc="24:5" @java.ref:"java.util.Objects::equals(java.lang.Object, java.lang.Object):boolean";
                                    yield %56 @loc="24:5";
                                }
                                ()java.type:"void" -> {
                                    java.break @loc="27:13";
                                };
                            return @loc="1:1";
                        };""",
                transformedOp.toText());
    }
}
