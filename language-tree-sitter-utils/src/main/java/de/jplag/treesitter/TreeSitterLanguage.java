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

/**
 * Abstract base class for Tree-sitter language implementations.
 * <p>
 * This class provides the foundation for implementing Tree-sitter language parsers in JPlag. It handles the native
 * library loading and symbol resolution required to interact with Tree-sitter language grammars. Subclasses must
 * implement the specific language type and symbol name to enable parsing of their target language.
 * </p>
 * <p>
 * The class uses Java's Foreign Function and Memory API to safely interact with native Tree-sitter libraries and
 * manages memory allocation through an auto-managed arena.
 * </p>
 */
public abstract class TreeSitterLanguage {
    /** Value layout for void pointers in native memory. */
    protected static final ValueLayout VOID_POINTER = ValueLayout.ADDRESS
            .withTargetLayout(MemoryLayout.sequenceLayout(Long.MAX_VALUE, ValueLayout.JAVA_BYTE));
    /** Function descriptor for language grammar functions. */
    protected static final FunctionDescriptor FUNCTION_DESCRIPTOR = FunctionDescriptor.of(VOID_POINTER);
    /** Native linker for function calls. */
    protected static final Linker LINKER = Linker.nativeLinker();

    /** Auto-managed arena for native memory allocation. */
    protected final Arena arena = Arena.ofAuto();

    /**
     * Creates a new Tree-sitter language instance.
     */
    protected TreeSitterLanguage() {
    }

    /**
     * Creates an {@link UnsatisfiedLinkError} for unresolved native symbols.
     * @param name The name of the unresolved symbol
     * @return A new UnsatisfiedLinkError with a descriptive message
     */
    protected static UnsatisfiedLinkError unresolved(String name) {
        return new UnsatisfiedLinkError("Unresolved symbol: " + name);
    }

    /**
     * Returns the native library type for this language implementation.
     * @return The native library type containing the language grammar
     */
    protected abstract NativeLibraryType libraryType();

    /**
     * Returns the symbol name for the language grammar function.
     * @return The symbol name to look up in the native library
     */
    protected abstract String symbolName();

    /**
     * Calls the native language grammar function and returns the resulting memory segment.
     * <p>
     * This method loads the native library, looks up the language grammar symbol, and invokes it to obtain the memory
     * segment containing the language grammar. The returned segment is read-only to prevent accidental modification.
     * </p>
     * @return A read-only memory segment containing the language grammar
     * @throws RuntimeException If the native function call fails
     * @throws UnsatisfiedLinkError If the symbol cannot be found in the library
     */
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
