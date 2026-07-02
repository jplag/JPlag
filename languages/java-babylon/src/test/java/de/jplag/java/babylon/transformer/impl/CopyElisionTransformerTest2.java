package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.pipeline.TransformationPipeline;
import de.jplag.java.babylon.transformer.TransformerTest;

/**
 * Another unit test for {@link CopyElisionTransformer}.
 */
public class CopyElisionTransformerTest2 extends TransformerTest {
    @Override
    protected String getFileName() {
        return "ConditionalExpression.java";
    }

    @Override
    protected TransformationPipeline getPipeline() {
        return pipeline(step(new CopyElisionTransformer()));
    }

    @Override
    protected String getExpectedOriginal() {
        return """
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
                };""";
    }

    @Override
    protected String getExpectedTransformed() {
        return """
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
                    %5 : java.type:"int" = java.cexpression @loc="4:19"
                        ()java.type:"boolean" -> {
                            %6 : java.type:"int" = constant @loc="4:28" @5;
                            %7 : java.type:"boolean" = gt %1 %6 @loc="4:19";
                            yield %7 @loc="4:19";
                        }
                        ()java.type:"int" -> {
                            %8 : java.type:"int" = constant @loc="4:41" @5;
                            %9 : java.type:"int" = sub %1 %8 @loc="4:32";
                            yield %9 @loc="4:19";
                        }
                        ()java.type:"int" -> {
                            %10 : java.type:"int" = constant @loc="4:54" @5;
                            %11 : java.type:"int" = add %1 %10 @loc="4:45";
                            yield %11 @loc="4:19";
                        };
                    %12 : Var<java.type:"int"> = var @loc="6:5" @"result3";
                    java.if @loc="7:5"
                        ()java.type:"boolean" -> {
                            %13 : java.type:"int" = constant @loc="7:19" @5;
                            %14 : java.type:"boolean" = gt %5 %13 @loc="7:9";
                            yield %14 @loc="7:5";
                        }
                        ()java.type:"void" -> {
                            %15 : java.type:"int" = constant @loc="8:29" @5;
                            %16 : java.type:"int" = sub %5 %15 @loc="8:19";
                            var.store %12 %16 @loc="8:9";
                            yield @loc="7:5";
                        }
                        ()java.type:"void" -> {
                            %17 : java.type:"int" = constant @loc="10:29" @5;
                            %18 : java.type:"int" = add %5 %17 @loc="10:19";
                            var.store %12 %18 @loc="10:9";
                            yield @loc="7:5";
                        };
                    %19 : java.type:"int" = var.load %12 @loc="13:16";
                    %20 : java.type:"java.lang.Integer" = invoke %19 @loc="13:5" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    invoke %20 @loc="13:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    return @loc="1:1";
                };""";
    }
}
