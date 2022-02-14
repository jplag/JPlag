
package de.jplag.text;

import java.io.File;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

public class Language implements de.jplag.Language {

    private ErrorConsumer program;

    private Parser parser = new Parser();

    public Language(ErrorConsumer program) {
        this.program = program;
        this.parser.setProgram(this.program);
    }

    @Override
    public int errorCount() {
        return this.parser.errorsCount();
    }

    @Override
    public String[] suffixes() {
        return new String[] {".TXT", ".txt", ".ASC", ".asc", ".TEX", ".tex"};
    }

    @Override
    public String getName() {
        return "Text Parser";
    }

    @Override
    public String getShortName() {
        return "text";
    }

    @Override
    public int minimumTokenMatch() {
        return 5;
    }

    @Override
    public TokenList parse(File dir, String[] files) {
        return this.parser.parse(dir, files);
    }

    @Override
    public boolean hasErrors() {
        return this.parser.hasErrors();
    }

    @Override
    public boolean supportsColumns() {
        return true;
    }

    @Override
    public boolean isPreformatted() {
        return false;
    }

    @Override
    public boolean usesIndex() {
        return false;
    }

    @Override
    public int numberOfTokens() {
        return parser.serial;
    }
}
