package jplag.clustering;

import jplag.*;
import jplag.options.Options;
import jplag.options.util.Messages;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

/**
 * This class calculates, based on the similarity matrix, the hierarchical
 * clustering of the documents, using MIN, MAX and AVR methods.
 */
public class Clusters {
	public Vector<Submission> submissions;
	public HashSet<Submission> neededSubmissions = new HashSet<Submission>();
	public float maxMergeValue = 0;
	private Program program;
	private Messages msg;
	
	public Clusters(Program program){
		this.program=program;
		this.msg=program.msg;
	}

	public Cluster calculateClustering(Vector<Submission> submissions) {
		this.submissions = submissions;
		Cluster clusters = null;
		
		switch (this.program.get_clusterType()) {
			case Options.MIN_CLUSTER:
			case Options.MAX_CLUSTER:
			case Options.AVR_CLUSTER:
				clusters = minMaxAvrClustering();
				break;
			default:
		}
		
		return clusters;
	}

	public String getType() {
		switch (this.program.get_clusterType()) {
			case Options.MIN_CLUSTER:
				return msg.getString("Clusters.MIN_single_link");
			case Options.MAX_CLUSTER:
				return msg.getString("Clusters.MAX_complete_link");
			case Options.AVR_CLUSTER:
				return msg.getString("Clusters.AVR_group_average");
			default:
				return msg.getString("Clusters.unknown");
		}
	}

	/** Min clustering... */
	public Cluster minMaxAvrClustering() {
		int nrOfSubmissions = submissions.size();
		boolean minClustering = (Options.MIN_CLUSTER == this.program.get_clusterType());
		boolean maxClustering = (Options.MAX_CLUSTER == this.program.get_clusterType());
        SimilarityMatrix simMatrix = this.program.get_similarity();
		
		ArrayList<Cluster> clusters = new ArrayList<Cluster>(submissions.size());
		for (int i=0; i<nrOfSubmissions; i++)
			clusters.add(new Cluster(i,this));
		
		while (clusters.size() > 1) {
			int indexA=-1, indexB=-1;
			float maxSim = -1;
			int nrOfClusters = clusters.size();
			
			// find similarity
			for (int a=0; a<(nrOfClusters-1); a++) {
				Cluster cluster = clusters.get(a);
				for (int b=a+1; b<nrOfClusters; b++) {
					float sim;
					if (minClustering)
						sim = cluster.maxSimilarity(clusters.get(b), simMatrix);
					else if (maxClustering)
						sim = cluster.minSimilarity(clusters.get(b), simMatrix);
					else
						sim = cluster.avrSimilarity(clusters.get(b), simMatrix);
					if (sim > maxSim) {
						maxSim = sim;
						indexA = a;
						indexB = b;
					}
				}
			}
			
			if (maxSim > maxMergeValue)
				maxMergeValue = maxSim;
			
			// now merge these clusters
			Cluster clusterA = clusters.get(indexA);
			Cluster clusterB = clusters.get(indexB);
			clusters.remove(clusterA);
			clusters.remove(clusterB);
			clusters.add(new Cluster(clusterA, clusterB, maxSim,this));
		}
		return clusters.get(0);
	}

	private ArrayList<Cluster> getClusters(Cluster clustering, float threshold) {
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		
		// First determine the clusters
		Stack<Cluster> stack = new Stack<Cluster>();
		stack.push(clustering);
		while (!stack.empty()) {
			Cluster current = stack.pop();
			
			if (current.size() == 1) {
				clusters.add(current);  // singleton clusters
			} else {
				if (current.getSimilarity() >= threshold) {
					clusters.add(current);
				} else {
					// current.size() != 1   !!!
					stack.push(current.getLeft());
					stack.push(current.getRight());
				}
			}
		}
		return clusters;
	}
  
