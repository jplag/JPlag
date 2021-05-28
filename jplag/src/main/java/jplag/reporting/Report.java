package jplag.reporting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import jplag.ExitException;
import jplag.JPlagBaseCodeComparison;
import jplag.JPlagComparison;
import jplag.JPlagResult;
import jplag.Match;
import jplag.Submission;
import jplag.Token;

/**
 * This class writes all the HTML pages
 */
public class Report {

    private JPlagResult result;
    private final File reportDir;
    private final Messages msg;

    int currentComparisonIndex = 0;
    Map<JPlagComparison, Integer> comparisonToIndex = new HashMap<>();

    public Report(File reportDir) throws ExitException {
        this.reportDir = reportDir;
        this.msg = new Messages("en");

        validateReportDir();
    }

    /**
     * Make sure the report directory exists, is a directory and has write access.
     */
    private void validateReportDir() throws ExitException {
        if (!reportDir.exists() && !reportDir.mkdirs()) {
            throw new ExitException("Cannot create report directory!");
        }

        if (!reportDir.isDirectory()) {
            throw new ExitException(reportDir + " is not a directory!");
        }

        if (!reportDir.canWrite()) {
            throw new ExitException("Cannot write directory: " + reportDir);
        }
    }

    /*
     * Two colors, represented by Rl,Gl,Bl and Rh,Gh,Bh respectively are mixed according to the percentage "percent"
     */
    private String color(float percent, int Rl, int Rh, int Gl, int Gh, int Bl, int Bh) {
        int farbeR = (int) (Rl + (Rh - Rl) * percent / 100);
        int farbeG = (int) (Gl + (Gh - Gl) * percent / 100);
        int farbeB = (int) (Bl + (Bh - Bl) * percent / 100);

        String helpR = (farbeR < 16 ? "0" : "") + Integer.toHexString(farbeR);
        String helpG = (farbeG < 16 ? "0" : "") + Integer.toHexString(farbeG);
        String helpB = (farbeB < 16 ? "0" : "") + Integer.toHexString(farbeB);

        return "#" + helpR + helpG + helpB;
    }

    /*
     * This procedure copies all the data from "data/" into the result-directory.
     */
    private void copyStaticFiles() {
        final String[] fileList = {"back.gif", "forward.gif", "help-en.html", "help-sim-en.html", "logo.gif", "fields.js"};

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

    private int getComparisonIndex(JPlagComparison comparison) {
        Integer index = comparisonToIndex.get(comparison);

        if (index != null) {
            return index;
        }

        comparisonToIndex.put(comparison, currentComparisonIndex++);

        return currentComparisonIndex - 1;
    }

    public void writeResult(JPlagResult result) throws ExitException {
        this.result = result;

        writeIndex();

        // TODO TS: Cluster currently not supported
        // if (result.getOptions().getClusterType() != ClusterType.NONE) {
        // writeClusters(clustering);
        // }

        copyStaticFiles();

        writeMatches(result.getComparisons());
    }

    /**
     * Create a new HTML file.
     */
    private HTMLFile createHTMLFile(String name) throws ExitException {
        File file = new File(reportDir, name);

        try {
            return HTMLFile.fromFile(file);
        } catch (IOException e) {
            throw new jplag.ExitException("Error opening file: " + file);
        }
    }

    private void writeHTMLHeader(HTMLFile file, String title) {
        file.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        file.println("<HTML><HEAD><TITLE>" + title + "</TITLE>");
        file.println("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        file.println("</HEAD>");
    }

    /**
     * Write the index.html file.
     */
    private void writeIndex() throws ExitException {
        HTMLFile htmlFile = createHTMLFile("index.html");

        writeIndexBegin(htmlFile, msg.getString("Report.Search_Results"));
        writeDistribution(htmlFile);

        String csvFile = "matches_avg.csv";

        writeLinksToComparisons(htmlFile, "<H4>" + msg.getString("Report.MatchesAvg"), csvFile);

        writeMatchesCSV(csvFile);

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
        htmlFile.println("<TR VALIGN=middle ALIGN=center BGCOLOR=#ffffff><TD>" + "<IMG SRC=\"logo.gif\" ALT=\"JPlag\" BORDER=0></TD>");
        htmlFile.println("<TD><H1><BIG>" + title + "</BIG></H1></TD></TR>");

        htmlFile.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Language") + ":</TD><TD>"
                + result.getOptions().getLanguageOption().name() + "</TD></TR>");
        htmlFile.print("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Submissions") + ":</TD><TD>" + result.getNumberOfSubmissions());

        htmlFile.println("</TD></TR>");

        if (result.getOptions().hasBaseCode()) {
            htmlFile.print("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Basecode_submission") + ":</TD>" + "<TD>"
                    + result.getOptions().getBaseCodeSubmissionName() + "</TD></TR>");
        }

