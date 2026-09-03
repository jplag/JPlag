package de.jplag.java.babylon.transformer.impl;

import org.junit.jupiter.api.TestInstance;

import de.jplag.java.babylon.pipeline.TransformationPipeline;
import de.jplag.java.babylon.transformer.TransformerTest;

/**
 * Unit test for {@link AssertRemoveTransformer}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AssertRemoveTransformerTest extends TransformerTest {
    @Override
    protected String getFileName() {
        return "Asserts.java";
    }

    @Override
    protected TransformationPipeline getPipeline() {
        return pipeline(step(new AssertRemoveTransformer()));
    }

    @Override
    protected String getExpectedOriginal() {
        return """
                func @loc="1:1:Asserts.java" @"main" (%0 : java.type:"Asserts")java.type:"void" -> {
                    %1 : java.type:"java.lang.String" = constant @loc="2:19" @"Hello";
                    %2 : Var<java.type:"java.lang.String"> = var %1 @loc="2:5" @"var1";
                    %3 : java.type:"boolean" = constant @loc="3:20" @true;
                    %4 : Var<java.type:"boolean"> = var %3 @loc="3:5" @"var2";
                    assert @loc="4:5" ()java.type:"boolean" -> {
                        %5 : java.type:"boolean" = var.load %4 @loc="4:12";
                        yield %5 @loc="4:5";
                    };
                    assert @loc="5:5"
                        ()java.type:"boolean" -> {
                            %6 : java.type:"java.lang.String" = var.load %2 @loc="5:12";
                            %7 : java.type:"int" = invoke %6 @loc="5:12" @java.ref:"java.lang.String::length():int";
                            %8 : java.type:"int" = constant @loc="5:28" @3;
                            %9 : java.type:"boolean" = lt %7 %8 @loc="5:12";
                            yield %9 @loc="5:5";
                        }
                        ()java.type:"java.lang.String" -> {
                            %10 : java.type:"java.lang.String" = constant @loc="5:32" @"This one should fail";
                            yield %10 @loc="5:5";
                        };
                    return @loc="1:1";
                };""";
    }

    @Override
    protected String getExpectedTransformed() {
        return """
                func @loc="1:1:Asserts.java" @"main" (%0 : java.type:"Asserts")java.type:"void" -> {
                    %1 : java.type:"java.lang.String" = constant @loc="2:19" @"Hello";
                    %2 : Var<java.type:"java.lang.String"> = var %1 @loc="2:5" @"var1";
                    %3 : java.type:"boolean" = constant @loc="3:20" @true;
                    %4 : Var<java.type:"boolean"> = var %3 @loc="3:5" @"var2";
                    return @loc="1:1";
                };""";
    }
}
