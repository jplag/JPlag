package jplag;

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.validation.SchemaFactory;

import jplag.clustering.Cluster;
import jplag.options.Options;
import net.s_nt.schuhmam.logger.Logger;
import net.s_nt.schuhmam.logger.LoggingManager;
import net.s_nt.schuhmam.xsd.ClusterTreeType;
import net.s_nt.schuhmam.xsd.ClusterType;
import net.s_nt.schuhmam.xsd.ComparisonType;
import net.s_nt.schuhmam.xsd.ComparisonsType;
import net.s_nt.schuhmam.xsd.JPlagHeaderType;
import net.s_nt.schuhmam.xsd.LimitEntriesType;
import net.s_nt.schuhmam.xsd.MatchLocationType;
import net.s_nt.schuhmam.xsd.ObjectFactory;
import net.s_nt.schuhmam.xsd.ParserLogEntryType;
import net.s_nt.schuhmam.xsd.Result;
import net.s_nt.schuhmam.xsd.SingleMatchType;
import net.s_nt.schuhmam.xsd.Result.AvgMatches;
import net.s_nt.schuhmam.xsd.Result.MaxMatches;
import net.s_nt.schuhmam.xsd.Result.MinMatches;
import net.s_nt.schuhmam.xsd.SubmissionProgramType;
import net.s_nt.schuhmam.xsd.SubmissionProgramsType;

/**
 * This class will generate a JPlagXMLResult.xml file which validates on the JPlagXMLSchema.xsd into the result directory.<br />
 * It is the interface between the JPlag API and the new designed XML output.
 * 
 * @author Markus.Schuhmacher
 * @see IReport
 */
public class ReportXML implements IReport
{
	private static final String JPLAG_XML_SCHEMA_FILENAME = "JPlagXMLSchema.xsd";
	private static final String JPLAG_XML_RESULT_FILENAME = "JPlagResult.xml";
	
	private final List<AllMatches> _allMatchesList;
	private final List<Submission> _allSubmissions;
	private Vector<Submission> _submissions;
	private final ObjectFactory _oF = new ObjectFactory();
	private final Hashtable<String, AllBasecodeMatches> _htBasecodeMatches;
	private Options _options;
	private SortedVector<AllMatches> _avgmatches, _maxmatches, _minmatches;
	private Cluster _clustering;
	
	private Result _result = this._oF.createResult();

	/**
	 * 
	 * @param allSubmissions The list of <strong>all</strong> {@link Submission} objects - even the invalid ones
	 * @param submissions The "original" Vector object of the submissions. Needed for legacy reasons because the cluster saves the index position of it members
	 * @param allMatchesList The list contains all created AllMatches objects.
	 * @param htBasecodeMatches A Hashtable with the submission name pointing to its AllMatches object of the basecode submission - easier for writing the submission-basecode AllMatches
	 */
	public ReportXML(List<Submission> allSubmissions, Vector<Submission> submissions, List<AllMatches> allMatchesList, Hashtable<String, AllBasecodeMatches> htBasecodeMatches)
	{
		this._allMatchesList = allMatchesList;
		this._allSubmissions = allSubmissions;
		this._submissions = submissions;
		this._htBasecodeMatches = htBasecodeMatches;
	}
	
