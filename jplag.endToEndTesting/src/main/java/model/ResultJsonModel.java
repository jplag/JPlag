package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultJsonModel {

	@JsonProperty("functionName")
	private String functionName;
	@JsonProperty("resultSimilarity")
	private String resultSimilarity;
	
	public ResultJsonModel(String functionName, String resultSimilarity)
	{
		this.functionName = functionName;
		this.resultSimilarity = resultSimilarity;
	}
	
	public ResultJsonModel()
	{
	}
	
	public float similarity()
	{
		return Float.parseFloat(resultSimilarity);
	}
	
	public String getFunctionName()
	{
		return functionName;
	}
}
