# JPlag-cpg Abstract Interpretation Engine

For debug, please run with `-ea` JVM flag.

**For now a lot of methods have a default switch case that throws an exception.
They will later be replaced with a default case that sets the value to unknown and returns an unknown value.**

All inputted code must be syntactically correct Java code that compiles without errors.

## Build

Maven: `mvn clean package`

## Code Structure

This module offers two passes for cpg.
The AiPass analyzes the whole cpg translation result.
The AiMethodPass analyzes every single method from a translation result independently.
Both of them use the AbstractInterpretation class, which is the main class in this module.

## Explicitly not supported language features

- exception flow is not modeled
- System.exit calls are not supported
- Continues and breaks in loops are not supported
- Iterators are not supported

## Usage Example

```java
import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.Language;
import de.jplag.exceptions.ExitException;
import de.jplag.java_cpg.JavaCpgLanguage;
import de.jplag.options.JPlagOptions;
import de.jplag.reporting.reportobject.ReportObjectFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

public static void main(String[] args) {
    Language language = new JavaCpgLanguage();
    File submissionsRoot = new File(".../submissionsFolders");
    Set<File> submissionDirectories = Set.of(submissionsRoot);
    JPlagOptions options = new JPlagOptions(language, submissionDirectories, Set.of());
    try {
        JPlagResult result = JPlag.run(options);
        File outDir = new File(System.getProperty("user.home") + "/Downloads/");
        ReportObjectFactory reportObjectFactory = new ReportObjectFactory(outDir);
        reportObjectFactory.createAndSaveReport(result);
        System.out.println("JPlag analysis finished. Report written to: " + outDir.getAbsolutePath());
    } catch (ExitException e) {
        System.err.println("JPlag exited with an error: " + e.getMessage());
    } catch (FileNotFoundException e) {
        System.err.println("I/O error: " + e.getMessage());
    }
}
```
