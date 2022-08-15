package de.jplag.end_to_end_testing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.jplag.options.JPlagOptions;

public class Options  {
	@JsonProperty("minimum_token_match")
	private int minimumTokenMatch;
	
	public Options(int minimumTokenMatch)
	{
		this.minimumTokenMatch = minimumTokenMatch;
	}
	
	public Options(JPlagOptions jplagOptions)
	{
		this.minimumTokenMatch = jplagOptions.getMinimumTokenMatch();
	}
	
	/**
	 * empty constructor in case the serialization contains an empty object to
	 * prevent throwing exceptions. this constructor was necessary for serialization
	 * with the Jackson parse extension
	 */
	public Options() {
		// For Serialization
	}
	
	@JsonIgnore
	public int getMinimumTokenMatch()
	{
		return minimumTokenMatch;
	}
	
	@JsonIgnore
	public boolean equals(Options options) {
		// TODO Auto-generated method stub
		return minimumTokenMatch == options.getMinimumTokenMatch();
	}
 }
