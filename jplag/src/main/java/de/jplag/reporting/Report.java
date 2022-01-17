package de.jplag.reporting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Language;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.Token;
import de.jplag.TokenList;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.ReportGenerationException;
import de.jplag.options.JPlagOptions;

/**
 * This class writes all the HTML pages.
 */
public class Report { // Mostly legacy code with some minor improvements.

    private static final String CSV_FILE = "matches_avg.csv";
    private static final String[] PICS = {"forward.gif", "back.gif"};

    private final Map<JPlagComparison, Integer> comparisonToIndex = new HashMap<>();
    private final Messages msg;
    private final File reportDir;
    private final JPlagOptions options;

    private final Language language;
    private JPlagResult result;

    private int currentComparisonIndex = 0;
    private int matchWritingProgess = 0;
    private int matchesWritten = 0;

    public Report(File reportDir, final Language language, JPlagOptions options) throws ReportGenerationException {
        this.reportDir = reportDir;
        this.options = options;
        this.language = language;
        msg = new Messages("en");

        validateReportDir();
    }

    public void writeResult(JPlagResult result) throws ReportGenerationException {
        this.result = result;
        System.out.println("\nWriting report...");
        writeIndex();
        copyStaticFiles();
        writeMatches(result.getComparisons(getOptions().getMaximumNumberOfComparisons()));
        System.out.println("Report exported to " + reportDir.getAbsolutePath());
    }

    private JPlagOptions getOptions() {
        return options;
    }

    private Language getLanguage() {
        return language;
    }

    /*
     * Two colors, represented by Rl,Gl,Bl and Rh,Gh,Bh respectively are mixed according to the percentage "percent"
     */
    private String color(float percent, int Rl, int Rh, int Gl, int Gh, int Bl, int Bh) {
        int redValue = (int) (Rl + (Rh - Rl) * percent / 100);
        int greenValue = (int) (Gl + (Gh - Gl) * percent / 100);
        int blueValue = (int) (Bl + (Bh - Bl) * percent / 100);

        String red = (redValue < 16 ? "0" : "") + Integer.toHexString(redValue);
        String green = (greenValue < 16 ? "0" : "") + Integer.toHexString(greenValue);
        String blue = (blueValue < 16 ? "0" : "") + Integer.toHexString(blueValue);

        return "#" + red + green + blue;
    }

