/*
 * @Author  Emeric Kwemou on 12.02.2005
 *
 * 
 */
package jplag.options;

import java.lang.reflect.Constructor;

import jplag.Language;
import jplag.Program;

/**
 * @Changed by Emeric Kwemou 12.02.2005
 * 
 */

public class OptionContainer extends Options {
	private String languageName = "java12";// Default
	private boolean found1 = false;

	public OptionContainer() {
	}

	public void setLanguage(String language) {
		this.languageName = language;
	}

	public void initializeSecondStep(Program program) throws jplag.ExitException {
		for (int j = 0; j < languages.length - 1; j += 2)
			if (languageName.equals(languages[j]))
				try {
					// Changed by Emeric Kwemou 13.01.05
					Constructor<?>[] laguageConstructors = Class.forName(languages[j + 1]).getDeclaredConstructors();
					Constructor<?> cons = laguageConstructors[0];
					Object[] ob = { program };
					// All Language have to have a program as Constructor Parameter
					// ->public Language(Program prog)
					Language tmp = (Language) cons.newInstance(ob);
					this.language = tmp;
					System.out.println("Language accepted ...................##########################################    " + tmp.name());
					this.min_token_match = this.language.min_token_match();
					this.suffixes = this.language.suffixes();
					found1 = true;
				} catch (ClassNotFoundException e) {
					System.out.println(e.getMessage() + "oui");
				} catch (IllegalAccessException e) {
					System.out.println(e.getMessage() + "oui");
				} catch (InstantiationException e) {
					System.out.println(e.getMessage() + "oui");
				} catch (Exception e) {
					e.printStackTrace();
					throw new jplag.ExitException("Language instantiation failed!");
				}
		if (!found1) {
			throw new jplag.ExitException("Unknown language: " + languageName);
		}

		// defaults
		if (!min_token_match_set)
			this.min_token_match = this.language.min_token_match();
		if (!suffixes_set)
			this.suffixes = this.language.suffixes();
	}
}
