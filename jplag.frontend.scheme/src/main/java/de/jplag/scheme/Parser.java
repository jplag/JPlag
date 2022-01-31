package de.jplag.scheme;

import java.io.File;

import de.jplag.AbstractParser;
import de.jplag.TokenList;

public class Parser extends AbstractParser implements SchemeTokenConstants {
    private String actFile;

    private TokenList struct;

    public TokenList parse(File dir, String files[]) {
        struct = new TokenList();
        errors = 0;
        SchemeParser parser = null;// no worry it will be reinitialized
        // in method parseFile(...)
        for (int i = 0; i < files.length; i++) {
            actFile = files[i];
            getErrorConsumer().print(null, "Parsing file " + files[i]);
            if (!SchemeParser.parseFile(dir, files[i], parser, this))
                errors++;
            struct.addToken(new SchemeToken(FILE_END, actFile, 1));
        }
        this.parseEnd();
        return struct;
    }

    public void add(int type, Token token) {
        struct.addToken(new SchemeToken(type, actFile, token.beginLine));
        /*
         * System.out.println(token.beginLine+"\t"+ (new SchemeToken(0,null,0)).type2string(type)+"\t"+ token.image);
         */
    }

}