    /*
     * This procedure copies all the data from "data/" into the result-directory.
     */
    private void copyStaticFiles() {
        final String[] fileList = {"back.gif", "forward.gif", "help-en.html", "help-sim-en.html", "logo.png", "fields.js"};

        for (int i = fileList.length - 1; i >= 0; i--) {
            try {
                URL url = Report.class.getResource("data/" + fileList[i]);
                DataInputStream dis = new DataInputStream(url.openStream());

                File dest = new File(reportDir, fileList[i]);
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(dest));

                byte[] buffer = new byte[1024];
                int count;
                do {
                    count = dis.read(buffer);
                    if (count != -1) {
                        dos.write(buffer, 0, count);
                    }
                } while (count != -1);
                dis.close();
                dos.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a new HTML file.
     */
    private HTMLFile createHTMLFile(String name) throws ReportGenerationException {
        File file = new File(reportDir, name);

        try {
            return HTMLFile.fromFile(file);
        } catch (IOException e) {
            throw new ReportGenerationException("Error opening file: " + file);
        }
    }

    private int getComparisonIndex(JPlagComparison comparison) {
        Integer index = comparisonToIndex.get(comparison);

        if (index != null) {
            return index;
        }

        comparisonToIndex.put(comparison, currentComparisonIndex++);

        return currentComparisonIndex - 1;
    }

    /**
     * This method generates an table entry in the list of all comparisons.
     */
    private void reportComparison(HTMLFile htmlFile, JPlagComparison comparison, int index) {
        Match match;
        TokenList tokensA = comparison.getFirstSubmission().getTokenList();
        TokenList tokensB = comparison.getSecondSubmission().getTokenList();
        // sort();

        htmlFile.println("<TABLE BORDER=\"1\" CELLSPACING=\"0\" BGCOLOR=\"#d0d0d0\">");
        htmlFile.println("<TR><TH><TH>" + comparison.getFirstSubmission().getName() + " (" + comparison.similarityOfFirst() + "%)<TH>"
                + comparison.getSecondSubmission().getName() + " (" + comparison.similarityOfSecond() + "%)<TH>"
                + msg.getString("AllMatches.Tokens"));

        for (int i = 0; i < comparison.getMatches().size(); i++) {
            match = comparison.getMatches().get(i);

            Token startA = tokensA.getToken(match.getStartOfFirst());
            Token endA = tokensA.getToken(match.getStartOfFirst() + match.getLength() - 1);
            Token startB = tokensB.getToken(match.getStartOfSecond());
            Token endB = tokensB.getToken(match.getStartOfSecond() + match.getLength() - 1);

            String col = Color.getHexadecimalValue(i);

            htmlFile.print("<TR><TD BGCOLOR=\"" + col + "\"><FONT COLOR=\"" + col + "\">-</FONT>");
            htmlFile.print("<TD><A HREF=\"javascript:ZweiFrames('match" + index + "-0.html#" + i + "',2,'match" + index + "-1.html#" + i
                    + "',3)\" NAME=\"" + i + "\">");
            htmlFile.print(new String(startA.file.getBytes()));

            if (getLanguage().usesIndex()) {
                htmlFile.print("(" + startA.getIndex() + "-" + endA.getIndex() + ")");
            } else {
                htmlFile.print("(" + startA.getLine() + "-" + endA.getLine() + ")");
            }

            htmlFile.print("<TD><A HREF=\"javascript:ZweiFrames('match" + index + "-0.html#" + i + "',2,'match" + index + "-1.html#" + i
                    + "',3)\" NAME=\"" + i + "\">");
            htmlFile.print(startB.file);

            if (getLanguage().usesIndex()) {
                htmlFile.print("(" + startB.getIndex() + "-" + endB.getIndex());
            } else {
                htmlFile.print("(" + startB.getLine() + "-" + endB.getLine());
            }

            htmlFile.println(
                    ")</A><TD ALIGN=center>" + "<FONT COLOR=\"" + comparison.color(match.getLength()) + "\">" + match.getLength() + "</FONT>");
        }

        if (getOptions().hasBaseCode()) {
            htmlFile.print(
                    "<TR><TD BGCOLOR=\"#C0C0C0\"><TD>" + msg.getString("AllMatches.Basecode") + " " + comparison.basecodeSimilarityOfFirst() + "%");
            htmlFile.println("<TD>" + msg.getString("AllMatches.Basecode") + " " + comparison.basecodeSimilarityOfSecond() + "%<TD>&nbsp;");
        }

        htmlFile.println("</TABLE>");
    }



    private synchronized void reportMatchWritingProgress(List<JPlagComparison> comparisons) {
        matchesWritten++;
        int currentProgress = 20 * matchesWritten / comparisons.size() * 5; // report every 5% step
        if (currentProgress > matchWritingProgess) {
            System.out.println("Writing matches: " + currentProgress + "%");
            matchWritingProgess = currentProgress;
        }
    }

    /**
     * Make sure the report directory exists, is a directory and has write access.
     */
    private void validateReportDir() throws ReportGenerationException {
        if (!reportDir.exists() && !reportDir.mkdirs()) {
            throw new ReportGenerationException("Cannot create report directory!");
        }

        if (!reportDir.isDirectory()) {
            throw new ReportGenerationException(reportDir + " is not a directory!");
        }

        if (!reportDir.canWrite()) {
            throw new ReportGenerationException("Cannot write directory: " + reportDir);
        }
    }

    private void writeDistribution(HTMLFile htmlFile) {
        int barLength = 75;
        int[] similarityDistribution = result.getSimilarityDistribution();

        int max = 1;

        for (int i = 0; i < 10; i++) {
            if (similarityDistribution[i] > max) {
                max = similarityDistribution[i];
            }
        }

        htmlFile.println("<H4>" + this.msg.getString("Report.Distribution") + ":</H4>\n<CENTER>");
        htmlFile.println("<TABLE CELLPADDING=1 CELLSPACING=1>");

        for (int i = 9; i >= 0; i--) {
            htmlFile.print("<TR BGCOLOR=" + color(i * 10 + 10, 128, 192, 128, 192, 255, 255) + "><TD ALIGN=center>" + (i * 10) + "% - "
                    + (i * 10 + 10) + "%" + "</TD><TD ALIGN=right>" + similarityDistribution[i] + "</TD><TD>");

            for (int j = (similarityDistribution[i] * barLength / max); j > 0; j--) {
                htmlFile.print("#");
            }

            if (similarityDistribution[i] * barLength / max == 0) {
                if (similarityDistribution[i] == 0) {
                    htmlFile.print(".");
                } else {
                    htmlFile.print("#");
                }
            }

            htmlFile.println("</TD></TR>");
        }

        htmlFile.println("</TABLE></CENTER>\n<P>\n<HR>");
    }

    private void writeHTMLHeader(HTMLFile file, String title) {
        file.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        file.println("<HTML><HEAD><TITLE>" + title + "</TITLE>");
        file.println("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        file.println("</HEAD>");
    }

    /*
     * i is the number of the match j == 0 if subA is considered, otherwise (j must then be 1) it is subB This procedure
     * makes use of the column and length information!
     */
    private int writeImprovedSubmission(HTMLFile f, int i, JPlagComparison comparison, int j) throws ReportGenerationException {
        Submission sub = comparison.getSubmission(j == 0);
        String[] files = comparison.files(j);
        String[][] text = sub.readFiles(files);
        TokenList tokens = comparison.getSubmission(j == 0).getTokenList();

        // Markup list:
        Comparator<MarkupText> comp = (mo1, mo2) -> {
            int col1 = mo1.column;
            int col2 = mo2.column;
            if (col1 > col2) {
                return -1;
            } else if (col1 < col2) {
                return 1;
            }
            return (mo1.frontMarkup ? -1 : 1);
        };
        TreeMap<MarkupText, Object> markupList = new TreeMap<>(comp);

        for (int x = 0; x < comparison.getMatches().size(); x++) {
            Match onematch = comparison.getMatches().get(x);

            Token start = tokens.getToken(onematch.getStart(j == 0));
            Token end = tokens.getToken((onematch.getStart(j == 0)) + onematch.getLength() - 1);
            for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
                if (start.file.equals(files[fileIndex]) && text[fileIndex] != null) {
                    String tmp = "<FONT color=\"" + Color.getHexadecimalValue(x) + "\">" + (j == 1 ? "<div style=\"position:absolute;left:0\">" : "")
                            + "<A HREF=\"javascript:ZweiFrames('match" + i + "-" + (1 - j) + ".html#" + x + "'," + (3 - j) + ",'match" + i
                            + "-top.html#" + x + "',1)\"><IMG SRC=\"" + PICS[j] + "\" ALT=\"other\" " + "BORDER=\"0\" ALIGN=\""
                            + (j == 0 ? "right" : "left") + "\"></A>" + (j == 1 ? "</div>" : "") + "<B>";
                    // position the icon and the beginning of the colorblock
                    markupList.put(new MarkupText(fileIndex, start.getLine() - 1, start.getColumn() - 1, tmp, true), null);
                    // mark the end
                    markupList.put(new MarkupText(fileIndex, end.getLine() - 1, end.getColumn() + end.getLength() - 1, "</B></FONT>", false), null);

                    // the link location is placed 3 lines before the start of a block
                    int linkLine = (Math.max(start.getLine() - 4, 0));
                    markupList.put(new MarkupText(fileIndex, linkLine, 0, "<A NAME=\"" + x + "\"></A>", false), null);
                }
            }
        }

        if (getOptions().hasBaseCode() && comparison.getFirstBaseCodeMatches() != null && comparison.getSecondBaseCodeMatches() != null) {
            JPlagComparison baseCodeComparison = comparison.getBaseCodeMatches(j == 0);

            for (Match match : baseCodeComparison.getMatches()) {
                Token start = tokens.getToken(match.getStartOfFirst());
                Token end = tokens.getToken(match.getStartOfFirst() + match.getLength() - 1);

                for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
                    if (start.file.equals(files[fileIndex]) && text[fileIndex] != null) {
                        String tmp = "<font color=\"#C0C0C0\"><EM>";
                        // beginning of the colorblock
                        markupList.put(new MarkupText(fileIndex, start.getLine() - 1, start.getColumn() - 1, tmp, false), null);
                        // mark the end
                        markupList.put(new MarkupText(fileIndex, end.getLine() - 1, end.getColumn() + end.getLength() - 1, "</EM></font>", true),
                                null);
                    }
                }
            }
        }

        // Apply changes:
        for (MarkupText markup : markupList.keySet()) {
            // System.out.println(markup);
            String tmp = text[markup.fileIndex][markup.lineIndex];
            // is there any &quot;, &amp;, &gt; or &lt; in the String?
            if (tmp.indexOf('&') >= 0) {
                List<String> list = new ArrayList<>();
                // convert the string into a vector
                int strLength = tmp.length();
                for (int k = 0; k < strLength; k++) {
                    if (tmp.charAt(k) != '&') {
                        list.add(tmp.charAt(k) + "");
                    } else { // put &quot;, &amp;, &gt; and &lt; into one element
                        String tmpSub = tmp.substring(k);
                        if (tmpSub.startsWith("&quot;")) {
                            list.add("&quot;");
                            k = k + 5;
                        } else if (tmpSub.startsWith("&amp;")) {
                            list.add("&amp;");
                            k = k + 4;
                        } else if (tmpSub.startsWith("&lt;")) {
                            list.add("&lt;");
                            k = k + 3;
                        } else if (tmpSub.startsWith("&gt;")) {
                            list.add("&gt;");
                            k = k + 3;
                        } else {
                            list.add(tmp.charAt(k) + "");
                        }
                    }
                }
                if (markup.column <= list.size()) {
                    list.add(markup.column, markup.text);
                } else {
                    list.add(markup.text);
                }

                StringBuilder builder = new StringBuilder();
                // reconvert the Vector into a String
                for (String element : list) {
                    builder.append(element);
                }
                text[markup.fileIndex][markup.lineIndex] = builder.toString();
            } else {
                text[markup.fileIndex][markup.lineIndex] = tmp.substring(0, (Math.min(tmp.length(), markup.column))) + markup.text
                        + tmp.substring((Math.min(tmp.length(), markup.column)));
            }
        }

        f.println("<div style=\"flex-grow: 1;\">");

        for (int x = 0; x < text.length; x++) {
            f.println("<h3>");
            f.println("<center>");
            f.println("<span>" + sub.getName() + "</span>");
            f.println("<span> - </span>");
            f.println("<span>" + files[x] + "</span>");
            f.println("</center>");
            f.println("</h3>");
            f.println("<HR>");
            if (getLanguage().isPreformatted()) {
                f.println("<PRE>");
            }
            for (int y = 0; y < text[x].length; y++) {
                f.print(text[x][y]);
                if (!getLanguage().isPreformatted()) {
                    f.println("<BR>");
                } else {
                    f.println();
                }
            }
            if (getLanguage().isPreformatted()) {
                f.println("</PRE>");
            }
        }

        f.println("</div>");

        return f.bytesWritten();
    }

    /**
     * Write the index.html file.
     */
    private void writeIndex() throws ReportGenerationException {
        HTMLFile htmlFile = createHTMLFile("index.html");

        writeIndexBegin(htmlFile, msg.getString("Report.Search_Results"));
        writeDistribution(htmlFile);

        writeLinksToComparisons(htmlFile, "<H4>" + msg.getString("Report.MatchesAvg"), CSV_FILE);

        writeMatchesCSV(CSV_FILE);

        writeIndexEnd(htmlFile);

        htmlFile.close();
    }

    /**
     * Write the beginning of the index.html file.
     */
    private void writeIndexBegin(HTMLFile htmlFile, String title) {
        writeHTMLHeader(htmlFile, title);

        htmlFile.println("<BODY BGCOLOR=#ffffff LINK=#000088 VLINK=#000000 TEXT=#000000>");
        htmlFile.println("<TABLE ALIGN=center CELLPADDING=2 CELLSPACING=1>");
        htmlFile.println("<TR VALIGN=middle ALIGN=center BGCOLOR=#ffffff><TD>" + "<IMG SRC=\"logo.png\" ALT=\"JPlag\" BORDER=0></TD>");
        htmlFile.println("<TD><H1><BIG>" + title + "</BIG></H1></TD></TR>");

        htmlFile.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Language") + ":</TD><TD>"
                         + getLanguage().getName() + "</TD></TR>");
        htmlFile.print("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Submissions") + ":</TD><TD>" + result.getNumberOfSubmissions());

