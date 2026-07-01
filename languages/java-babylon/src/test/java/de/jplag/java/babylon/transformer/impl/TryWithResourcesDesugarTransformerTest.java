package de.jplag.java.babylon.transformer.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Unit test for {@link TryWithResourcesDesugarTransformer}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TryWithResourcesDesugarTransformerTest extends AbstractTransformerTest {
    /**
     * Unit test for {@link TryWithResourcesDesugarTransformer}.
     */
    @Test
    public void testTransformer() {
        ParseResult parseResult = assertDoesNotThrow(() -> parseFile("TryWithResources.java"));
        CoreOp.FuncOp op = parseResult.extractCodeModel();
        CoreOp.FuncOp transformedOp = parseResult.extractCodeModel(pipeline(step(new TryWithResourcesDesugarTransformer())));

        assertEquals(
                """
                        func @loc="1:1:TryWithResources.java" @"main" (%0 : java.type:"TryWithResources")java.type:"void" -> {
                            %1 : java.type:"java.lang.String" = constant @loc="2:19" @"DoesNotExist.txt";
                            %2 : Var<java.type:"java.lang.String"> = var %1 @loc="2:5" @"path";
                            %3 : java.type:"java.util.Scanner" = constant @loc="3:21" @null;
                            %4 : Var<java.type:"java.util.Scanner"> = var %3 @loc="3:5" @"other";
                            java.try @loc="4:5"
                                ()Var<java.type:"java.util.Scanner"> -> {
                                    %5 : java.type:"java.lang.String" = var.load %2 @loc="4:57";
                                    %6 : java.type:"java.io.File" = new %5 @loc="4:48" @java.ref:"java.io.File::(java.lang.String)";
                                    %7 : java.type:"java.util.Scanner" = new %6 @loc="4:36" @java.ref:"java.util.Scanner::(java.io.File)";
                                    var.store %4 %7 @loc="4:28";
                                    %8 : Var<java.type:"java.util.Scanner"> = var %7 @loc="4:10" @"scanner";
                                    yield %8 @loc="4:5";
                                }
                                (%9 : Var<java.type:"java.util.Scanner">)java.type:"void" -> {
                                    java.while @loc="5:9"
                                        ()java.type:"boolean" -> {
                                            %10 : java.type:"java.util.Scanner" = var.load %9 @loc="5:16";
                                            %11 : java.type:"boolean" = invoke %10 @loc="5:16" @java.ref:"java.util.Scanner::hasNext():boolean";
                                            yield %11 @loc="5:9";
                                        }
                                        ()java.type:"void" -> {
                                            %12 : java.type:"java.io.PrintStream" = field.load @loc="6:13" @java.ref:"java.lang.System::out:java.io.PrintStream";
                                            %13 : java.type:"java.util.Scanner" = var.load %9 @loc="6:32";
                                            %14 : java.type:"java.lang.String" = invoke %13 @loc="6:32" @java.ref:"java.util.Scanner::nextLine():java.lang.String";
                                            invoke %12 %14 @loc="6:13" @java.ref:"java.io.PrintStream::println(java.lang.String):void";
                                            java.continue @loc="5:9";
                                        };
                                    yield @loc="4:5";
                                }
                                (%15 : java.type:"java.io.FileNotFoundException")java.type:"void" -> {
                                    %16 : Var<java.type:"java.io.FileNotFoundException"> = var %15 @loc="4:5" @"exception";
                                    %17 : java.type:"java.io.FileNotFoundException" = var.load %16 @loc="9:9";
                                    invoke %17 @loc="9:9" @java.ref:"java.io.FileNotFoundException::printStackTrace():void";
                                    yield @loc="4:5";
                                }
                                ()java.type:"void" -> {
                                    java.if @loc="11:9"
                                        ()java.type:"boolean" -> {
                                            %18 : java.type:"java.util.Scanner" = var.load %4 @loc="11:13";
                                            %19 : java.type:"java.lang.Object" = constant @loc="11:22" @null;
                                            %20 : java.type:"boolean" = neq %18 %19 @loc="11:13";
                                            yield %20 @loc="11:9";
                                        }
                                        ()java.type:"void" -> {
                                            %21 : java.type:"java.util.Scanner" = var.load %4 @loc="12:13";
                                            invoke %21 @loc="12:13" @java.ref:"java.util.Scanner::close():void";
                                            yield @loc="11:9";
                                        }
                                        ()java.type:"void" -> {
                                            yield;
                                        };
                                    yield @loc="4:5";
                                };
                            return @loc="1:1";
                        };""",
                op.toText());

        assertEquals(
                """
                        func @loc="1:1:TryWithResources.java" @"main" (%0 : java.type:"TryWithResources")java.type:"void" -> {
                            %1 : java.type:"java.lang.String" = constant @loc="2:19" @"DoesNotExist.txt";
                            %2 : Var<java.type:"java.lang.String"> = var %1 @loc="2:5" @"path";
                            %3 : java.type:"java.util.Scanner" = constant @loc="3:21" @null;
                            %4 : Var<java.type:"java.util.Scanner"> = var %3 @loc="3:5" @"other";
                            %5 : Var<java.type:"java.util.Scanner"> = var @loc="4:36" @"scanner";
                            java.try @loc="4:5"
                                ()java.type:"void" -> {
                                    %6 : java.type:"java.lang.String" = var.load %2 @loc="4:57";
                                    %7 : java.type:"java.io.File" = new %6 @loc="4:48" @java.ref:"java.io.File::(java.lang.String)";
                                    %8 : java.type:"java.util.Scanner" = new %7 @loc="4:36" @java.ref:"java.util.Scanner::(java.io.File)";
                                    var.store %4 %8 @loc="4:28";
                                    var.store %5 %8 @loc="4:10";
                                    java.while @loc="5:9"
                                        ()java.type:"boolean" -> {
                                            %9 : java.type:"java.util.Scanner" = var.load %5 @loc="5:16";
                                            %10 : java.type:"boolean" = invoke %9 @loc="5:16" @java.ref:"java.util.Scanner::hasNext():boolean";
                                            yield %10 @loc="5:9";
                                        }
                                        ()java.type:"void" -> {
                                            %11 : java.type:"java.io.PrintStream" = field.load @loc="6:13" @java.ref:"java.lang.System::out:java.io.PrintStream";
                                            %12 : java.type:"java.util.Scanner" = var.load %5 @loc="6:32";
                                            %13 : java.type:"java.lang.String" = invoke %12 @loc="6:32" @java.ref:"java.util.Scanner::nextLine():java.lang.String";
                                            invoke %11 %13 @loc="6:13" @java.ref:"java.io.PrintStream::println(java.lang.String):void";
                                            java.continue @loc="5:9";
                                        };
                                    yield @loc="4:5";
                                }
                                (%14 : java.type:"java.io.FileNotFoundException")java.type:"void" -> {
                                    constant @loc="9:9" @null;
                                    %15 : Var<java.type:"java.io.FileNotFoundException"> = var %14 @loc="4:5" @"exception";
                                    %16 : java.type:"java.io.FileNotFoundException" = var.load %15 @loc="9:9";
                                    invoke %16 @loc="9:9" @java.ref:"java.io.FileNotFoundException::printStackTrace():void";
                                    yield @loc="4:5";
                                }
                                ()java.type:"void" -> {
                                    constant @loc="11:9" @null;
                                    %17 : java.type:"java.util.Scanner" = var.load %5;
                                    java.if
                                        ()java.type:"boolean" -> {
                                            %18 : java.type:"java.lang.Object" = constant @loc="4:36" @null;
                                            %19 : java.type:"boolean" = neq %17 %18 @loc="4:36";
                                            yield %19 @loc="4:36";
                                        }
                                        ()java.type:"void" -> {
                                            invoke %17 @loc="4:36" @java.ref:"java.lang.AutoCloseable::close():void";
                                            yield @loc="4:36";
                                        }
                                        ()java.type:"void" -> {
                                            yield;
                                        };
                                    java.if @loc="11:9"
                                        ()java.type:"boolean" -> {
                                            %20 : java.type:"java.util.Scanner" = var.load %4 @loc="11:13";
                                            %21 : java.type:"java.lang.Object" = constant @loc="11:22" @null;
                                            %22 : java.type:"boolean" = neq %20 %21 @loc="11:13";
                                            yield %22 @loc="11:9";
                                        }
                                        ()java.type:"void" -> {
                                            %23 : java.type:"java.util.Scanner" = var.load %4 @loc="12:13";
                                            invoke %23 @loc="12:13" @java.ref:"java.util.Scanner::close():void";
                                            yield @loc="11:9";
                                        }
                                        ()java.type:"void" -> {
                                            yield;
                                        };
                                    yield @loc="4:5";
                                };
                            return @loc="1:1";
                        };""",
                transformedOp.toText());
    }
}