        if (result.getComparisons().size() > 0) {
            htmlFile.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Matches_displayed") + ":</TD>" + "<TD>");

            htmlFile.println(result.getComparisons().size() + " (" + msg.getString("Report.Treshold") + ": "
                    + result.getOptions().getSimilarityThreshold() + "%)<br>");

            htmlFile.println("</TD></TR>");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat();

        htmlFile.println(
                "<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Date") + ":</TD><TD>" + dateFormat.format(new Date()) + "</TD></TR>");
        htmlFile.println("<TR BGCOLOR=#aaaaff>" + "<TD><EM>" + msg.getString("Report.Minimum_Match_Length") + "</EM> ("
                + msg.getString("Report.sensitivity") + "):</TD><TD>" + result.getOptions().getMinTokenMatch() + "</TD></TR>");
        htmlFile.println("<TR BGCOLOR=#aaaaff VALIGN=top><TD>" + msg.getString("Report.Suffixes") + ":</TD><TD>");

        String[] fileSuffixes = result.getOptions().getFileSuffixes();

        for (int i = 0; i < fileSuffixes.length; i++) {
            htmlFile.print(fileSuffixes[i] + (i < fileSuffixes.length - 1 ? ", " : "</TD></TR>\n"));
        }

        htmlFile.println("</TABLE>\n<HR>");
    }

    /**
     * Write the end of the index.html file.
     */
    private void writeIndexEnd(HTMLFile htmlFile) {
        htmlFile.println("<HR>\n<P ALIGN=right><FONT SIZE=\"1\" FACE=\"helvetica\">JPlag</FONT></P>");
        htmlFile.println("</BODY>\n</HTML>");
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

    private void writeLinksToComparisons(HTMLFile htmlFile, String headerStr, String csvFile) {
        List<JPlagComparison> comparisons = result.getComparisons();

        htmlFile.println(headerStr + " (<a href=\"help-sim-" + "en" // Country tag
                + ".html\"><small><font color=\"#000088\">" + msg.getString("Report.WhatIsThis") + "</font></small></a>):</H4>");
        htmlFile.println("<p><a href=\"" + csvFile + "\">download csv</a></p>");
        htmlFile.println("<TABLE CELLPADDING=3 CELLSPACING=2>");

        for (JPlagComparison comparison : comparisons) {
            String submissionNameA = comparison.firstSubmission.name;
            String submissionNameB = comparison.secondSubmission.name;

            htmlFile.print("<TR><TD BGCOLOR=" + color(comparison.percentA(), 128, 192, 128, 192, 255, 255) + ">" + submissionNameA
                    + "</TD><TD><nobr>-&gt;</nobr>");

            htmlFile.print("</TD><TD BGCOLOR=" + color(comparison.percentB(), 128, 192, 128, 192, 255, 255) + " ALIGN=center><A HREF=\"match"
                    + getComparisonIndex(comparison) + ".html\">" + submissionNameB + "</A><BR><FONT COLOR=\""
                    + color(comparison.percent(), 0, 255, 0, 0, 0, 0) + "\">(" + (((int) (comparison.percent() * 10)) / (float) 10) + "%)</FONT>");

            htmlFile.println("</TD></TR>");
        }

        htmlFile.println("</TABLE><P>\n");
        htmlFile.println("<!---->");
    }

    private void writeMatchesCSV(String fileName) {
        FileWriter writer = null;
        File csvFile = new File(reportDir, fileName);
        List<JPlagComparison> comparisons = result.getComparisons();

        try {
            csvFile.createNewFile();
            writer = new FileWriter(csvFile);

            for (JPlagComparison comparison : comparisons) {
                String submissionNameA = comparison.firstSubmission.name;
                String submissionNameB = comparison.secondSubmission.name;

                writer.write(getComparisonIndex(comparison) + ";");
                writer.write(submissionNameA + ";");
                writer.write(submissionNameB + ";");
                writer.write((((int) (comparison.percent() * 10)) / (float) 10) + ";");
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

    private void writeMatches(List<JPlagComparison> comparisons) {
        comparisons.forEach(comparison -> {
            try {
                int i = getComparisonIndex(comparison);
                writeMatch(comparison, i);
            } catch (ExitException e) {
                e.printStackTrace();
            }
        });
    }

    private void writeMatch(JPlagComparison comparison, int i) throws ExitException {
        HTMLFile htmlFile = createHTMLFile("match" + i + ".html");

        writeHTMLHeader(htmlFile, TagParser.parse(msg.getString("Report.Matches_for_X1_AND_X2"),
                new String[] {comparison.firstSubmission.name, comparison.secondSubmission.name}));

        htmlFile.println("<body>");
        htmlFile.println("  <div style=\"align-items: center; display: flex; justify-content: space-around;\">");

        htmlFile.println("    <div>");
        htmlFile.println("      <h3 align=\"center\">");
        htmlFile.println(TagParser.parse(msg.getString("Report.Matches_for_X1_AND_X2"),
                new String[] {comparison.firstSubmission.name, comparison.secondSubmission.name}));
        htmlFile.println("      </h3>");
        htmlFile.println("      <h1 align=\"center\">");
        htmlFile.println("        " + comparison.roundedPercent() + "%");
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

        if (result.getOptions().getLanguage().usesIndex()) {
            writeIndexedSubmission(htmlFile, i, comparison, 0);
            writeIndexedSubmission(htmlFile, i, comparison, 1);
        } else if (result.getOptions().getLanguage().supportsColumns()) {
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

    /**
     * This method generates an table entry in the list of all comparisons.
     */
    private void reportComparison(HTMLFile htmlFile, JPlagComparison comparison, int index) {
        Match match;
        Token[] tokensA = comparison.firstSubmission.tokenList.tokens;
        Token[] tokensB = comparison.secondSubmission.tokenList.tokens;
        // sort();

        htmlFile.println("<TABLE BORDER=\"1\" CELLSPACING=\"0\" BGCOLOR=\"#d0d0d0\">");
        htmlFile.println("<TR><TH><TH>" + comparison.firstSubmission.name + " (" + comparison.percentA() + "%)<TH>" + comparison.secondSubmission.name
                + " (" + comparison.percentB() + "%)<TH>" + msg.getString("AllMatches.Tokens"));

        for (int i = 0; i < comparison.matches.size(); i++) {
            match = comparison.matches.get(i);

            Token startA = tokensA[match.startA];
            Token endA = tokensA[match.startA + match.length - 1];
            Token startB = tokensB[match.startB];
            Token endB = tokensB[match.startB + match.length - 1];

            String col = Colors.getColor(i);

            htmlFile.print("<TR><TD BGCOLOR=\"" + col + "\"><FONT COLOR=\"" + col + "\">-</FONT>");
            htmlFile.print("<TD><A HREF=\"javascript:ZweiFrames('match" + index + "-0.html#" + i + "',2,'match" + index + "-1.html#" + i
                    + "',3)\" NAME=\"" + i + "\">");
            htmlFile.print(new String(startA.file.getBytes()));

            if (result.getOptions().getLanguage().usesIndex()) {
                htmlFile.print("(" + startA.getIndex() + "-" + endA.getIndex() + ")");
            } else {
                htmlFile.print("(" + startA.getLine() + "-" + endA.getLine() + ")");
            }

            htmlFile.print("<TD><A HREF=\"javascript:ZweiFrames('match" + index + "-0.html#" + i + "',2,'match" + index + "-1.html#" + i
                    + "',3)\" NAME=\"" + i + "\">");
            htmlFile.print(startB.file);

            if (result.getOptions().getLanguage().usesIndex()) {
                htmlFile.print("(" + startB.getIndex() + "-" + endB.getIndex());
            } else {
                htmlFile.print("(" + startB.getLine() + "-" + endB.getLine());
            }

            htmlFile.println(")</A><TD ALIGN=center>" + "<FONT COLOR=\"" + comparison.color(match.length) + "\">" + match.length + "</FONT>");
        }

        if (result.getOptions().hasBaseCode()) {
            htmlFile.print(
                    "<TR><TD BGCOLOR=\"#C0C0C0\"><TD>" + msg.getString("AllMatches.Basecode") + " " + comparison.roundedPercentBasecodeA() + "%");
            htmlFile.println("<TD>" + msg.getString("AllMatches.Basecode") + " " + comparison.roundedPercentBasecodeB() + "%<TD>&nbsp;");
        }

        htmlFile.println("</TABLE>");
    }

    // --------------------------------------------------------------------------
    // ==========================================================================
    // --------------------------------------------------------------------------

    // TODO:
    // /* this function copies all submissions into the result directory */
    // private int copySubmissions() throws jplag.ExitException {
    // int bytes = 0;
    // for (Iterator<Submission> i = program.clusters.neededSubmissions.iterator(); i.hasNext(); ) {
    // Submission sub = i.next();
    // int index = this.program.clusters.submissions.indexOf(sub);
    //
    // HTMLFile f = createHTMLFile(reportDir, "submission" + index + ".html");
    // writeHTMLHeader(f, sub.name);
    // f.println("<BODY BGCOLOR=\"#ffffff\">");
    //
    // String[] files = sub.files;
    // String text[][] = sub.readFiles(files);
    //
    // for (int j = 0; j < files.length; j++) {
    // f.println("<HR>\n<H3><CENTER>" + files[j] + "</CENTER></H3><HR>");
    // if (this.language.isPreformated()) {
    // f.println("<PRE>");
    // }
    // for (int k = 0; k < text[j].length; k++) {
    // f.print(text[j][k]);
    // if (!this.language.isPreformated()) {
    // f.println("<BR>");
    // } else {
    // f.println();
    // }
    // }
    // if (language.isPreformated()) {
    // f.println("</PRE>");
    // }
    // }
    //
    // f.println("</BODY>\n</HTML>");
    // f.close();
    // bytes += f.bytesWritten();
    // }
    // return bytes;
    // }

    // TODO TS: Cluster currently not supported
    // private int writeClusters(Cluster clustering) throws jplag.ExitException {
    // int bytes = 0;
    //
    // HTMLFile f = createHTMLFile("cluster.html");
    // writeHTMLHeader(f, msg.getString("Report.Clustering_Results"));
    // String clustertype = msg.getString("Report.Type") + ": " + program.clusters.getType();
    // f.println("<BODY>\n<H2>" + msg.getString("Report.Clustering_Results") + " (" + clustertype
    // + ")</H2>");
    // f.println("<H3><A HREF=\"dendro.html\">" + msg.getString("Report.Dendrogram") + "</A></H3>");
    // bytes += this.program.clusters.makeDendrograms(reportDir, clustering);
    //
    // if (this.program.get_threshold() != null) {
    // for (int i = 0; i < this.program.get_threshold().length; i++) {
    // float threshold = this.program.get_threshold()[i];
    // String clustertitle = TagParser
    // .parse(msg.getString("Report.Clusters_for_Xpercent_treshold"),
    // new String[]{threshold + ""});
    // f.println("<H3><A HREF=\"cluster" + threshold + ".html\">" + clustertitle + "</A></H3>");
    // HTMLFile f2 = createHTMLFile(reportDir, "cluster" + threshold + ".html");
    // writeHTMLHeader(f2, clustertitle);
    // f2.println("<BODY>\n<H2>" + clustertitle + " (" + clustertype + ")</H2>");
    // String text = program.clusters.printClusters(clustering, threshold, f2);
    // f2.println("</BODY>\n</HTML>");
    // f2.close();
    // bytes += f2.bytesWritten();
    // f.print(text);
    // }
    // } else {
    // float increase = this.program.clusters.maxMergeValue / 10;
    // if (increase < 5) {
    // increase = 5;
    // }
    // for (float threshold = increase; threshold <= program.clusters.maxMergeValue;
    // threshold += increase) {
    // String clustertitle = TagParser
    // .parse(msg.getString("Report.Clusters_for_Xpercent_treshold"),
    // new String[]{threshold + ""});
    // f.println(
    // "<H3><A HREF=\"cluster" + (int) threshold + ".html\">" + clustertitle + "</A></H3>");
    // HTMLFile f2 = createHTMLFile(reportDir, "cluster" + (int) threshold + ".html");
    // writeHTMLHeader(f2, clustertitle);
    // f2.println("<BODY>\n<H2>" + clustertitle + " (" + clustertype + ")</H2>");
    // String text = program.clusters.printClusters(clustering, (int) threshold, f2);
    // f2.println("</BODY>\n</HTML>");
    // f2.close();
    // bytes += f2.bytesWritten();
    // f.print(text);
    // }
    // }
    //
    // f.println("</BODY>\n</HTML>");
    // f.close();
    //
    // bytes += copySubmissions();
    // return f.bytesWritten() + bytes;
    // }

    // SUBMISSION - here it comes...
    private final String[] pics = {"forward.gif", "back.gif"};

    /*
     * i is the number of the match j == 0 if subA is considered, otherwise (j must then be 1) it is subB
     */
    private void writeNormalSubmission(HTMLFile f, int i, JPlagComparison comparison, int j) throws ExitException {
        Submission sub = (j == 0 ? comparison.firstSubmission : comparison.secondSubmission);
        String[] files = comparison.files(j);

        String[][] text = sub.readFiles(files);

        Token[] tokens = (j == 0 ? comparison.firstSubmission : comparison.secondSubmission).tokenList.tokens;
        Match currentMatch;
        String hilf;
        int h;
        for (int x = 0; x < comparison.matches.size(); x++) {
            currentMatch = comparison.matches.get(x);

            Token start = tokens[(j == 0 ? currentMatch.startA : currentMatch.startB)];
            Token ende = tokens[((j == 0 ? currentMatch.startA : currentMatch.startB) + currentMatch.length - 1)];

            for (int y = 0; y < files.length; y++) {
                if (start.file.equals(files[y]) && text[y] != null) {
                    hilf = "<FONT color=\"" + Colors.getColor(x) + "\">" + (j == 1 ? "<div style=\"position:absolute;left:0\">" : "")
                            + "<A HREF=\"javascript:ZweiFrames('match" + i + "-" + (1 - j) + ".html#" + x + "'," + (3 - j) + ",'match" + i
                            + "-top.html#" + x + "',1)\"><IMG SRC=\"" + pics[j] + "\" ALT=\"other\" " + "BORDER=\"0\" ALIGN=\""
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
                    if (start.getLine() != ende.getLine() && // if match is only one line
                            text[y][ende.getLine() - 1].startsWith("<FONT ")) {
                        text[y][ende.getLine() - 1] = "</B></FONT>" + text[y][ende.getLine() - 1];
                    } else {
                        text[y][ende.getLine() - 1] += "</B></FONT>";
                    }
                }
            }
        }

        if (result.getOptions().hasBaseCode() && comparison.bcMatchesA != null && comparison.bcMatchesB != null) {
            JPlagBaseCodeComparison baseCodeComparison = (j == 0 ? comparison.bcMatchesA : comparison.bcMatchesB);

            for (int x = 0; x < baseCodeComparison.matches.size(); x++) {
                currentMatch = baseCodeComparison.matches.get(x);
                Token start = tokens[currentMatch.startA];
                Token ende = tokens[currentMatch.startA + currentMatch.length - 1];

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
                        if (start.getLine() != ende.getLine() && // match is only one line
                                text[y][ende.getLine() - 1].startsWith("<font color=\"#C0C0C0\">")) {
                            text[y][ende.getLine() - 1] = "</EM><font color=\"#000000\">" + text[y][ende.getLine() - 1];
                        } else {
                            text[y][ende.getLine() - 1] += "</EM><font color=\"#000000\">";
                        }
                    }
                }
            }
        }

        f.println("<div style=\"flex-grow: 1;\">");

        for (int x = 0; x < text.length; x++) {
            f.println("<h3>");
            f.println("<center>");
            f.println("<span>" + sub.name + "</span>");
            f.println("<span> - </span>");
            f.println("<span>" + files[x] + "</span>");
            f.println("</center>");
            f.println("</h3>");
            f.println("<HR>");
            if (result.getOptions().getLanguage().isPreformatted()) {
                f.println("<PRE>");
            }
            for (int y = 0; y < text[x].length; y++) {
                f.print(text[x][y]);
                if (!result.getOptions().getLanguage().isPreformatted()) {
                    f.println("<BR>");
                } else {
                    f.println();
                }
            }
            if (result.getOptions().getLanguage().isPreformatted()) {
                f.println("</PRE>");
            }
        }

        f.println("</div>");
    }

    /*
     * i is the number of the match j == 0 if subA is considered, otherwise it is subB This procedure uses only the
     * getIndex() method of the token. It is meant to be used with the Character front end
     */
    private void writeIndexedSubmission(HTMLFile f, int i, JPlagComparison comparison, int j) throws ExitException {
        Submission sub = (j == 0 ? comparison.firstSubmission : comparison.secondSubmission);
        String[] files = comparison.files(j);
        char[][] text = sub.readFilesChar(files);
        Token[] tokens = (j == 0 ? comparison.firstSubmission : comparison.secondSubmission).tokenList.tokens;

        // get index array with matches sorted in ascending order.
        int[] perm = comparison.sort_permutation(j);

        int index = 0; // match index
        Match onematch = null;
        Token start = null;
        Token end = null;

        f.println("<div style=\"flex-grow: 1;\">");

        for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
            f.println("<h3>");
            f.println("<center>");
            f.println("<span>" + sub.name + "</span>");
            f.println("<span> - </span>");
            f.println("<span>" + files[fileIndex] + "</span>");
            f.println("</center>");
            f.println("</h3>");
            f.println("<HR>");
            char[] buffer = text[fileIndex];

            for (int charNr = 0; charNr < buffer.length; charNr++) {
                if (onematch == null) {
                    if (index < comparison.matches.size()) {
                        onematch = comparison.matches.get(perm[index]);
                        start = tokens[(j == 0 ? onematch.startA : onematch.startB)];
                        end = tokens[((j == 0 ? onematch.startA : onematch.startB) + onematch.length - 1)];
                        index++;
                    } else {
                        start = end = null;
                    }
                }
                // begin markup
                if (start != null && start.getIndex() == charNr) {
                    f.print("<A NAME=\"" + perm[index - 1] + "\"></A>");
                    f.print("<FONT color=\"" + Colors.getColor(perm[index - 1]) + "\"><B>");
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

    /*
     * i is the number of the match j == 0 if subA is considered, otherwise (j must then be 1) it is subB This procedure
     * makes use of the column and length information!
     */
    private int writeImprovedSubmission(HTMLFile f, int i, JPlagComparison comparison, int j) throws jplag.ExitException {
        Submission sub = (j == 0 ? comparison.firstSubmission : comparison.secondSubmission);
        String[] files = comparison.files(j);
        String[][] text = sub.readFiles(files);
        Token[] tokens = (j == 0 ? comparison.firstSubmission : comparison.secondSubmission).tokenList.tokens;

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

        for (int x = 0; x < comparison.matches.size(); x++) {
            Match onematch = comparison.matches.get(x);

            Token start = tokens[(j == 0 ? onematch.startA : onematch.startB)];
            Token end = tokens[((j == 0 ? onematch.startA : onematch.startB) + onematch.length - 1)];
            for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
                if (start.file.equals(files[fileIndex]) && text[fileIndex] != null) {
                    String tmp = "<FONT color=\"" + Colors.getColor(x) + "\">" + (j == 1 ? "<div style=\"position:absolute;left:0\">" : "")
                            + "<A HREF=\"javascript:ZweiFrames('match" + i + "-" + (1 - j) + ".html#" + x + "'," + (3 - j) + ",'match" + i
                            + "-top.html#" + x + "',1)\"><IMG SRC=\"" + pics[j] + "\" ALT=\"other\" " + "BORDER=\"0\" ALIGN=\""
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

        if (result.getOptions().hasBaseCode() && comparison.bcMatchesA != null && comparison.bcMatchesB != null) {
            JPlagBaseCodeComparison baseCodeComparison = (j == 0 ? comparison.bcMatchesA : comparison.bcMatchesB);

            for (int x = 0; x < baseCodeComparison.matches.size(); x++) {
                Match onematch = baseCodeComparison.matches.get(x);
                Token start = tokens[onematch.startA];
                Token end = tokens[onematch.startA + onematch.length - 1];

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
                Vector<String> tmpV = new Vector<>();
                // convert the string into a vector
                int strLength = tmp.length();
                for (int k = 0; k < strLength; k++) {
                    if (tmp.charAt(k) != '&') {
                        tmpV.addElement(tmp.charAt(k) + "");
                    } else { // put &quot;, &amp;, &gt; and &lt; into one element
                        String tmpSub = tmp.substring(k);
                        if (tmpSub.startsWith("&quot;")) {
                            tmpV.addElement("&quot;");
                            k = k + 5;
                        } else if (tmpSub.startsWith("&amp;")) {
                            tmpV.addElement("&amp;");
                            k = k + 4;
                        } else if (tmpSub.startsWith("&lt;")) {
                            tmpV.addElement("&lt;");
                            k = k + 3;
                        } else if (tmpSub.startsWith("&gt;")) {
                            tmpV.addElement("&gt;");
                            k = k + 3;
                        } else {
                            tmpV.addElement(tmp.charAt(k) + "");
                        }
                    }
                }
                if (markup.column <= tmpV.size()) {
                    tmpV.insertElementAt(markup.text, markup.column);
                } else {
                    tmpV.addElement(markup.text);
                }

                StringBuilder tmpVStr = new StringBuilder();
                // reconvert the Vector into a String
                for (int k = 0; k < tmpV.size(); k++) {
                    tmpVStr.append(tmpV.elementAt(k));
                }
                text[markup.fileIndex][markup.lineIndex] = tmpVStr.toString();
            } else {
                text[markup.fileIndex][markup.lineIndex] = tmp.substring(0, (Math.min(tmp.length(), markup.column))) + markup.text
                        + tmp.substring((Math.min(tmp.length(), markup.column)));
            }
        }

        f.println("<div style=\"flex-grow: 1;\">");

        for (int x = 0; x < text.length; x++) {
            f.println("<h3>");
            f.println("<center>");
            f.println("<span>" + sub.name + "</span>");
            f.println("<span> - </span>");
            f.println("<span>" + files[x] + "</span>");
            f.println("</center>");
            f.println("</h3>");
            f.println("<HR>");
            if (result.getOptions().getLanguage().isPreformatted()) {
                f.println("<PRE>");
            }
            for (int y = 0; y < text[x].length; y++) {
                f.print(text[x][y]);
                if (!result.getOptions().getLanguage().isPreformatted()) {
                    f.println("<BR>");
                } else {
                    f.println();
                }
            }
            if (result.getOptions().getLanguage().isPreformatted()) {
                f.println("</PRE>");
            }
        }

        f.println("</div>");

        return f.bytesWritten();
    }
}