	/** Print it! */
	public  String printClusters(Cluster clustering, float threshold,
			HTMLFile f) {
		int maxSize = 0;
		
		ArrayList<Cluster> clusters = getClusters(clustering, threshold);
		
		for (Iterator<Cluster> i=clusters.iterator(); i.hasNext(); ) {
			Cluster cluster = i.next();
			if (cluster.size() > maxSize)
				maxSize = cluster.size();
		}
		
		TreeSet<Cluster> sorted = new TreeSet<Cluster>(clusters);
		clusters = null;
		
		// Now print them:
		return outputClustering(f, sorted, maxSize);
	}
	
	private final int barLength = 70;
	/** This method returns the distribution HTML code as a string */
	private String outputClustering(HTMLFile f, Collection<Cluster> allClusters,
			int maxSize) {
		int[] distribution = new int[maxSize+1];
		int max = 0;
		for (int i=0; i<=maxSize; i++)
			distribution[i] = 0;
		
		// Now output the clustering:
		f.println("<TABLE CELLPADDING=2 CELLSPACING=2>");
		
		f.println("<TR><TH ALIGN=center BGCOLOR=#8080ff>"
			+ msg.getString("Clusters.Cluster_number")
			+ "<TH ALIGN=center BGCOLOR=#8080ff>"
			+ msg.getString("Clusters.Size")
			+ "<TH ALIGN=center BGCOLOR=#8080ff>"
			+ msg.getString("Clusters.Threshold")
			+ "<TH ALIGN=center BGCOLOR=#8080ff>"
			+ msg.getString("Clusters.Cluster_members")
			+ "<TH ALIGN=center BGCOLOR=#8080ff>"
			+ msg.getString("Clusters.Most_frequent_words") + "</TR>");
		Iterator<Cluster> clusterI = allClusters.iterator();
		for (int i=1; clusterI.hasNext(); i++) {
			Cluster cluster = clusterI.next();
			if (max < ++distribution[cluster.size()])
				max = distribution[cluster.size()];
			
			// no singleton clusters
			if (cluster.size() == 1)
				continue;
			
			f.print("<TR><TD ALIGN=center BGCOLOR=#8080ff>" + i
				+ "<TD ALIGN=center BGCOLOR=#c0c0ff>" + cluster.size()
				+ "<TD ALIGN=center BGCOLOR=#c0c0ff>" + cluster.getSimilarity()
				+ "<TD ALIGN=left BGCOLOR=#c0c0ff>");
			
			// sort names
			TreeSet<Submission> sortedSubmissions = new TreeSet<Submission>();
			for (int x=0; x<cluster.size(); x++) {
				sortedSubmissions.add(
					submissions.elementAt(cluster.getSubmissionAt(x)));
			}
			
			for (Iterator<Submission> iter=sortedSubmissions.iterator(); iter.hasNext();) {
				Submission sub = iter.next();
				int index = submissions.indexOf(sub);
				f.print("<A HREF=\"submission"+index+".html\">"+sub.name+"</A>");
				if (iter.hasNext())
					f.print(", ");
				neededSubmissions.add(sub); // write files for these.
			}
			
			if (this.program.get_language() instanceof jplag.text.Language) {
				f.println("<TD ALIGN=left BGCOLOR=#c0c0ff>" +
					ThemeGenerator.generateThemes(sortedSubmissions,
						this.program.get_themewords(),
						true,this.program));
			} else {
				f.println("<TD ALIGN=left BGCOLOR=#c0c0ff>-");
			}
			
			f.println("</TR>");
		}
		f.println("</TABLE>\n<P>\n");
		
		f.println("<H5>" + msg.getString("Clusters.Distribution_of_cluster_size")
			+ ":</H5>");
		
		String text;
		text = "<TABLE CELLPADDING=1 CELLSPACING=1>\n";
		text += "<TR><TH ALIGN=center BGCOLOR=#8080ff>"
			+ msg.getString("Clusters.Cluster_size")
			+ "<TH ALIGN=center BGCOLOR=#8080ff>"
			+ msg.getString("Clusters.Number_of_clusters")
			+ "<TH ALIGN=center BGCOLOR=#8080ff>.</TR>\n";
		for (int i=0; i<=maxSize; i++) {
			if (distribution[i] == 0) continue;
			text+= "<TR><TD ALIGN=center BGCOLOR=#c0c0ff>" + i
				+ "<TD ALIGN=right BGCOLOR=#c0c0ff>" + distribution[i]
				+ "<TD BGCOLOR=#c0c0ff>\n";
			for (int j=(distribution[i] * barLength / max); j>0; j--) text += ("#");
			if (distribution[i] * barLength / max == 0) {
				if (distribution[i]==0)
					text += (".");
				else
					text += ("#");
			}
			text += ("</TR>\n");
		}
		text += ("</TABLE>\n");
		
		f.print(text);
		return text;
	}

