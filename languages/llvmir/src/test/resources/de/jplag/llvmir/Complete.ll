; ModuleID = 'Complete.c'
source_filename = "Complete.c"
target datalayout = "e-m:o-i64:64-i128:128-n32:64-S128"
target triple = "arm64-apple-macosx13.0.0"

@Global_Var = private unnamed_addr constant [14 x i8] c"Hello World!\0A\00", align 1
@struct.const = private constant {i32, double} {i32 4, double 8.12}

%struct.Struct = type { i32 }

; Function Attrs: noinline nounwind optnone ssp uwtable
define i32 @main() #0 {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i32, align 4
  %4 = alloca float, align 4
  %5 = alloca %struct.Struct, align 4
  store i32 0, ptr %1, align 4
  store i32 4, ptr %2, align 4
  %6 = load i32, ptr %2, align 4
  %7 = add nsw i32 5, %6
  store i32 %7, ptr %3, align 4
  %8 = load i32, ptr %2, align 4
  %9 = sub nsw i32 5, %8
  store i32 %9, ptr %3, align 4
  %10 = load i32, ptr %2, align 4
  %11 = mul nsw i32 5, %10
  store i32 %11, ptr %3, align 4
  %12 = load i32, ptr %2, align 4
  %13 = sdiv i32 5, %12
  %14 = sitofp i32 %13 to float
  store float %14, ptr %4, align 4
  %15 = load i32, ptr %2, align 4
  %16 = srem i32 5, %15
  store i32 %16, ptr %3, align 4
  %17 = load i32, ptr %2, align 4
  %18 = shl i32 %17, 5
  store i32 %18, ptr %3, align 4
  %19 = load i32, ptr %2, align 4
  %20 = and i32 %19, 5
  store i32 %20, ptr %3, align 4
  %21 = load i32, ptr %2, align 4
  %22 = or i32 %21, 5
  store i32 %22, ptr %3, align 4
  %23 = load i32, ptr %2, align 4
  %24 = xor i32 %23, 5
  store i32 %24, ptr %3, align 4
  %25 = call i32 (ptr, ...) @printf(ptr noundef @Global_Var)
  br label %vectors

vectors:
  %26 = call i32 @vector(<4 x i32> <i32 1, i32 2, i32 3, i32 4>, <4 x i32> <i32 1, i32 2, i32 3, i32 4>)
  switch i32 %26, label %vectors [ i32 10, label %aggregates
                                   i32 1, label %vectors
                                   i32 2, label %vectors ]

aggregates:
  %struc = insertvalue {i32} undef, i32 1, 0
  %27 = extractvalue {i32} %struc, 0
  callbr void asm "", "r,!i"(i32 0)
              to label %memory [label %aggregates]

memory:
  fence acquire
  br label %entry

entry:
  %28 = getelementptr inbounds %struct.Struct, ptr %5, i32 0, i32 0
  store i32 1, ptr %28, align 4
  %old = atomicrmw add ptr %28, i32 1 acquire
  %orig = load atomic i32, ptr %28 unordered, align 4
  br label %loop

loop:
  %cmp = phi i32 [ %orig, %entry ], [%value_loaded, %loop]
  %squared = mul i32 %cmp, %cmp
  %val_success = cmpxchg ptr %28, i32 %cmp, i32 %squared acq_rel monotonic
  %value_loaded = extractvalue { i32, i1 } %val_success, 0
  %success = extractvalue { i32, i1 } %val_success, 1
  br i1 %success, label %done, label %loop

done:
  %false = icmp eq i32 4, 5
  %first = select i1 true, i8 17, i8 42
  ret i32 0
}

define i32 @vector(<4 x i32> %v1, <4 x i32> %v2) {
  %vec = shufflevector <4 x i32> %v1, <4 x i32> %v2, <4 x i32> <i32 0, i32 4, i32 1, i32 5>
  %vec_ins = insertelement <4 x i32> %vec, i32 10, i32 0
  %elem = extractelement <4 x i32> %vec_ins, i32 0
  ret i32 %elem
}


declare i32 @printf(ptr noundef, ...) #1
