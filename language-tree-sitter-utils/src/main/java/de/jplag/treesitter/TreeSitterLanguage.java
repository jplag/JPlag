package de.jplag.treesitter;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

import de.jplag.treesitter.library.NativeLibraryType;

public abstract class TreeSitterLanguage {
    protected static final ValueLayout VOID_POINTER = ValueLayout.ADDRESS
            .withTargetLayout(MemoryLayout.sequenceLayout(Long.MAX_VALUE, ValueLayout.JAVA_BYTE));
    protected static final FunctionDescriptor FUNCTION_DESCRIPTOR = FunctionDescriptor.of(VOID_POINTER);
    protected static final Linker LINKER = Linker.nativeLinker();

    protected final Arena arena = Arena.ofAuto();

    protected static UnsatisfiedLinkError unresolved(String name) {
        return new UnsatisfiedLinkError("Unresolved symbol: " + name);
    }

    protected abstract NativeLibraryType libraryType();

    protected abstract String symbolName();

    protected MemorySegment call() {
        SymbolLookup symbols = SymbolLookup.libraryLookup(libraryType().create().getLibraryPath().toString(), arena);
        MemorySegment address = symbols.find(symbolName()).orElseThrow(() -> unresolved(symbolName()));
        try {
            MethodHandle function = LINKER.downcallHandle(address, FUNCTION_DESCRIPTOR);
            return ((MemorySegment) function.invokeExact()).asReadOnly();
        } catch (Throwable throwable) {
            throw new RuntimeException("Call to " + symbolName() + " failed", throwable);
        }
    }
}
