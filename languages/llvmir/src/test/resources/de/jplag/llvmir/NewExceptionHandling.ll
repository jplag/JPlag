define i32 @f() nounwind personality i32 (...)* @__CxxFrameHandler3 {
entry:
  %obj = alloca %struct.Cleanup, align 4
  %e = alloca i32, align 4
  %call = invoke %struct.Cleanup* @"??0Cleanup@@QEAA@XZ"(%struct.Cleanup* nonnull %obj)
          to label %invoke.cont unwind label %lpad.catch

invoke.cont:                                      ; preds = %entry
  invoke void @"?may_throw@@YAXXZ"()
          to label %invoke.cont.2 unwind label %lpad.cleanup

invoke.cont.2:                                    ; preds = %invoke.cont
  call void @"??_DCleanup@@QEAA@XZ"(%struct.Cleanup* nonnull %obj) nounwind
  br label %return

return:                                           ; preds = %invoke.cont.3, %invoke.cont.2
  %retval.0 = phi i32 [ 0, %invoke.cont.2 ], [ %3, %invoke.cont.3 ]
  ret i32 %retval.0

lpad.cleanup:                                     ; preds = %invoke.cont.2
  %0 = cleanuppad within none []
  call void @"??1Cleanup@@QEAA@XZ"(%struct.Cleanup* nonnull %obj) nounwind
  cleanupret from %0 unwind label %lpad.catch

lpad.catch:                                       ; preds = %lpad.cleanup, %entry
  %1 = catchswitch within none [label %catch.body] unwind label %lpad.terminate

catch.body:                                       ; preds = %lpad.catch
  %catch = catchpad within %1 [%rtti.TypeDescriptor2* @"??_R0H@8", i32 0, i32* %e]
  invoke void @"?may_throw@@YAXXZ"()
          to label %invoke.cont.3 unwind label %lpad.terminate

invoke.cont.3:                                    ; preds = %catch.body
  %2 = load i32, i32* %e, align 4
  catchret from %catch to label %return

lpad.terminate:                                   ; preds = %catch.body, %lpad.catch
  cleanuppad within none []
  call void @"?terminate@@YAXXZ"()
  unreachable
}