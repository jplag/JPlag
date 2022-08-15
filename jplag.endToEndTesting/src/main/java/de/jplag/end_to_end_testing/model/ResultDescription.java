package de.jplag.end_to_end_testing.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultDescription {
	@JsonProperty("options")
	Options options;
	@JsonProperty("tests")
	Map<String, ExpectedResult> identifierToResultMap;
	
	public ResultDescription(Options options, Map<String, ExpectedResult> identifierToResultMap)
	{
		this.options = options;
		this.identifierToResultMap = identifierToResultMap;
	}
	
	/**
	 * empty constructor in case the serialization contains an empty object to
	 * prevent throwing exceptions. this constructor was necessary for serialization
	 * with the Jackson parse extension
	 */
	public ResultDescription()
	{
		// For Serialization
	}
	
	@JsonIgnore
	public ExpectedResult getExpectedResultByIdentifier(String identifier)
	{
		return identifierToResultMap.get(identifier);
	}
	
	@JsonIgnore
	public Options getOptions()
	{
		return options;		
	}
}
