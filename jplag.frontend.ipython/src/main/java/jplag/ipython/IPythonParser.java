package jplag.ipython;

import jplag.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class IPythonParser extends StreamParser {
    private Map<String, jplag.Parser> parsers = new Hashtable<>();

    static private String maybeJoinString(Object object) {
        if (object instanceof JSONArray) {
            StringBuilder res = new StringBuilder();
            Iterator<Object> iter = ((JSONArray) object).iterator();
            while (iter.hasNext()) {
                res.append(iter.next());
            }
            return res.toString();
        } else if (object instanceof String) {
            return (String) object;
        } else {
            return null;
        }
    }

    static Tuple2<? extends StreamParser, String> getLanguageParser(JSONObject cell) {
        String lang = null;
        try {
            lang = cell.getJSONObject("metadata").getJSONObject("language_info").getString("name");
        } catch (Exception e) {
            e.printStackTrace();
            lang = "python";
        }

        switch (lang.toLowerCase()) {
            case "javascript":
                return new Tuple2<>(new jplag.generic.JavascriptParser(), "javascript");
            case "php":
                return new Tuple2<>(new jplag.generic.PhpParser(), "php");
            case "c":
                return new Tuple2<>(new jplag.cpp.Scanner(), "c");
            case "c#":
                return new Tuple2<>(new jplag.csharp.Parser(), "c#");
            case "python":
            default:
                return new Tuple2<>(new jplag.python3.Parser(), "python");
        }
    }

    @NotNull
    @Override
    public Token getEndOfFileToken(String file) {
        return new IPythonToken(IPythonTokenConstants.FILE_END, file, -1, -1, -1);
    }

    @Override
    public boolean parseStream(@NotNull InputStream stream, @NotNull final TokenAdder adder) throws IOException {
        final JSONObject ipython;
        try {
            ipython = new JSONObject(new JSONTokener(stream));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        final Tuple2<? extends StreamParser, String> lang = IPythonParser.getLanguageParser(ipython);
        Iterator<Object> cells = null;
        try {
            if (ipython.has("cells")) {
                cells = ipython.getJSONArray("cells").iterator();
            } else {
                cells = new Iterator<Object>() {
                    Iterator<Object> worksheetIter = ipython.getJSONArray("worksheets").iterator();
                    Iterator<Object> cellsIter = null;

                    @Override
                    public boolean hasNext() {
                        while (cellsIter == null || !cellsIter.hasNext()) {
                            if (!worksheetIter.hasNext()) {
                                return false;
                            }
                            cellsIter = ((JSONObject)worksheetIter.next()).getJSONArray("cells").iterator();
                        }
                        return true;
                    }

                    @Override
                    public Object next() {
                        return cellsIter.next();
                    }

                    @Override
                    public void remove() {
                    }
                };
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        int currentStartLineNumber = 1;

        while (cells.hasNext()) {
            int amountLines = 1;
            int extraLines = 0;

            Object item = cells.next();
            try {
                JSONObject cell = (JSONObject) item;
                String cellType = cell.getString("cell_type");
                if (cellType.equals("raw")) {
                    amountLines = 0;
                    continue;
                }
                String source = maybeJoinString(cell.has("source") ? cell.get("source") : cell.get("input"));

                if (!cellType.equals("code")) {
                    adder.addToken(IPythonToken.createToken(source, adder.currentFile, currentStartLineNumber));
                    continue;
                }

                amountLines = JplagStringUtils.countOccurences(source, '\n') + 1;
                final int ln = currentStartLineNumber;

                lang.getA().parseStream(new ByteArrayInputStream(source.getBytes()), new TokenAdder(adder.currentFile) {
                    @Override
                    public Token getLast() {
                        return adder.getLast();
                    }

                    @Override
                    public void addToken(@NotNull Token token) {
                        adder.addToken(IPythonToken.createToken(token, lang.getB(), ln));
                    }
                });

                for (Object output : cell.getJSONArray("outputs")) {
                    try {
                        adder.addToken(IPythonToken.createToken(
                                output.toString(), adder.currentFile, currentStartLineNumber + amountLines + extraLines)
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        extraLines++;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                currentStartLineNumber += amountLines + extraLines;
            }
        }

        return true;
    }
}