	@Override
	public void write(File f, int[] dist, 
			SortedVector<AllMatches> avgmatches, 
			SortedVector<AllMatches> maxmatches, 
			SortedVector<AllMatches> minmatches, Cluster clustering, Options options) throws ExitException 
	{
		this._options = options;
		this._clustering = clustering;
		
		this._avgmatches = avgmatches;
		this._maxmatches = maxmatches;
		this._minmatches = minmatches;
				
		try
		{
			this.createResult();
			
			//finally the Marshaller creates the XML file out of the given root element Result - the JPlagXMLSchema.xsd is used for validation
			JAXBContext j = JAXBContext.newInstance(Result.class);
			SchemaFactory sF = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Marshaller marshall = j.createMarshaller(); marshall.setSchema(sF.newSchema(new File(JPLAG_XML_SCHEMA_FILENAME)));
			marshall.marshal(_result, new File(f, ReportXML.JPLAG_XML_RESULT_FILENAME));
			
			//copy the XML schema into the result directory so that the JPlag Web Service can archive it
			java.nio.file.Files.copy(new File(ReportXML.JPLAG_XML_SCHEMA_FILENAME).toPath(), new File(f, ReportXML.JPLAG_XML_SCHEMA_FILENAME).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExitException("Error while copying the Schema file");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExitException("Fehler beim Validieren des XML Schemas"); 
		}
	}
	
	/**
	 * This method will set everything necessary at the _result object
	 * @throws DatatypeConfigurationException
	 */
	private void createResult() throws DatatypeConfigurationException
	{
		this._result.setSubmissionPrograms(this.createSubmissionPrograms());
		this._result.setHeader(this.createHeader());
		this._result.setComparisons(this.createComparisons());
		
		if (this._options.clustering)
			this._result.setClusterTree(this.createCluster(this._clustering));

		this._result.setAvgMatches(this.createAvgMatches());
		this._result.setMaxMatches(this.createMaxMatches());
		if (this._options.comparisonMode == Options.COMPMODE_REVISION)
			this._result.setMinMatches(this.createMinMatches());
		
		List<ParserLogEntryType> generalLog = this._result.getGeneralLog();
		for(Logger.Entry entry : LoggingManager.createOrGetLogger("xmlGeneral").getMessages(Logger.MASK_ALL))
		{
			ParserLogEntryType log = this._oF.createParserLogEntryType();
			log.setMessage(entry._msg);
			log.setType(entry._type);
			generalLog.add(log);
		}
	}
	
	/**
	 * For more information please read the XML schema documentation.
	 * @return A complete JPlagHeaderType object
	 * @throws DatatypeConfigurationException
	 */
	private JPlagHeaderType createHeader() throws DatatypeConfigurationException
	{
		JPlagHeaderType header = this._oF.createJPlagHeaderType();
		header.setTitle(this._options.getTitle());
		header.setSourceDirectory(this._options.original_dir);
		header.setLanguage(this._options.language.name());
		
		LimitEntriesType le = this._oF.createLimitEntriesType();
		if (this._options.store_percent)
			le.setType("percent");
		else
			le.setType("number");
		le.setValue(this._options.store_matches); //Class CommandLineOptions is taking care of concating the % from the value 
		
		header.setLimitEntries(le);
		header.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		header.setMinTokenLength(this._options.min_token_match);
		
		List<String> suffixes = header.getLanguageSuffix();
		for (String s : this._options.suffixes)
			suffixes.add(s);
		
		header.setPathToFiles(this._options.sub_dir);
		header.setReadSubdirs(this._options.read_subdirs);
		header.setComparisonMode(this._options.comparisonMode);

		if (this._options.useBasecode)
			header.setBasecodeSubmission(this._result.getSubmissionPrograms().getSubmissionProgram(this._options.basecode));
		
		return header;
	}
	
	/**
	 * For more information please read the XML schema documentation.
	 * @return A complete list of all submissions stored in the SubmissionProgramsType object
	 */
	private SubmissionProgramsType createSubmissionPrograms()
	{
		SubmissionProgramsType programs = this._oF.createSubmissionProgramsType();
		List<SubmissionProgramType> l = programs.getProgram();

		for(Submission s : this._allSubmissions)
		{
			SubmissionProgramType program = this._oF.createSubmissionProgramType();
			program.setName(s.name);
			program.setId("programID" + s.name);
			
			if (this._options.useBasecode)
			{
				//Bei dem Vergleich von Submission mit Basecode ist subA immer Submission und subB immer basecode
				AllMatches tmp = this._htBasecodeMatches.get(s.name);
				if (tmp != null)
				{
					float bcPercent = tmp.percentA();
					program.setBasecodeMatchPercentage(bcPercent);						
				}
			}
			
			Logger log = LoggingManager.createOrGetLogger(s.name);
			List<ParserLogEntryType> programLog = program.getParser();
			List<Logger.Entry> logs = log.getMessages(Logger.MASK_ALL);
			boolean ok = true;
			for (Logger.Entry entry : logs)
			{
				ParserLogEntryType logEntry = this._oF.createParserLogEntryType();
				logEntry.setMessage(entry._msg);
				logEntry.setType(entry._type);
				
				if(entry._type == Logger.ERROR)
					ok = false;
				
				programLog.add(logEntry);
			}
			
			program.setParsedSuccessfully(ok);
			l.add(program);
		}

		return programs;
	}
	
	/**
	 * For more information please read the XML schema documentation.
	 * @return A complete list of all submission comparisons (AllMatches objects) which were created during the compare methods.
	 */
	private ComparisonsType createComparisons()
	{
		ComparisonsType comparisons = this._oF.createComparisonsType();
		List<ComparisonType> comparisonList = comparisons.getComparison();
		
		for (AllMatches match : this._allMatchesList)
			comparisonList.add(this.createComparisonType(match));
		
		if (this._options.useBasecode)
			for (AllBasecodeMatches bcMatch : this._htBasecodeMatches.values())
			{
				ComparisonType bcComparison = this.createComparisonType(bcMatch);
				this._result.getSubmissionPrograms().getSubmissionProgram(bcMatch.subA.name).setBasecodeMatch(bcComparison);
				comparisonList.add(bcComparison);
			}
		
		return comparisons;
	}
		
	/**
	 * Will fill the list of ComparisonType references according to the avgmatches content.
	 */
	private AvgMatches createAvgMatches()
	{
		AvgMatches matches = this._oF.createResultAvgMatches();
		fillListWithComparisonType(this._avgmatches, matches.getComparison());
		
		return matches;
	}
	
	/**
	 * Will fill the list of ComparisonType references according to the maxmatches content.
	 */
	private MaxMatches createMaxMatches()
	{
		MaxMatches matches = this._oF.createResultMaxMatches();
		fillListWithComparisonType(this._maxmatches, matches.getComparison());
		
		return matches;
	}
	
	/**
	 * Will fill the list of ComparisonType references according to the minmatches content.
	 */
	private MinMatches createMinMatches()
	{
		MinMatches matches = this._oF.createResultMinMatches();
		fillListWithComparisonType(this._minmatches, matches.getComparison());
		
		return matches;
	}
	
	/**
	 * This method will fill List<Object> l with ComparisonType references for every entry in SortedVector sVec. Every ComparisonType object has a fixed id naming schema
	 * @param sVec The set of AllMatches objects that shall be found in the finished ComparisonsType object
	 * @param l the List will contain the search results
	 */
	private void fillListWithComparisonType(SortedVector<AllMatches> sVec, List<Object> l)
	{
		for(AllMatches allMatches : sVec)
			l.add(this.getComparisonByID(String.format("compare%sWith%s", allMatches.subA.name, allMatches.subB.name)));
	}
	
	private ComparisonType createComparisonType(AllMatches match)
	{
		ComparisonType comparison = this._oF.createComparisonType();
		comparison.setId(String.format("compare%sWith%s", match.subA.name, match.subB.name));
		comparison.setPercentAverage(match.percent());
		comparison.setPercentA(match.percentA());
		comparison.setPercentB(match.percentB());
		comparison.setProgramA(this._result.getSubmissionPrograms().getSubmissionProgram(match.subA.name));
		comparison.setProgramB(this._result.getSubmissionPrograms().getSubmissionProgram(match.subB.name));
				
		List<SingleMatchType> matches = comparison.getSingleMatch();
		for (int i = 0; i < match.size(); ++i)
		{
			SingleMatchType xmlMatch = this._oF.createSingleMatchType();
			Match m = match.matches[i];
			xmlMatch.setTokenCount(m.length);
			
			Token startA = match.subA.struct.tokens[m.startA];
			Token startB = match.subB.struct.tokens[m.startB];
			xmlMatch.setBeginA(this.buildLocation(startA));
			xmlMatch.setBeginB(this.buildLocation(startB));
			
			Token endA = match.subA.struct.tokens[m.startA + m.length - 1]; //Ich verstehe nicht, wieso der HTML Bericht -1 rechnet (oder warum der Durchschnittsprozentwert mit ganzer Länge errechnet wird) @see AllMatches.java:233
			Token endB = match.subB.struct.tokens[m.startB + m.length - 1];
			xmlMatch.setEndA(this.buildLocation(endA));
			xmlMatch.setEndB(this.buildLocation(endB));
			
			matches.add(xmlMatch);
		}
		
		return comparison;
	}
	
	/**
	 * This method will search for a previously created ComparisonType object with a given AllMatches object.
	 * @param id the id of the ComparisonType object which shall be found in the ComparisonsType object
	 * @return the corresponding ComparisonType object
	 */
	private ComparisonType getComparisonByID(String id)
	{
		List<ComparisonType> l = this._result.getComparisons().getComparison();
		for (ComparisonType comparison : l)
		{
			if (comparison.getId().equals(id))
				return comparison;
		}
		
		return null;
	}
	
	/**
	 * From a given {@link Token} t this method will return a MatchLocationType object.
	 * @param t the {@link Token} object which will be translated into a MatchLocationType object
	 * @return A MatchLocationType object which represents a position in a file
	 */
	private MatchLocationType buildLocation(Token t)
	{
		MatchLocationType result = this._oF.createMatchLocationType();
		result.setFileName(t.file);
		result.setLine(t.getLine());
		result.setColumn(t.getColumn());
		
		return result;
	}
	
	/**
	 * This method will initiate the creation of the cluster tree 
	 * @param clustering The root cluster node
	 * @return A finshed {@link ClusterTreeType} which only contains the root element of the cluster. Every cluster has neighbour left and right.
	 */
	private ClusterTreeType createCluster(Cluster clustering)
	{
		ClusterTreeType clTree = this._oF.createClusterTreeType();
		ClusterType root = this._oF.createClusterType();
		this.createCluster(clustering, root);
		
		clTree.setRoot(root);
		return clTree;
	}
	
	/**
	 * A recursive method which builds up a tree of {@link Cluster} objects.
	 * @param clustering The {@link Cluster} object which should be translated into cl
	 * @param cl the {@link ClusterType} object where the method shall store the information of the clustering variable
	 */
	private void createCluster(Cluster clustering, ClusterType cl)
	{
		List<Object> programs = cl.getPrograms();
		Set<Submission> subSet = new HashSet<Submission>(clustering.size());
		for (int i = 0; i < clustering.size(); ++i)
		{
			Submission sub = this._submissions.get(clustering.getSubmissionAt(i));
			programs.add(this._result.getSubmissionPrograms().getSubmissionProgram(sub.name));
			subSet.add(sub);
		}
		
		cl.setThreshold(clustering.getSimilarity());
		cl.setMostFrequentWords(clustering.getClusters().generateThemes(subSet));
		cl.setClusterID("clusterID" + clustering.hashCode());

		if (clustering.size() == 1)
			return;
		
		Cluster left = clustering.getLeft();
		if (left != null)
		{
			ClusterType xmlLeft = this._oF.createClusterType();
			cl.setLeft(xmlLeft);
			this.createCluster(left, xmlLeft);
		}
		
		Cluster right = clustering.getRight();
		if (right != null)
		{
			ClusterType xmlRight = this._oF.createClusterType();
			cl.setRight(xmlRight);
			this.createCluster(right, xmlRight);
		}
	}
}