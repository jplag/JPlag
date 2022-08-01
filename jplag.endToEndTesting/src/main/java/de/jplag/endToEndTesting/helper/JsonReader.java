package de.jplag.endToEndTesting.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.Reader;

import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * read in a json file located at the passing path
	 * 
	 * @param path to the json file with extension
	 * @return a JSONObject of the persistence file in .json format
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject readJsonFromPath(String path) throws IOException, JSONException {
		if (!path.endsWith(".json")) {
			throw new JSONException("the specified filename in the path must be a *.json file");
		}

		File initialFile = new File(path);
		InputStream targetStream = new FileInputStream(initialFile);

		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(targetStream, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			targetStream.close();
		}
	}
}
