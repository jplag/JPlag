package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.pipeline.TransformationPipeline;
import de.jplag.java.babylon.transformer.TransformerTest;

/**
 * Unit test for {@link IfFuseTransformer}.
 */
public class IfFuseTransformerTest extends TransformerTest {
    @Override
    protected String getFileName() {
        return "If.java";
    }

    @Override
    protected TransformationPipeline getPipeline() {
        return pipeline(step(new IfFuseTransformer()));
    }

    @Override
    protected String getExpectedOriginal() {
        return """
                func @loc="1:1:If.java" @"main" (%0 : java.type:"If")java.type:"void" -> {
                    %1 : java.type:"java.lang.Class" = constant @loc="2:13" @java.type:"java.lang.Long";
                    %2 : java.type:"int" = invoke %1 @loc="2:13" @java.ref:"java.lang.Object::hashCode():int";
                    %3 : java.type:"int" = constant @loc="2:37" @10;
                    %4 : java.type:"int" = mod %2 %3 @loc="2:13";
                    %5 : Var<java.type:"int"> = var %4 @loc="2:5" @"a";
                    %6 : java.type:"java.lang.Class" = constant @loc="3:13" @java.type:"java.lang.Double";
                    %7 : java.type:"int" = invoke %6 @loc="3:13" @java.ref:"java.lang.Object::hashCode():int";
                    %8 : java.type:"int" = constant @loc="3:39" @10;
                    %9 : java.type:"int" = mod %7 %8 @loc="3:13";
                    %10 : Var<java.type:"int"> = var %9 @loc="3:5" @"b";
                    %11 : java.type:"java.lang.Class" = constant @loc="4:13" @java.type:"java.lang.Integer";
                    %12 : java.type:"int" = invoke %11 @loc="4:13" @java.ref:"java.lang.Object::hashCode():int";
                    %13 : java.type:"int" = constant @loc="4:40" @10;
                    %14 : java.type:"int" = mod %12 %13 @loc="4:13";
                    %15 : Var<java.type:"int"> = var %14 @loc="4:5" @"c";
                    java.if @loc="6:5"
                        ()java.type:"boolean" -> {
                            %16 : java.type:"int" = var.load %5 @loc="6:9";
                            %17 : java.type:"int" = var.load %10 @loc="6:13";
                            %18 : java.type:"boolean" = gt %16 %17 @loc="6:9";
                            yield %18 @loc="6:5";
                        }
                        ()java.type:"void" -> {
                            java.if @loc="7:9"
                                ()java.type:"boolean" -> {
                                    %19 : java.type:"int" = var.load %10 @loc="7:13";
                                    %20 : java.type:"int" = var.load %15 @loc="7:17";
                                    %21 : java.type:"boolean" = gt %19 %20 @loc="7:13";
                                    yield %21 @loc="7:9";
                                }
                                ()java.type:"void" -> {
                                    %22 : java.type:"java.lang.String" = constant @loc="8:24" @"A > C";
                                    invoke %22 @loc="8:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    yield @loc="7:9";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            yield @loc="6:5";
                        }
                        ()java.type:"void" -> {
                            yield;
                        };
                    java.if @loc="12:5"
                        ()java.type:"boolean" -> {
                            %23 : java.type:"int" = var.load %5 @loc="12:9";
                            %24 : java.type:"int" = var.load %10 @loc="12:13";
                            %25 : java.type:"boolean" = gt %23 %24 @loc="12:9";
                            yield %25 @loc="12:5";
                        }
                        ()java.type:"void" -> {
                            yield @loc="12:5";
                        }
                        ()java.type:"void" -> {
                            java.if @loc="15:9"
                                ()java.type:"boolean" -> {
                                    %26 : java.type:"int" = var.load %10 @loc="15:13";
                                    %27 : java.type:"int" = var.load %15 @loc="15:17";
                                    %28 : java.type:"boolean" = lt %26 %27 @loc="15:13";
                                    yield %28 @loc="15:9";
                                }
                                ()java.type:"void" -> {
                                    %29 : java.type:"java.lang.String" = constant @loc="16:24" @"A < C";
                                    invoke %29 @loc="16:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    yield @loc="15:9";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            yield @loc="12:5";
                        };
                    java.if @loc="21:5"
                        ()java.type:"boolean" -> {
                            %30 : java.type:"int" = var.load %5 @loc="21:9";
                            %31 : java.type:"int" = var.load %10 @loc="21:13";
                            %32 : java.type:"boolean" = gt %30 %31 @loc="21:9";
                            yield %32 @loc="21:5";
                        }
                        ()java.type:"void" -> {
                            java.if @loc="22:9"
                                ()java.type:"boolean" -> {
                                    %33 : java.type:"int" = var.load %10 @loc="22:13";
                                    %34 : java.type:"int" = var.load %15 @loc="22:17";
                                    %35 : java.type:"boolean" = gt %33 %34 @loc="22:13";
                                    yield %35 @loc="22:9";
                                }
                                ()java.type:"void" -> {
                                    yield @loc="22:9";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            yield @loc="21:5";
                        }
                        ()java.type:"void" -> {
                            java.if @loc="25:9"
                                ()java.type:"boolean" -> {
                                    %36 : java.type:"int" = var.load %10 @loc="25:13";
                                    %37 : java.type:"int" = var.load %15 @loc="25:17";
                                    %38 : java.type:"boolean" = lt %36 %37 @loc="25:13";
                                    yield %38 @loc="25:9";
                                }
                                ()java.type:"void" -> {
                                    %39 : java.type:"java.lang.String" = constant @loc="26:24" @"A < C";
                                    invoke %39 @loc="26:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    yield @loc="25:9";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            yield @loc="21:5";
                        };
                    java.if @loc="30:5"
                        ()java.type:"boolean" -> {
                            %40 : java.type:"int" = var.load %5 @loc="30:9";
                            %41 : java.type:"int" = var.load %10 @loc="30:13";
                            %42 : java.type:"boolean" = gt %40 %41 @loc="30:9";
                            yield %42 @loc="30:5";
                        }
                        ()java.type:"void" -> {
                            java.if @loc="31:9"
                                ()java.type:"boolean" -> {
                                    %43 : java.type:"int" = var.load %10 @loc="31:13";
                                    %44 : java.type:"int" = var.load %15 @loc="31:17";
                                    %45 : java.type:"boolean" = gt %43 %44 @loc="31:13";
                                    yield %45 @loc="31:9";
                                }
                                ()java.type:"void" -> {
                                    %46 : java.type:"java.lang.String" = constant @loc="32:24" @"A > C";
                                    invoke %46 @loc="32:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    yield @loc="31:9";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            yield @loc="30:5";
                        }
                        ()java.type:"void" -> {
                            java.if @loc="35:9"
                                ()java.type:"boolean" -> {
                                    %47 : java.type:"int" = var.load %10 @loc="35:13";
                                    %48 : java.type:"int" = var.load %15 @loc="35:17";
                                    %49 : java.type:"boolean" = lt %47 %48 @loc="35:13";
                                    yield %49 @loc="35:9";
                                }
                                ()java.type:"void" -> {
                                    %50 : java.type:"java.lang.String" = constant @loc="36:24" @"A < C";
                                    invoke %50 @loc="36:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    yield @loc="35:9";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            yield @loc="30:5";
                        };
                    return @loc="1:1";
                };""";
    }

