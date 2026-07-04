package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.pipeline.TransformationPipeline;
import de.jplag.java.babylon.transformer.TransformerTest;

/**
 * Unit test for {@link StreamFuseTransformer}.
 */
public class StreamFuseTransformerTest extends TransformerTest {
    @Override
    protected String getFileName() {
        return "Stream.java";
    }

    @Override
    protected TransformationPipeline getPipeline() {
        return pipeline(step(new StreamFuseTransformer()), step(new DeadCodeEliminationTransformer()));
    }

    @Override
    protected String getExpectedOriginal() {
        return """
                func @loc="1:1:Stream.java" @"main" (%0 : java.type:"Stream")java.type:"void" -> {
                    %1 : java.type:"int" = constant @loc="2:37" @1;
                    %2 : java.type:"java.lang.Integer" = invoke %1 @loc="2:29" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    %3 : java.type:"int" = constant @loc="2:40" @2;
                    %4 : java.type:"java.lang.Integer" = invoke %3 @loc="2:29" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    %5 : java.type:"int" = constant @loc="2:43" @3;
                    %6 : java.type:"java.lang.Integer" = invoke %5 @loc="2:29" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    %7 : java.type:"int" = constant @loc="2:46" @4;
                    %8 : java.type:"java.lang.Integer" = invoke %7 @loc="2:29" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    %9 : java.type:"java.util.List<java.lang.Integer>" = invoke %2 %4 %6 %8 @loc="2:29" @java.ref:"java.util.List::of(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object):java.util.List";
                    %10 : Var<java.type:"java.util.List<java.lang.Integer>"> = var %9 @loc="2:5" @"source1";
                    %11 : java.type:"int" = constant @loc="3:21" @4;
                    %12 : java.type:"int[]" = new %11 @loc="3:21" @java.ref:"int[]::(int)";
                    %13 : java.type:"int" = constant @loc="3:22" @1;
                    %14 : java.type:"int" = constant @loc="3:21" @0;
                    array.store %12 %14 %13 @loc="3:21";
                    %15 : java.type:"int" = constant @loc="3:25" @2;
                    %16 : java.type:"int" = constant @loc="3:21" @1;
                    array.store %12 %16 %15 @loc="3:21";
                    %17 : java.type:"int" = constant @loc="3:28" @3;
                    %18 : java.type:"int" = constant @loc="3:21" @2;
                    array.store %12 %18 %17 @loc="3:21";
                    %19 : java.type:"int" = constant @loc="3:31" @4;
                    %20 : java.type:"int" = constant @loc="3:21" @3;
                    array.store %12 %20 %19 @loc="3:21";
                    %21 : Var<java.type:"int[]"> = var %12 @loc="3:5" @"source2";
                    %22 : java.type:"java.lang.String" = constant @loc="4:22" @"1234";
                    %23 : Var<java.type:"java.lang.String"> = var %22 @loc="4:5" @"source3";
                    %24 : java.type:"java.util.List<java.lang.Integer>" = var.load %10 @loc="6:16";
                    %25 : java.type:"java.util.stream.Stream<java.lang.Integer>" = invoke %24 @loc="6:16" @java.ref:"java.util.List::stream():java.util.stream.Stream";
                    %26 : java.type:"java.util.function.Predicate<java.lang.Integer>" = lambda @loc="6:40" @lambda.isReflectable=true (%27 : java.type:"java.lang.Integer")java.type:"boolean" -> {
                        %28 : Var<java.type:"java.lang.Integer"> = var %27 @loc="6:40" @"i";
                        %29 : java.type:"java.lang.Integer" = var.load %28 @loc="6:45";
                        %30 : java.type:"int" = invoke %29 @loc="6:45" @java.ref:"java.lang.Integer::intValue():int";
                        %31 : java.type:"int" = constant @loc="6:49" @2;
                        %32 : java.type:"int" = mod %30 %31 @loc="6:45";
                        %33 : java.type:"int" = constant @loc="6:54" @0;
                        %34 : java.type:"boolean" = eq %32 %33 @loc="6:45";
                        return %34 @loc="6:40";
                    };
                    %35 : java.type:"java.util.stream.Stream<java.lang.Integer>" = invoke %25 %26 @loc="6:16" @java.ref:"java.util.stream.Stream::filter(java.util.function.Predicate):java.util.stream.Stream";
                    %36 : java.type:"java.util.function.Function<java.lang.Integer, java.lang.Integer>" = lambda @loc="6:61" @lambda.isReflectable=true (%37 : java.type:"java.lang.Integer")java.type:"java.lang.Integer" -> {
                        %38 : Var<java.type:"java.lang.Integer"> = var %37 @loc="6:61" @"i";
                        %39 : java.type:"java.lang.Integer" = var.load %38 @loc="6:66";
                        return %39 @loc="6:61";
                    };
                    %40 : java.type:"java.util.stream.Stream<java.lang.Integer>" = invoke %35 %36 @loc="6:16" @java.ref:"java.util.stream.Stream::map(java.util.function.Function):java.util.stream.Stream";
                    %41 : java.type:"java.util.List<java.lang.Integer>" = invoke %40 @loc="6:16" @java.ref:"java.util.stream.Stream::toList():java.util.List";
                    invoke %41 @loc="6:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    %42 : java.type:"int[]" = var.load %21 @loc="7:46";
                    %43 : java.type:"java.util.stream.IntStream" = invoke %42 @loc="7:32" @java.ref:"java.util.Arrays::stream(int[]):java.util.stream.IntStream";
                    %44 : java.type:"java.util.function.IntPredicate" = lambda @loc="7:62" @lambda.isReflectable=true (%45 : java.type:"int")java.type:"boolean" -> {
                        %46 : Var<java.type:"int"> = var %45 @loc="7:62" @"i";
                        %47 : java.type:"int" = var.load %46 @loc="7:67";
                        %48 : java.type:"int" = constant @loc="7:71" @2;
                        %49 : java.type:"int" = mod %47 %48 @loc="7:67";
                        %50 : java.type:"int" = constant @loc="7:76" @0;
                        %51 : java.type:"boolean" = eq %49 %50 @loc="7:67";
                        return %51 @loc="7:62";
                    };
                    %52 : java.type:"java.util.stream.IntStream" = invoke %43 %44 @loc="7:32" @java.ref:"java.util.stream.IntStream::filter(java.util.function.IntPredicate):java.util.stream.IntStream";
                    %53 : java.type:"int[]" = invoke %52 @loc="7:32" @java.ref:"java.util.stream.IntStream::toArray():int[]";
                    %54 : java.type:"java.lang.String" = invoke %53 @loc="7:16" @java.ref:"java.util.Arrays::toString(int[]):java.lang.String";
                    invoke %54 @loc="7:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    %55 : java.type:"java.lang.String" = var.load %23 @loc="8:16";
                    %56 : java.type:"java.util.stream.IntStream" = invoke %55 @loc="8:16" @java.ref:"java.lang.String::chars():java.util.stream.IntStream";
                    %57 : java.type:"java.util.function.IntPredicate" = lambda @loc="8:39" @lambda.isReflectable=true (%58 : java.type:"int")java.type:"boolean" -> {
                        %59 : Var<java.type:"int"> = var %58 @loc="8:39" @"i";
                        %60 : java.type:"int" = var.load %59 @loc="8:44";
                        %61 : java.type:"int" = constant @loc="8:48" @2;
                        %62 : java.type:"int" = mod %60 %61 @loc="8:44";
                        %63 : java.type:"int" = constant @loc="8:53" @0;
                        %64 : java.type:"boolean" = eq %62 %63 @loc="8:44";
                        return %64 @loc="8:39";
                    };
                    %65 : java.type:"java.util.stream.IntStream" = invoke %56 %57 @loc="8:16" @java.ref:"java.util.stream.IntStream::filter(java.util.function.IntPredicate):java.util.stream.IntStream";
                    %66 : java.type:"java.util.function.IntFunction<java.lang.String>" = lambda @loc="8:65" @lambda.isReflectable=true (%67 : java.type:"int")java.type:"java.lang.String" -> {
                        %68 : Var<java.type:"int"> = var %67 @loc="8:65" @"x$0";
                        %69 : java.type:"int" = var.load %68 @loc="8:65";
                        %70 : java.type:"java.lang.String" = invoke %69 @loc="8:65" @java.ref:"java.lang.Integer::toString(int):java.lang.String";
                        return %70 @loc="8:65";
                    };
                    %71 : java.type:"java.util.stream.Stream<java.lang.String>" = invoke %65 %66 @loc="8:16" @java.ref:"java.util.stream.IntStream::mapToObj(java.util.function.IntFunction):java.util.stream.Stream";
                    %72 : java.type:"java.util.function.ToLongFunction<java.lang.String>" = lambda @loc="8:94" @lambda.isReflectable=true (%73 : java.type:"java.lang.String")java.type:"long" -> {
                        %74 : Var<java.type:"java.lang.String"> = var %73 @loc="8:94" @"rec$";
                        %75 : java.type:"java.lang.String" = var.load %74 @loc="8:94";
                        %76 : java.type:"int" = invoke %75 @loc="8:94" @java.ref:"java.lang.Object::hashCode():int";
                        %77 : java.type:"long" = conv %76 @loc="8:94";
                        return %77 @loc="8:94";
                    };
                    %78 : java.type:"java.util.stream.LongStream" = invoke %71 %72 @loc="8:16" @java.ref:"java.util.stream.Stream::mapToLong(java.util.function.ToLongFunction):java.util.stream.LongStream";
                    %79 : java.type:"long" = invoke %78 @loc="8:16" @java.ref:"java.util.stream.LongStream::sum():long";
                    %80 : java.type:"java.lang.Long" = invoke %79 @loc="8:5" @java.ref:"java.lang.Long::valueOf(long):java.lang.Long";
                    invoke %80 @loc="8:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    return @loc="1:1";
                };""";
    }

