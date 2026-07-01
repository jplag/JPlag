package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.pipeline.TransformationPipeline;
import de.jplag.java.babylon.transformer.TransformerTest;

/**
 * Unit test for {@link InliningStep}.
 */
public class InliningStepTest extends TransformerTest {
    @Override
    protected String getFileName() {
        return "Inline.java";
    }

    @Override
    protected TransformationPipeline getPipeline() {
        return pipeline(new InliningStep());
    }

    @Override
    protected String getExpectedOriginal() {
        return """
                func @loc="1:1:Inline.java" @"main" (%0 : java.type:"Inline")java.type:"void" -> {
                    %1 : java.type:"java.lang.String" = constant @loc="2:25" @"Hello";
                    %2 : java.type:"java.lang.String" = invoke %0 %1 @loc="2:16" @java.ref:"Inline::toInline(java.lang.String):java.lang.String";
                    invoke %2 @loc="2:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    return @loc="1:1";
                };""";
    }

    @Override
    protected String getExpectedTransformed() {
        return """
                func @loc="1:1:Inline.java" @"main" (%0 : java.type:"Inline")java.type:"void" -> {
                    %1 : java.type:"java.lang.String" = constant @loc="2:25" @"Hello";
                    %2 : Var<java.type:"void"> = var;
                    %3 : Var<java.type:"java.lang.String"> = var %1 @"parameter";
                    %4 : java.type:"java.lang.String" = constant @"length: ";
                    %5 : java.type:"java.lang.String" = var.load %3;
                    %6 : java.type:"int" = invoke %5 @java.ref:"java.lang.String::length():int";
                    %7 : java.type:"java.lang.String" = concat %4 %6;
                    var.store %2 %7;
                    %8 : java.type:"void" = var.load %2;
                    invoke %8 @loc="2:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    return @loc="1:1";
                };""";
    }
}
