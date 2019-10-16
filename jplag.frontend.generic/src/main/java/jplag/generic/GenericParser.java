package jplag.generic;

import jplag.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.Iterator;

public abstract class GenericParser extends jplag.Parser implements GenericTokenConstants {
    private Structure struct = new Structure();
    private String currentFile;

    protected abstract GenericToken makeToken(int type, String file, int line, int column, int lengt);
    protected abstract String getCommandLineProgram();

    public jplag.Structure parse(File dir, String[] files) {
        struct = new Structure();
        errors = 0;
        for (int i = 0; i < files.length; i++) {
            getProgram().print(null, "Parsing file " + files[i] + "\n");
            if (!parseFile(dir, files[i]))
                errors++;
            System.gc();
            struct.addToken(this.makeToken(FILE_END, files[i], -1, -1, -1));
        }
        this.parseEnd();
        return struct;
    }

    public boolean parseFile(File dir, String file) {
        currentFile = file;
        Tuple3<Integer, String, String> cmdRes = CommandExecutor.execute(getCommandLineProgram(), new File(dir, file).getAbsolutePath());
        Integer exitValue = cmdRes.getA();
        String stdout = cmdRes.getB();
        String stderr = cmdRes.getC();

        if (!stderr.startsWith("[]")) {
            System.err.println(stderr);
        }
        if (exitValue != 0) {
            return false;
        }


        JSONArray res;
        try {
            res = new JSONArray(new JSONTokener(cmdRes.getB()));
        } catch (Exception e) {
            System.err.println(stdout);
            e.printStackTrace();
            return false;
        }

        Iterator<Object> jsonIt = res.iterator();
        while (jsonIt.hasNext()) {
            Object item = jsonIt.next();
            try {
                JSONObject token = (JSONObject) item;
                int type = token.getJSONObject("token").getInt("value");

                if (type == GenericTokenConstants.FILE_END) {
                    getProgram().print(null, "Command outputted file end token");
                } else {
                    this.add(
                            type,
                            token.getInt("line"),
                            token.getInt("column"),
                            token.getInt("length")
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private void add(int type, int line, int column, int length) {
        struct.addToken(this.makeToken(type, (currentFile == null ? "null" : currentFile), line, column, length));
    }
}
