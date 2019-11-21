package jplag.generic;

import jplag.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.Iterator;

public abstract class GenericParser extends StreamParser implements GenericTokenConstants {
    protected abstract GenericToken makeToken(int type, String file, int line, int column, int lengt);
    protected abstract String getCommandLineProgram();

    private boolean processOutput(Tuple3<Integer, String, String> cmdRes, @NotNull TokenAdder adder) {
        Integer exitValue = cmdRes.getA();
        String stdout = cmdRes.getB();
        String stderr = cmdRes.getC();
        System.out.println(stdout);

        if (!stderr.isEmpty() && !stderr.startsWith("[]")) {
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
                    adder.addToken(this.makeToken(
                            type,
                            adder.currentFile,
                            token.getInt("line"),
                            token.getInt("column"),
                            token.getInt("length")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }


    @NotNull
    @Override
    public Token getEndOfFileToken(String file) {
        return this.makeToken(FILE_END, file, -1, -1, -1);
    }

    @Override
    public boolean parseStream(@NotNull InputStream stream, @NotNull TokenAdder adder) throws IOException {
        Tuple3<Integer, String, String> cmdRes = CommandExecutor.execute(stream,getCommandLineProgram(), "/dev/stdin");
        return this.processOutput(cmdRes, adder);
    }

    @Override
    public boolean parseFile(File dir, String file) {
        String currentFile = new File(dir, file).getAbsolutePath();
        Tuple3<Integer, String, String> cmdRes = CommandExecutor.execute(getCommandLineProgram(), currentFile);
        return this.processOutput(cmdRes, new TokenAdder(file) {
            @Nullable
            @Override
            public Token getLast() {
                return struct.tokens[struct.size() - 1];
            }

            @Override
            public void addToken(@NotNull Token token) {
                struct.addToken(token);
            }
        });
    }
}