        htmlFile.println("</TD></TR>");

        if (getOptions().hasBaseCode()) {
            htmlFile.print("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Basecode_submission") + ":</TD>" + "<TD>"
                           + getOptions().getBaseCodeSubmissionName() + "</TD></TR>");
        }

        if (getOptions().getMaximumNumberOfComparisons() > 0) {
            htmlFile.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Matches_displayed") + ":</TD>" + "<TD>");
            htmlFile.println(getOptions().getMaximumNumberOfComparisons() + " of " + result.getComparisons().size() + " ("
                             + msg.getString("Report.Treshold") + ": " + getOptions().getSimilarityThreshold() + "%)<br>");

            htmlFile.println("</TD></TR>");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat();

        htmlFile.println(
                "<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Date") + ":</TD><TD>" + dateFormat.format(new Date()) + "</TD></TR>");
        htmlFile.println("<TR BGCOLOR=#aaaaff>" + "<TD><EM>" + msg.getString("Report.Minimum_Match_Length") + "</EM> ("
                         + msg.getString("Report.sensitivity") + "):</TD><TD>" + getOptions().getMinimumTokenMatch() + "</TD></TR>");
        htmlFile.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Suffixes") + ":</TD><TD>");

        String[] fileSuffixes = getOptions().getFileSuffixes();

        for (int i = 0; i < fileSuffixes.length; i++) {
            htmlFile.print(fileSuffixes[i] + (i < fileSuffixes.length - 1 ? ", " : "</TD></TR>\n"));
        }

        htmlFile.println("</TABLE>\n<HR>");
    }

    /*
     * i is the number of the match j == 0 if subA is considered, otherwise it is subB This procedure uses only the
     * getIndex() method of the token. It is meant to be used with the Character front end
     */
    private void writeIndexedSubmission(HTMLFile f, int i, JPlagComparison comparison, int j) throws ReportGenerationException {
        boolean useFirst = j == 0;
        Submission sub = comparison.getSubmission(useFirst);
        String[] files = comparison.files(j);
        char[][] text = sub.readFilesChar(files);
        TokenList tokens = comparison.getSubmission(useFirst).getTokenList();

        // get index array with matches sorted in ascending order.
        List<Integer> perm = comparison.sort_permutation(useFirst);

        int index = 0; // match index
        Match onematch = null;
        Token start = null;
        Token end = null;

        f.println("<div style=\"flex-grow: 1;\">");

        for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
            f.println("<h3>");
            f.println("<center>");
            f.println("<span>" + sub.getName() + "</span>");
            f.println("<span> - </span>");
            f.println("<span>" + files[fileIndex] + "</span>");
            f.println("</center>");
            f.println("</h3>");
            f.println("<HR>");
            char[] buffer = text[fileIndex];

            for (int charNr = 0; charNr < buffer.length; charNr++) {
                if (onematch == null) {
                    if (index < comparison.getMatches().size()) {
                        onematch = comparison.getMatches().get(perm.get(index));
                        start = tokens.getToken(onematch.getStart(j == 0));
                        end = tokens.getToken((onematch.getStart(j == 0)) + onematch.getLength() - 1);
                        index++;
                    } else {
                        start = end = null;
                    }
                }
                // begin markup
                if (start != null && start.getIndex() == charNr) {
                    f.print("<A NAME=\"" + perm.get(index - 1) + "\"></A>");
                    f.print("<FONT color=\"" + Color.getHexadecimalValue(perm.get(index - 1)) + "\"><B>");
                    // "<A HREF=\"javascript:ZweiFrames('match"+i+"-"+(1-j)+
                    // ".html#"+index+"',"+(3-j)+",'match"+i+"-top.html#"+index+
                    // "',1)\">"+"<IMG SRC=\""+pics[j]+
                    // "\" ALT=\"other\" BORDER=\"0\" "+"ALIGN="+
                    // (j==0 ? "right" : "left")+"></A>");
                }
                // text
                if (buffer[charNr] == '<') {
                    f.print("&lt;");
                } else if (buffer[charNr] == '>') {
                    f.print("&gt;");
                } else if (buffer[charNr] == '\n') {
                    f.print("<br>\n");
                } else {
                    f.print(buffer[charNr]);
                }
                // end markup
                if (end != null && end.getIndex() == charNr) {
                    f.print("</B></FONT>");
                    onematch = null; // switch to next match
                }
            }
        }

        f.println("</div>");
    }

    /**
     * Write the end of the index.html file.
     */
    private void writeIndexEnd(HTMLFile htmlFile) {
        htmlFile.println("<HR>\n<P ALIGN=right><FONT SIZE=\"1\" FACE=\"helvetica\">JPlag</FONT></P>");
        htmlFile.println("</BODY>\n</HTML>");
    }

    private void writeLinksToComparisons(HTMLFile htmlFile, String headerStr, String csvFile) {
        List<JPlagComparison> comparisons = result.getComparisons(getOptions().getMaximumNumberOfComparisons()); // should be already sorted!

        htmlFile.println(headerStr + " (<a href=\"help-sim-" + "en" // Country tag
                + ".html\"><small><font color=\"#000088\">" + msg.getString("Report.WhatIsThis") + "</font></small></a>):</H4>");
        htmlFile.println("<p><a href=\"" + csvFile + "\">download csv</a></p>");
        htmlFile.println("<TABLE CELLPADDING=3 CELLSPACING=2>");

        for (JPlagComparison comparison : comparisons) {
            String submissionNameA = comparison.getFirstSubmission().getName();
            String submissionNameB = comparison.getSecondSubmission().getName();

            htmlFile.print("<TR><TD BGCOLOR=" + color(comparison.similarityOfFirst(), 128, 192, 128, 192, 255, 255) + ">" + submissionNameA
                    + "</TD><TD><nobr>-&gt;</nobr>");

            htmlFile.print("</TD><TD BGCOLOR=" + color(comparison.similarityOfSecond(), 128, 192, 128, 192, 255, 255)
                    + " ALIGN=center><A HREF=\"match" + getComparisonIndex(comparison) + ".html\">" + submissionNameB + "</A><BR><FONT COLOR=\""
                    + color(comparison.similarity(), 0, 255, 0, 0, 0, 0) + "\">(" + (((int) (comparison.similarity() * 10)) / (float) 10)
                    + "%)</FONT>");

            htmlFile.println("</TD></TR>");
        }

        htmlFile.println("</TABLE><P>\n");
        htmlFile.println("<!---->");
    }

    private void writeMatch(JPlagComparison comparison, int i) throws ReportGenerationException {
        HTMLFile htmlFile = createHTMLFile("match" + i + ".html");

        writeHTMLHeader(htmlFile, TagParser.parse(msg.getString("Report.Matches_for_X1_AND_X2"),
                new String[] {comparison.getFirstSubmission().getName(), comparison.getSecondSubmission().getName()}));

        htmlFile.println("<body>");
        htmlFile.println("  <div style=\"align-items: center; display: flex; justify-content: space-around;\">");

        htmlFile.println("    <div>");
        htmlFile.println("      <h3 align=\"center\">");
        htmlFile.println(TagParser.parse(msg.getString("Report.Matches_for_X1_AND_X2"),
                new String[] {comparison.getFirstSubmission().getName(), comparison.getSecondSubmission().getName()}));
        htmlFile.println("      </h3>");
        htmlFile.println("      <h1 align=\"center\">");
        htmlFile.println("        " + comparison.roundedSimilarity() + "%");
        htmlFile.println("      </h1>");
        htmlFile.println("      <center>");
        htmlFile.println("        <a href=\"index.html\" target=\"_top\">");
        htmlFile.println("          " + msg.getString("Report.INDEX"));
        htmlFile.println("        </a>");
        htmlFile.println("        <span>-</span>");
        htmlFile.println("        <a href=\"help-en.html\" target=\"_top\">");
        htmlFile.println("          " + msg.getString("Report.HELP"));
        htmlFile.println("        </a>");
        htmlFile.println("      </center>");
        htmlFile.println("    </div>");

        htmlFile.println("    <div>");
        reportComparison(htmlFile, comparison, i);
        htmlFile.println("    </div>");

        htmlFile.println("  </div>");

        htmlFile.println("  <hr>");

        htmlFile.println("  <div style=\"display: flex;\">");

        if (getLanguage().usesIndex()) {
            writeIndexedSubmission(htmlFile, i, comparison, 0);
            writeIndexedSubmission(htmlFile, i, comparison, 1);
        } else if (getLanguage().supportsColumns()) {
            writeImprovedSubmission(htmlFile, i, comparison, 0);
            writeImprovedSubmission(htmlFile, i, comparison, 1);
        } else {
            writeNormalSubmission(htmlFile, i, comparison, 0);
            writeNormalSubmission(htmlFile, i, comparison, 1);
        }

        htmlFile.println("  </div>");

        htmlFile.println("</body>");
        htmlFile.println("</html>");
        htmlFile.close();
    }

    private void writeMatches(List<JPlagComparison> comparisons) {
        comparisons.parallelStream().forEach(comparison -> {
            try {
                writeMatch(comparison, getComparisonIndex(comparison));
            } catch (ExitException exception) {
                exception.printStackTrace();
            }
            reportMatchWritingProgress(comparisons);
        });
    }

    private void writeMatchesCSV(String fileName) {
        FileWriter writer = null;
        File csvFile = new File(reportDir, fileName);
        List<JPlagComparison> comparisons = result.getComparisons(getOptions().getMaximumNumberOfComparisons());

        try {
            csvFile.createNewFile();
            writer = new FileWriter(csvFile, JPlagOptions.CHARSET);

            for (JPlagComparison comparison : comparisons) {
                String submissionNameA = comparison.getFirstSubmission().getName();
                String submissionNameB = comparison.getSecondSubmission().getName();

                writer.write(getComparisonIndex(comparison) + ";");
                writer.write(submissionNameA + ";");
                writer.write(submissionNameB + ";");
                writer.write((((int) (comparison.similarity() * 10)) / (float) 10) + ";");
                writer.write("\n");
            }

            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception ignored) {
            }
        }
    }

    /*
     * i is the number of the match j == 0 if subA is considered, otherwise (j must then be 1) it is subB
     */
    private void writeNormalSubmission(HTMLFile f, int i, JPlagComparison comparison, int j) throws ReportGenerationException {
        Submission sub = comparison.getSubmission(j == 0);
        String[] files = comparison.files(j);

        String[][] text = sub.readFiles(files);

        TokenList tokens = comparison.getSubmission(j == 0).getTokenList();
        String hilf;
        int h;
        for (int x = 0; x < comparison.getMatches().size(); x++) {
            Match currentMatch = comparison.getMatches().get(x);

            Token start = tokens.getToken(currentMatch.getStart(j == 0));
            Token end = tokens.getToken((currentMatch.getStart(j == 0)) + currentMatch.getLength() - 1);

            for (int y = 0; y < files.length; y++) {
                if (start.file.equals(files[y]) && text[y] != null) {
                    hilf = "<FONT color=\"" + Color.getHexadecimalValue(x) + "\">" + (j == 1 ? "<div style=\"position:absolute;left:0\">" : "")
                            + "<A HREF=\"javascript:ZweiFrames('match" + i + "-" + (1 - j) + ".html#" + x + "'," + (3 - j) + ",'match" + i
                            + "-top.html#" + x + "',1)\"><IMG SRC=\"" + PICS[j] + "\" ALT=\"other\" " + "BORDER=\"0\" ALIGN=\""
                            + (j == 0 ? "right" : "left") + "\"></A>" + (j == 1 ? "</div>" : "") + "<B>";
                    // position the icon and the beginning of the colorblock
                    if (text[y][start.getLine() - 1].endsWith("</FONT>")) {
                        text[y][start.getLine() - 1] += hilf;
                    } else {
                        text[y][start.getLine() - 1] = hilf + text[y][start.getLine() - 1];
                    }
                    // the link location is placed 3 lines before the start of a block
                    h = (Math.max(start.getLine() - 4, 0));
                    text[y][h] = "<A NAME=\"" + x + "\"></A>" + text[y][h];
                    // mark the end
                    if (start.getLine() != end.getLine() && // if match is only one line
                            text[y][end.getLine() - 1].startsWith("<FONT ")) {
                        text[y][end.getLine() - 1] = "</B></FONT>" + text[y][end.getLine() - 1];
                    } else {
                        text[y][end.getLine() - 1] += "</B></FONT>";
                    }
                }
            }
        }

        if (getOptions().hasBaseCode() && comparison.getFirstBaseCodeMatches() != null && comparison.getSecondBaseCodeMatches() != null) {
            JPlagComparison baseCodeComparison = comparison.getBaseCodeMatches(j == 0);

            for (Match currentMatch : baseCodeComparison.getMatches()) {
                Token start = tokens.getToken(currentMatch.getStartOfFirst());
                Token end = tokens.getToken(currentMatch.getStartOfFirst() + currentMatch.getLength() - 1);

                for (int y = 0; y < files.length; y++) {
                    if (start.file.equals(files[y]) && text[y] != null) {
                        hilf = ("<font color=\"#C0C0C0\"><EM>");
                        // position the icon and the beginning of the colorblock
                        if (text[y][start.getLine() - 1].endsWith("<font color=\"#000000\">")) {
                            text[y][start.getLine() - 1] += hilf;
                        } else {
                            text[y][start.getLine() - 1] = hilf + text[y][start.getLine() - 1];
                        }

                        // mark the end
                        if (start.getLine() != end.getLine() && // match is only one line
                                text[y][end.getLine() - 1].startsWith("<font color=\"#C0C0C0\">")) {
                            text[y][end.getLine() - 1] = "</EM><font color=\"#000000\">" + text[y][end.getLine() - 1];
                        } else {
                            text[y][end.getLine() - 1] += "</EM><font color=\"#000000\">";
                        }
                    }
                }
            }
        }

        f.println("<div style=\"flex-grow: 1;\">");

        for (int x = 0; x < text.length; x++) {
            f.println("<h3>");
            f.println("<center>");
            f.println("<span>" + sub.getName() + "</span>");
            f.println("<span> - </span>");
            f.println("<span>" + files[x] + "</span>");
            f.println("</center>");
            f.println("</h3>");
            f.println("<HR>");
            if (getLanguage().isPreformatted()) {
                f.println("<PRE>");
            }
            for (int y = 0; y < text[x].length; y++) {
                f.print(text[x][y]);
                if (!getLanguage().isPreformatted()) {
                    f.println("<BR>");
                } else {
                    f.println();
                }
            }
            if (getLanguage().isPreformatted()) {
                f.println("</PRE>");
            }
        }

        f.println("</div>");
    }
}