    @Override
    protected String getExpectedTransformed() {
        return """
                func @loc="1:1:Stream.java" @"main" (%0 : java.type:"Stream")java.type:"void" -> {
                    %1 : java.type:"int" = constant @loc="2:37" @1;
                    %2 : java.type:"java.lang.Integer" = invoke %1 @loc="2:29" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    %3 : java.type:"int" = constant @loc="2:40" @2;
                    %4 : java.type:"java.lang.Integer" = invoke %3 @loc="2:29" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    %5 : java.type:"int" = constant @loc="2:43" @3;
                    %6 : java.type:"java.lang.Integer" = invoke %5 @loc="2:29" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    %7 : java.type:"int" = constant @loc="2:46" @4;
                    %8 : java.type:"java.lang.Integer" = invoke %7 @loc="2:29" @java.ref:"java.lang.Integer::valueOf(int):java.lang.Integer";
                    %9 : java.type:"java.util.List<java.lang.Integer>" = invoke %2 %4 %6 %8 @loc="2:29" @java.ref:"java.util.List::of(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object):java.util.List";
                    %10 : Var<java.type:"java.util.List<java.lang.Integer>"> = var %9 @loc="2:5" @"source1";
                    %11 : java.type:"int" = constant @loc="3:21" @4;
                    %12 : java.type:"int[]" = new %11 @loc="3:21" @java.ref:"int[]::(int)";
                    %13 : java.type:"int" = constant @loc="3:22" @1;
                    %14 : java.type:"int" = constant @loc="3:21" @0;
                    array.store %12 %14 %13 @loc="3:21";
                    %15 : java.type:"int" = constant @loc="3:25" @2;
                    %16 : java.type:"int" = constant @loc="3:21" @1;
                    array.store %12 %16 %15 @loc="3:21";
                    %17 : java.type:"int" = constant @loc="3:28" @3;
                    %18 : java.type:"int" = constant @loc="3:21" @2;
                    array.store %12 %18 %17 @loc="3:21";
                    %19 : java.type:"int" = constant @loc="3:31" @4;
                    %20 : java.type:"int" = constant @loc="3:21" @3;
                    array.store %12 %20 %19 @loc="3:21";
                    %21 : Var<java.type:"int[]"> = var %12 @loc="3:5" @"source2";
                    %22 : java.type:"java.lang.String" = constant @loc="4:22" @"1234";
                    %23 : Var<java.type:"java.lang.String"> = var %22 @loc="4:5" @"source3";
                    %24 : java.type:"java.util.List<java.lang.Integer>" = var.load %10 @loc="6:16";
                    %25 : java.type:"java.util.ArrayList" = new @loc="6:16" @java.ref:"java.util.ArrayList::()";
                    %26 : Var<java.type:"java.util.List<java.lang.Integer>"> = var %25 @loc="6:16" @"streamResult";
                    java.enhancedFor @loc="6:16"
                        ()java.type:"java.util.Collection<java.lang.Integer>" -> {
                            yield %24;
                        }
                        (%27 : java.type:"java.lang.Integer")Var<java.type:"java.lang.Integer"> -> {
                            %28 : Var<java.type:"java.lang.Integer"> = var %27 @"s";
                            yield %28;
                        }
                        (%29 : Var<java.type:"java.lang.Integer">)java.type:"void" -> {
                            %30 : Var<java.type:"boolean"> = var @loc="6:16";
                            %31 : java.type:"java.lang.Integer" = var.load %29 @loc="6:16";
                            %32 : Var<java.type:"java.lang.Integer"> = var %31 @loc="6:40" @"i";
                            %33 : java.type:"java.lang.Integer" = var.load %32 @loc="6:45";
                            %34 : java.type:"int" = invoke %33 @loc="6:45" @java.ref:"java.lang.Integer::intValue():int";
                            %35 : java.type:"int" = constant @loc="6:49" @2;
                            %36 : java.type:"int" = mod %34 %35 @loc="6:45";
                            %37 : java.type:"int" = constant @loc="6:54" @0;
                            %38 : java.type:"boolean" = eq %36 %37 @loc="6:45";
                            var.store %30 %38;
                            java.if
                                ()java.type:"boolean" -> {
                                    %39 : java.type:"boolean" = var.load %30 @loc="6:16";
                                    yield %39 @loc="6:16";
                                }
                                ()java.type:"void" -> {
                                    %40 : Var<java.type:"java.lang.Integer"> = var @loc="6:16";
                                    %41 : java.type:"java.lang.Integer" = var.load %29 @loc="6:16";
                                    %42 : Var<java.type:"java.lang.Integer"> = var %41 @loc="6:61" @"i";
                                    %43 : java.type:"java.lang.Integer" = var.load %42 @loc="6:66";
                                    var.store %40 %43;
                                    %44 : java.type:"java.lang.Integer" = var.load %40 @loc="6:16";
                                    %45 : java.type:"boolean" = invoke %26 %44 @loc="6:16" @java.ref:"java.util.List::add(java.lang.Object):boolean";
                                    yield @loc="6:16";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            yield @loc="6:16";
                        };
                    %46 : java.type:"java.util.List<java.lang.Integer>" = var.load %26 @loc="6:16";
                    invoke %46 @loc="6:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    %47 : java.type:"int[]" = var.load %21 @loc="7:46";
                    %48 : java.type:"int" = constant @loc="7:32" @0;
                    %49 : java.type:"int[]" = new %48 @loc="7:32" @java.ref:"int[]::(int)";
                    %50 : Var<java.type:"int[]"> = var %49 @loc="7:32" @"streamResult";
                    java.enhancedFor @loc="7:32"
                        ()java.type:"int[]" -> {
                            yield %47;
                        }
                        (%51 : java.type:"int")Var<java.type:"int"> -> {
                            %52 : Var<java.type:"int"> = var %51 @"s";
                            yield %52;
                        }
                        (%53 : Var<java.type:"int">)java.type:"void" -> {
                            %54 : Var<java.type:"boolean"> = var @loc="7:32";
                            %55 : java.type:"int" = var.load %53 @loc="7:32";
                            %56 : Var<java.type:"int"> = var %55 @loc="7:62" @"i";
                            %57 : java.type:"int" = var.load %56 @loc="7:67";
                            %58 : java.type:"int" = constant @loc="7:71" @2;
                            %59 : java.type:"int" = mod %57 %58 @loc="7:67";
                            %60 : java.type:"int" = constant @loc="7:76" @0;
                            %61 : java.type:"boolean" = eq %59 %60 @loc="7:67";
                            var.store %54 %61;
                            java.if
                                ()java.type:"boolean" -> {
                                    %62 : java.type:"boolean" = var.load %54 @loc="7:32";
                                    yield %62 @loc="7:32";
                                }
                                ()java.type:"void" -> {
                                    %63 : java.type:"int[]" = var.load %50 @loc="7:32";
                                    %64 : java.type:"int" = array.length %63 @loc="7:32";
                                    %65 : java.type:"int" = constant @loc="7:32" @1;
                                    %66 : java.type:"int" = add %64 %65 @loc="7:32";
                                    %67 : java.type:"int[]" = invoke %50 %66 @loc="7:32" @java.ref:"java.util.Arrays::copyOf(int[], int):int[]";
                                    var.store %50 %67 @loc="7:32";
                                    %68 : java.type:"int[]" = var.load %50 @loc="7:32";
                                    %69 : java.type:"int" = array.length %68 @loc="7:32";
                                    %70 : java.type:"int" = constant @loc="7:32" @1;
                                    %71 : java.type:"int" = sub %69 %70 @loc="7:32";
                                    %72 : java.type:"int[]" = var.load %50 @loc="7:32";
                                    %73 : java.type:"int" = var.load %53 @loc="7:32";
                                    array.store %72 %71 %73 @loc="7:32";
                                    yield @loc="7:32";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            yield @loc="7:32";
                        };
                    %74 : java.type:"int[]" = var.load %50 @loc="7:32";
                    %75 : java.type:"java.lang.String" = invoke %74 @loc="7:16" @java.ref:"java.util.Arrays::toString(int[]):java.lang.String";
                    invoke %75 @loc="7:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    %76 : java.type:"java.lang.String" = var.load %23 @loc="8:16";
                    %77 : java.type:"long" = constant @loc="8:16" @0;
                    %78 : Var<java.type:"long"> = var %77 @loc="8:16" @"sum";
                    java.enhancedFor @loc="8:16"
                        ()java.type:"char[]" -> {
                            %79 : java.type:"char[]" = invoke %76 @java.ref:"java.lang.String::toCharArray():char[]";
                            yield %79;
                        }
                        (%80 : java.type:"int")Var<java.type:"int"> -> {
                            %81 : Var<java.type:"int"> = var %80 @"s";
                            yield %81;
                        }
                        (%82 : Var<java.type:"int">)java.type:"void" -> {
                            %83 : Var<java.type:"boolean"> = var @loc="8:16";
                            %84 : java.type:"int" = var.load %82 @loc="8:16";
                            %85 : Var<java.type:"int"> = var %84 @loc="8:39" @"i";
                            %86 : java.type:"int" = var.load %85 @loc="8:44";
                            %87 : java.type:"int" = constant @loc="8:48" @2;
                            %88 : java.type:"int" = mod %86 %87 @loc="8:44";
                            %89 : java.type:"int" = constant @loc="8:53" @0;
                            %90 : java.type:"boolean" = eq %88 %89 @loc="8:44";
                            var.store %83 %90;
                            java.if
                                ()java.type:"boolean" -> {
                                    %91 : java.type:"boolean" = var.load %83 @loc="8:16";
                                    yield %91 @loc="8:16";
                                }
                                ()java.type:"void" -> {
                                    %92 : Var<java.type:"java.lang.String"> = var @loc="8:16";
                                    %93 : java.type:"int" = var.load %82 @loc="8:16";
                                    %94 : Var<java.type:"int"> = var %93 @loc="8:65" @"x$0";
                                    %95 : java.type:"int" = var.load %94 @loc="8:65";
                                    %96 : java.type:"java.lang.String" = invoke %95 @loc="8:65" @java.ref:"java.lang.Integer::toString(int):java.lang.String";
                                    var.store %92 %96;
                                    %97 : Var<java.type:"long"> = var @loc="8:16";
                                    %98 : java.type:"java.lang.String" = var.load %92 @loc="8:16";
                                    %99 : Var<java.type:"java.lang.String"> = var %98 @loc="8:94" @"rec$";
                                    %100 : java.type:"java.lang.String" = var.load %99 @loc="8:94";
                                    %101 : java.type:"int" = invoke %100 @loc="8:94" @java.ref:"java.lang.Object::hashCode():int";
                                    %102 : java.type:"long" = conv %101 @loc="8:94";
                                    var.store %97 %102;
                                    %103 : java.type:"long" = var.load %78 @loc="8:16";
                                    %104 : java.type:"long" = var.load %97 @loc="8:16";
                                    %105 : java.type:"long" = add %103 %104 @loc="8:16";
                                    var.store %78 %105 @loc="8:16";
                                    yield @loc="8:16";
                                }
                                ()java.type:"void" -> {
                                    yield;
                                };
                            yield @loc="8:16";
                        };
                    %106 : java.type:"long" = var.load %78 @loc="8:16";
                    %107 : java.type:"java.lang.Long" = invoke %106 @loc="8:5" @java.ref:"java.lang.Long::valueOf(long):java.lang.Long";
                    invoke %107 @loc="8:5" @java.ref:"java.lang.IO::println(java.lang.Object):void";
                    return @loc="1:1";
                };""";
    }
}