	/* Dendrograms... */
	public int makeDendrograms(File root, Cluster clustering)
				throws jplag.ExitException {
		HTMLFile f = this.program.report.openHTMLFile(root, "dendro.html");
		f.println("<!DOCTYPE HTML PUBLIC \"-//DTD HTML 3.2//EN\">");
		f.println("<HTML>\n<HEAD>\n<TITLE>"
			+ msg.getString("Clusters.Dendrogram") + "</TITLE>\n"
			+ "<script language=\"JavaScript\" type=\"text/javascript\" "
			+ "src=\"fields.js\">\n</script>\n</HEAD>\n<BODY>");
		f.println("<H1>" + msg.getString("Clusters.Dendrogram") + "</H1>");
		
		f.println("<form name=\"data\" action=\"\">");
		f.println("<table border=\"0\">");
		f.println("<tr><td>" + msg.getString("Clusters.Cluster_size") + ":</td>"
			+ "<td><input type=\"text\" readonly name=\"size\" size=\"5\"></td>");
		f.println("<td rowspan=\"3\">" + msg.getString("Clusters.Themewords")
			+ ":</td><td rowspan=\"3\"><textarea cols=\"80\" rows=\"3\" readonly "
			+ "name=\"theme\"></textarea></td></tr>");
		f.println("<tr><td>" + msg.getString("Clusters.Threshold")
			+ ":</td><td><input type=\"text\" readonly name=\"thresh\" "
			+ "size=\"6\"></td></tr>");
		f.println("<tr><td>" + msg.getString("Clusters.Documents")
			+ ":</td><td><input type=\"text\" readonly name=\"docs\" "
			+ "size=\"30\"></td></tr>");
		f.println("</table>\n</form>");
		
		f.println(paintDendrogram(new File(root, "dendro.gif"), clustering));
		f.println("<P><IMG SRC=\"dendro.gif\" ALT=\""
			+ msg.getString("Clusters.Dendrogram_picture")
			+ "\" USEMAP=\"#Dendrogram\"></P>");
		f.println("</BODY>\n</HTML>");
		f.close();
		return f.bytesWritten();
	}
	
	private String trimStringToLength(String text, int l) {
		int trim = l;
		if (trim > text.length())
			trim = text.length();
		return text.substring(0, trim);
	}
	
	private void paintCoords(int xSize, int ySize) {
		float yStep = 1;
		while ((yStep*(ySize-50)/(threshold-lowThreshold)) < 20)
			yStep += 1;
		
		FontMetrics metrics = g.getFontMetrics();
		int height = metrics.getAscent();
		for (float y=lowThreshold; y<threshold; y+=yStep) {
			int yCoord = 10 + (int)((y-lowThreshold)*(ySize-50)/(threshold-lowThreshold));
			
			g.setColor(Color.LIGHT_GRAY);
			g.drawLine(45, yCoord, xSize, yCoord);
			g.setColor(Color.BLACK);
			
			String text = "" + y;
			text = trimStringToLength(text, 5);
			int width = metrics.stringWidth(text);
			g.drawString(text, 40-width, yCoord+height/2);
		}
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(45, ySize-40, xSize, ySize-40);
		g.setColor(Color.BLACK);
		
		String text = "" + threshold;
		text = trimStringToLength(text, 5);
		int width = metrics.stringWidth(text);
		g.drawString(text, 40-width, ySize-40+height/2);
		
		g.drawLine(45, 10, 45, ySize-35);
		g.drawLine(45, ySize-35, xSize, ySize-35);
	}
	
