package jplag.json;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import jplag.Structure;
import jplag.json.grammar.*;
import jplag.json.grammar.JsonParser;
import jplag.json.grammar.JsonParser.CompilationUnitContext;



import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Parser extends jplag.Parser implements JsonTokenConstants {
    private Structure struct = new Structure();
    private String currentFile;
    private boolean runOut = false;


    public void outOfSerials() {
        if (runOut)
            return;
        runOut = true;
        errors++;
        program.print("ERROR: Out of serials!", null);
        System.out.println("jplag.json.Parser: ERROR: Out of serials!");
    }

    public static void main(String args[]) {
        String[] ffiles = {};
        Parser parser = new Parser();
        parser.setProgram(new jplag.StrippedProgram());
        jplag.Structure struct = parser.parse(null, ffiles);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(ffiles[0])));
            int lineNr = 1;
            int token = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (token < struct.size()) {
                    boolean first = true;
                    while (struct.tokens[token] != null
                            && struct.tokens[token].getLine() == lineNr) {
                        if (!first) {
                            System.out.println();
                        }
                        JsonToken tok = (JsonToken) struct.tokens[token];
                        System.out.print(JsonToken.type2string(tok.type) + " ("
                                + tok.getLine() + ","
                                + tok.getColumn() + ","
                                + tok.getLength() + ")\t");
                        first = false;
                        token++;
                    }
                    if (first) {
                        System.out.print("                \t");
                    }
                }
                System.out.println(line);
                lineNr++;
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public jplag.Structure parse(File dir, String files[]) {
        struct = new Structure();
        errors = 0;
        for (int i = 0; i < files.length; i++) {
            getProgram().print(null, "Parsing file " + files[i] + "\n");
            if (!parseFile(dir, files[i])) {
                errors++;
            }
            System.gc();//Emeric
            struct.addToken(new JsonToken(FILE_END, files[i], -1, -1, -1));
        }
        this.parseEnd();
        return struct;
    }

    public boolean parseFile(File dir, String file) {
        BufferedInputStream fis;

        ANTLRInputStream input;
        try {
            fis = new BufferedInputStream(new FileInputStream(new File(dir, file)));
            currentFile = file;
            input = new ANTLRInputStream(fis);

            // create a lexer that feeds off of input CharStream
            JsonLexer lexer = new JsonLexer(input);

            // create a buffer of tokens pulled from the lexer
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // create a parser that feeds off the tokens buffer
            JsonParser parser = new JsonParser(tokens);

            CompilationUnitContext cuc = parser.compilationUnit();

            ParseTreeWalker ptw = new ParseTreeWalker();
            for (int i = 0; i < cuc.getChildCount(); i++) {
                ParseTree pt = cuc.getChild(i);
                ptw.walk(new JplagJsonListener(this), pt);
            }

        } catch (IOException e) {
            getProgram().addError("Parsing Error in '" + file + "':\n" + e.getMessage() + "\n");
            return false;
        }

        return true;
    }

    public void add(int type, org.antlr.v4.runtime.Token tok) {
        struct.addToken(new JsonToken(type, (currentFile == null ? "null" : currentFile), tok.getLine(), tok.getCharPositionInLine() + 1,
                tok.getText().length()));
    }

    public void add(String text, org.antlr.v4.runtime.Token tok) {
        struct.addToken(new JsonToken(text, (currentFile == null ? "null" : currentFile), tok.getLine(), tok.getCharPositionInLine() + 1,
                text.length(), this));
    }

    public void addEnd(int type, org.antlr.v4.runtime.Token tok) {
        struct.addToken(new JsonToken(type, (currentFile == null ? "null" : currentFile), tok.getLine(), struct.tokens[struct.size()-1].getColumn() + 1,0));
    }
}
