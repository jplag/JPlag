package atujplag.util;


import jplagWsClient.jplagClient.Option;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class LanguageSetting {
	private String name;
	private String suffixes;
	private int minMatchLen;
	private String clusterType;
	private boolean readSubdirs;
	private String storeMatches;
	
	private Element elem;
	
	/**
	 * Adds a new language element with the given name "name" to the
	 * document's root element. Unset attributes stay as default values
	 */
	public LanguageSetting(Document doc, String name) {
		this.name = name;
		elem = doc.createElement("language");
		elem.setAttribute("name",name);
		elem.setAttribute("storeMatches","20");
		storeMatches = "20";
		clusterType = "";
		suffixes = "";
		doc.getDocumentElement().appendChild(elem);
	}
	
	public LanguageSetting(Element elem) {
		this.elem = elem;
		name = elem.getAttribute("name");
		if(name.length()==0) name = "Illegal language name";
		suffixes = elem.getAttribute("suffixes"); //$NON-NLS-1$
		minMatchLen = parseElemInt("minMatchLen"); //$NON-NLS-1$
		clusterType = elem.getAttribute("clusterType"); //$NON-NLS-1$
		readSubdirs = parseElemBoolean("readSubdirs"); //$NON-NLS-1$
		storeMatches = elem.getAttribute("storeMatches"); //$NON-NLS-1$
	}
    
    /**
     * Constructs an element-less LanguageSetting object out of a Option object.
     * The set methods may not be used for the resulting object!
     */
    public LanguageSetting(Option opt) {
        elem = null;
        name = opt.getLanguage();
        
        String[] suffixArray = opt.getSuffixes();
        StringBuffer strbuf = new StringBuffer();
        for(int i=0;i<suffixArray.length;i++) {
            strbuf.append(suffixArray[i]);
            if(i!=suffixArray.length-1) strbuf.append(',');
        }
        this.suffixes = strbuf.toString();
        
        minMatchLen = opt.getMinimumMatchLength();
        clusterType = opt.getClustertype();
        readSubdirs = opt.isReadSubdirs();
        storeMatches = opt.getStoreMatches();
    }
    
    /**
     * Constructs an element-less LanguageSetting object using the specified
     * parameters. "storeMatches" will be initialised to "20" and clusterType
     * to "". To be used for applying default settings
     */

	public LanguageSetting(String name, String[] suffixArray, int minMatchLen) {
		this.elem = null;
		this.name = name;
		this.minMatchLen = minMatchLen;
		storeMatches = "20";
		clusterType = "";
		
		StringBuffer strbuf = new StringBuffer();
		for(int i=0;i<suffixArray.length;i++) {
			strbuf.append(suffixArray[i]);
			if(i!=suffixArray.length-1) strbuf.append(',');
		}
		this.suffixes = strbuf.toString();
	}
    
	private void setElemString(String name, String str) {
		elem.setAttribute(name, (str == null) ? "" : str); //$NON-NLS-1$
	}
	
	private void setElemInt(String name, int val) {
		setElemString(name, val + ""); //$NON-NLS-1$
	}
	
	private int parseElemInt(String name) {
		try {
			return Integer.parseInt(elem.getAttribute(name));
		}
		catch(NumberFormatException ex) {
			System.out.println("LanguageSetting: Attribute \""+name+"\" is not "
					+ "a number!");
			return 0;
		}
	}
	
	private boolean parseElemBoolean(String name) {
		return elem.getAttribute(name).equals("true"); //$NON-NLS-1$
	}
	
	public String getClusterType() {
		return clusterType;
	}
	public void setClusterType(String clusterType) {
		this.clusterType = clusterType;
		setElemString("clusterType", clusterType); //$NON-NLS-1$
	}
	
	public int getMinMatchLen() {
		return minMatchLen;
	}
	public void setMinMatchLen(int minMatchLen) {
		this.minMatchLen = minMatchLen;
		setElemInt("minMatchLen", minMatchLen); //$NON-NLS-1$
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isReadSubdirs() {
		return readSubdirs;
	}
	public void setReadSubdirs(boolean readSubdirs) {
		this.readSubdirs = readSubdirs;
		setElemString("readSubdirs", (readSubdirs?"true":"false")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String getStoreMatches() {
		return storeMatches;
	}
	public void setStoreMatches(String storeMatches) {
		this.storeMatches = storeMatches;
		setElemString("storeMatches", storeMatches); //$NON-NLS-1$
	}
	
	public String getSuffixes() {
		return suffixes;
	}
	public void setSuffixes(String[] suffArray) {
		StringBuffer strbuf = new StringBuffer();
		for(int i=0;i<suffArray.length;i++) {
			strbuf.append(suffArray[i]);
			if(i!=suffArray.length-1) strbuf.append(',');
		}
		suffixes = strbuf.toString();
		setElemString("suffixes", suffixes);
	}
}
