package jplag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TokenAdder {
    public @NotNull String currentFile;

    public TokenAdder(@NotNull String currentFile) {
        this.currentFile = currentFile;
    }

    public abstract @Nullable Token getLast();

    public abstract void addToken(@NotNull Token token);
}
