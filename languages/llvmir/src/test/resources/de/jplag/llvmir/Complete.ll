; ModuleID = 'Complete.c'
source_filename = "Complete.c"
target datalayout = "e-m:o-i64:64-i128:128-n32:64-S128"
target triple = "arm64-apple-macosx13.0.0"

%Type_Def = type i8
@Global_Var = private unnamed_addr constant [13 x i8] c"Hello World!\00", align 1
@struct.const = private constant {i32 , float } {i32 4, float 17.8}

%struct.Struct = type { i32 }

module asm "movl  $1, %eax"

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
  %26 = getelementptr inbounds %struct.Struct, ptr %5, i32 0, i32 0
  store i32 1, ptr %26, align 4

  %vec = shufflevector <4 x i32> %v1, <4 x i32> %v2, <4 x i32> <i32 0, i32 4, i32 1, i32 5>
  %vec = insertelement <4 x i32> %vec, i32 1, i32 0
  %elem = extractelement <4 x i32> %vec, i32 0
  %val = extractvalue {i32, float } %agg, 0
  %struc = insertvalue {i32, float} undef, i32 1, 0

  fence acquire
  %old = atomicrmw add ptr %ptr, i32 1 acquire

  %orig = load atomic i32, ptr %ptr unordered, align 4                      ; yields i32
  br label %loop

loop:
  %cmp = phi i32 [ %orig, %entry ], [%value_loaded, %loop]
  %squared = mul i32 %cmp, %cmp
  %val_success = cmpxchg ptr %ptr, i32 %cmp, i32 %squared acq_rel monotonic ; yields  { i32, i1 }
  %value_loaded = extractvalue { i32, i1 } %val_success, 0
  %success = extractvalue { i32, i1 } %val_success, 1
  br i1 %success, label %done, label %loop

done:
  %false = icmp eq i32 4, 5
  ret i32 0
}


declare i32 @printf(ptr noundef, ...) #1