    @Override
    protected String getExpectedTransformed() {
        return """
                func @loc="1:1:If.java" @"main" (%0 : java.type:"If")java.type:"void" -> {
                    %1 : java.type:"java.lang.Class" = constant @loc="2:13" @java.type:"java.lang.Long";
                    %2 : java.type:"int" = invoke %1 @loc="2:13" @java.ref:"java.lang.Object::hashCode():int";
                    %3 : java.type:"int" = constant @loc="2:37" @10;
                    %4 : java.type:"int" = mod %2 %3 @loc="2:13";
                    %5 : Var<java.type:"int"> = var %4 @loc="2:5" @"a";
                    %6 : java.type:"java.lang.Class" = constant @loc="3:13" @java.type:"java.lang.Double";
                    %7 : java.type:"int" = invoke %6 @loc="3:13" @java.ref:"java.lang.Object::hashCode():int";
                    %8 : java.type:"int" = constant @loc="3:39" @10;
                    %9 : java.type:"int" = mod %7 %8 @loc="3:13";
                    %10 : Var<java.type:"int"> = var %9 @loc="3:5" @"b";
                    %11 : java.type:"java.lang.Class" = constant @loc="4:13" @java.type:"java.lang.Integer";
                    %12 : java.type:"int" = invoke %11 @loc="4:13" @java.ref:"java.lang.Object::hashCode():int";
                    %13 : java.type:"int" = constant @loc="4:40" @10;
                    %14 : java.type:"int" = mod %12 %13 @loc="4:13";
                    %15 : Var<java.type:"int"> = var %14 @loc="4:5" @"c";
                    java.if @loc="6:5"
                        ()java.type:"boolean" -> {
                            %16 : java.type:"boolean" = java.cand @loc="7:9"
                                ()java.type:"boolean" -> {
                                    %17 : java.type:"int" = var.load %5 @loc="6:9";
                                    %18 : java.type:"int" = var.load %10 @loc="6:13";
                                    %19 : java.type:"boolean" = gt %17 %18 @loc="6:9";
                                    yield %19 @loc="6:5";
                                }
                                ()java.type:"boolean" -> {
                                    %20 : java.type:"int" = var.load %10 @loc="7:13";
                                    %21 : java.type:"int" = var.load %15 @loc="7:17";
                                    %22 : java.type:"boolean" = gt %20 %21 @loc="7:13";
                                    yield %22 @loc="7:9";
                                };
                            yield %16 @loc="7:9";
                        }
                        ()java.type:"void" -> {
                            %23 : java.type:"java.lang.String" = constant @loc="8:24" @"A > C";
                            invoke %23 @loc="8:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                            yield @loc="7:9";
                        }
                        ()java.type:"void" -> {
                            yield;
                        };
                    java.if @loc="12:5"
                        ()java.type:"boolean" -> {
                            %24 : java.type:"boolean" = java.cand @loc="15:9"
                                ()java.type:"boolean" -> {
                                    %25 : java.type:"int" = var.load %5 @loc="12:9";
                                    %26 : java.type:"int" = var.load %10 @loc="12:13";
                                    %27 : java.type:"boolean" = gt %25 %26 @loc="12:9";
                                    %28 : java.type:"boolean" = not %27 @loc="12:5";
                                    yield %28 @loc="12:5";
                                }
                                ()java.type:"boolean" -> {
                                    %29 : java.type:"int" = var.load %10 @loc="15:13";
                                    %30 : java.type:"int" = var.load %15 @loc="15:17";
                                    %31 : java.type:"boolean" = lt %29 %30 @loc="15:13";
                                    yield %31 @loc="15:9";
                                };
                            yield %24 @loc="15:9";
                        }
                        ()java.type:"void" -> {
                            %32 : java.type:"java.lang.String" = constant @loc="16:24" @"A < C";
                            invoke %32 @loc="16:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                            yield @loc="15:9";
                        }
                        ()java.type:"void" -> {
                            yield;
                        };
                    java.if @loc="21:5"
                        ()java.type:"boolean" -> {
                            %33 : java.type:"int" = var.load %5 @loc="21:9";
                            %34 : java.type:"int" = var.load %10 @loc="21:13";
                            %35 : java.type:"boolean" = gt %33 %34 @loc="21:9";
                            yield %35 @loc="21:5";
                        }
                        ()java.type:"void" -> {
                            %36 : java.type:"int" = var.load %10 @loc="22:13";
                            %37 : java.type:"int" = var.load %15 @loc="22:17";
                            %38 : java.type:"boolean" = gt %36 %37 @loc="22:13";
                            yield @loc="21:5";
                        }
                        ()java.type:"void" -> {
                            java.if @loc="25:9"
                                ()java.type:"boolean" -> {
                                    %39 : java.type:"int" = var.load %10 @loc="25:13";
                                    %40 : java.type:"int" = var.load %15 @loc="25:17";
                                    %41 : java.type:"boolean" = lt %39 %40 @loc="25:13";
                                    yield %41 @loc="25:9";
                                }
                                ()java.type:"void" -> {
                                    %42 : java.type:"java.lang.String" = constant @loc="26:24" @"A < C";
                                    invoke %42 @loc="26:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    yield @loc="25:9";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            yield @loc="21:5";
                        };
                    java.if @loc="30:5"
                        ()java.type:"boolean" -> {
                            %43 : java.type:"int" = var.load %5 @loc="30:9";
                            %44 : java.type:"int" = var.load %10 @loc="30:13";
                            %45 : java.type:"boolean" = gt %43 %44 @loc="30:9";
                            yield %45 @loc="30:5";
                        }
                        ()java.type:"void" -> {
                            java.if @loc="31:9"
                                ()java.type:"boolean" -> {
                                    %46 : java.type:"int" = var.load %10 @loc="31:13";
                                    %47 : java.type:"int" = var.load %15 @loc="31:17";
                                    %48 : java.type:"boolean" = gt %46 %47 @loc="31:13";
                                    yield %48 @loc="31:9";
                                }
                                ()java.type:"void" -> {
                                    %49 : java.type:"java.lang.String" = constant @loc="32:24" @"A > C";
                                    invoke %49 @loc="32:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    yield @loc="31:9";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            yield @loc="30:5";
                        }
                        ()java.type:"void" -> {
                            java.if @loc="35:9"
                                ()java.type:"boolean" -> {
                                    %50 : java.type:"int" = var.load %10 @loc="35:13";
                                    %51 : java.type:"int" = var.load %15 @loc="35:17";
                                    %52 : java.type:"boolean" = lt %50 %51 @loc="35:13";
                                    yield %52 @loc="35:9";
                                }
                                ()java.type:"void" -> {
                                    %53 : java.type:"java.lang.String" = constant @loc="36:24" @"A < C";
                                    invoke %53 @loc="36:13" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                                    yield @loc="35:9";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            yield @loc="30:5";
                        };
                    return @loc="1:1";
                };""";
    }
}
