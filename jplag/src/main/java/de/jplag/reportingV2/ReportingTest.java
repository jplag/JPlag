package de.jplag.reportingV2;

import de.jplag.ExitException;
import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;

import java.util.List;

public class ReportingTest {

	public static void main(String[] args) throws ExitException {
		JPlagOptions options = new JPlagOptions("C:\\Uni\\PISE\\jplag-try-out\\src\\submission", LanguageOption.JAVA_1_9);
		JPlag jPlag = new JPlag(options);
		JPlagResult result = jPlag.run();
		Report reporting = new JsonReport();
		if ( reporting.saveReport(result) ) {
			System.out.println("Successfully saved report.");
		}
	}
}