	private static final int maxVertLines = 200;
	public String paintDendrogram(File f, Cluster clustering) {
		//ArrayList clusters = null;
		lowThreshold = 0;
		threshold = (int)maxMergeValue + 1;
		
		do {
			threshold = threshold - 1;
			clusters = getClusters(clustering, threshold);
		} while (clusters.size() > maxVertLines);
		
		int size = clusters.size();
		factor = 1000 / size;
		if (factor < 4)
			factor = 4;
		
		int xSize = factor*size + 50;
		int ySize = 500 + 50;
		BufferedImage image = new BufferedImage(xSize+1, ySize+1,
			BufferedImage.TYPE_BYTE_INDEXED);
		g = (Graphics2D)image.getGraphics();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 30+xSize, 30+ySize);
		g.setPaintMode();
		
		paintCoords(xSize, ySize);
		
		mapString = "<map name=\"Dendrogram\">\n";
		minX = 50;
		minY = 10;
		maxY = ySize-40;
		drawCluster(clustering);
		g.setColor(Color.GRAY);
		g.drawLine(clustering.x, clustering.y, clustering.x, minY);
		
		try {
			FileOutputStream fo = new FileOutputStream(f);
			GIFEncoder encode = new GIFEncoder(image);
			encode.write(fo);
			fo.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return mapString+"</map>";
	}
	
	private  int minX, minY, maxY;
	private  int factor;
	private  ArrayList<Cluster> clusters;
	private  float threshold, lowThreshold;
	private  Graphics2D g;
	private  String mapString;
	
	public void drawCluster(Cluster cluster) {
		int index = clusters.indexOf(cluster);
		if (index != -1) {
			cluster.y = maxY;
			cluster.x = minX + index * factor;
			if (cluster.size() > 1) g.setColor(Color.RED);
			else g.setColor(Color.BLACK);
			g.drawRect(cluster.x-1, cluster.y-cluster.size(), 2, 1+cluster.size());
		} else {
			Cluster left = cluster.getLeft();
			Cluster right = cluster.getRight();
			drawCluster(left);
			drawCluster(right);
			int yBar = minY + (int)((maxY-minY)*(cluster.getSimilarity()/threshold));
			g.setColor(Color.DARK_GRAY);
			if (left.y>yBar) {
				g.drawLine(left.x, left.y-1, left.x, yBar);
				writeMap(left, yBar);
			}
			if (right.y>yBar) {
				g.drawLine(right.x, right.y-1, right.x, yBar);
				writeMap(right, yBar);
			}
			g.setColor(Color.BLACK);
			g.drawLine(left.x, yBar, right.x, yBar);
			cluster.x = (right.x+left.x) / 2;
			cluster.y = yBar;
		}
	}
	
	public void writeMap(Cluster cluster, float yBar) {
		Set<Submission> subSet = new HashSet<Submission>(cluster.size());
		String documents = "";
		for (int i=0; i<cluster.size(); i++) {
			Submission sub = submissions.elementAt(cluster.getSubmissionAt(i));
			documents += sub.name + " ";
			subSet.add(sub);
		}
		documents = documents.trim();
		String theme = ThemeGenerator.generateThemes(subSet,
			this.program.get_themewords(),false,this.program);
		mapString += "<area shape=\"rect\" coords=\"" + (cluster.x-2) + ","
			+ (yBar) + "," + (cluster.x+2) + "," + (cluster.y+2)
			+ "\" onMouseover=\"set('" + cluster.size() + "','"
			+ trimStringToLength(String.valueOf(cluster.getSimilarity()),6)
			+ "','" + trimStringToLength(documents, 50) + "','" + theme + "')\" ";
//		if (cluster.size() == 1)
//			mapString += "href=\"submission"+cluster.getSubmissionAt(0)+".html\">\n";
//		else
		mapString += "nohref>\n";
	}
}
