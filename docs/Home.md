<p align="center"> 
	<img alt="JPlag logo" src="https://user-images.githubusercontent.com/4396919/190650441-5c9407a0-94db-4b05-ae0d-518427db3529.png" width=300px>
</p>

## What is JPlag
JPlag is a system that finds similarities among multiple sets of source code files. This way it can detect software plagiarism and collusion in software development. JPlag does not merely compare bytes of text but is aware of programming language syntax and program structure and hence is robust against many kinds of attempts to disguise similarities between plagiarized files. JPlag currently supports Java, C#, C/C++, Python 3, Go, Rust, Kotlin, Swift, Scala, Scheme, EMF, and natural language text.

JPlag is typically used to detect and thus discourage the unallowed copying of student exercise programs in programming education. But in principle, it can also be used to detect stolen software parts among large amounts of source text or modules that have been duplicated (and only slightly modified). JPlag has already played a part in several intellectual property cases where it has been successfully used by expert witnesses.

[TODO]: <> (Link or visualize example report)

**Just to make it clear**: JPlag does not compare to the internet! It is designed to find similarities among the student solutions, which is usually sufficient for computer programs.

## History
Originally, JPlag was developed in 1996 by Guido Mahlpohl and others at the chair of Prof. Walter Tichy at Karlsruhe Institute of Technology (KIT). It was first documented in a [Tech Report](https://publikationen.bibliothek.kit.edu/542000) in 2000 and later more formally in the [Journal of Universal Computer Science](http://www.ipd.kit.edu/tichy/uploads/publikationen/16/finding_plagiarisms_among_a_set_of_progr_638847.pdf). Since 2015 JPlag is hosted here on GitHub. After 25 years of its creation, JPlag is still used frequently in many universities in different countries around the world.

## Download JPlag
Download the latest version of JPlag [here](https://github.com/jplag/jplag/releases). If you encounter bugs or other issues, please report them [here](https://github.com/jplag/jplag/issues).

## Using JPlag
Use JPlag via the CLI to analyze your set of source codes. You can display your results via [jplag.github.io](https://jplag.github.io/JPlag/). No data will leave your computer! More usage information can be found [here](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

## Include JPlag as a Dependency
JPlag is released on [Maven Central](https://search.maven.org/search?q=de.jplag), it can be included as follows:
```xml
<dependency>
  <groupId>de.jplag</groupId>
  <artifactId>jplag</artifactId>
</dependency>
```

## JPlag legacy version
In case you depend on the legacy version of JPlag we refer to the [legacy release v2.12.1](https://github.com/jplag/jplag/releases/tag/v2.12.1-SNAPSHOT) and the [legacy branch](https://github.com/jplag/jplag/tree/legacy). Note that the legacy CLI usage is slightly different.

The following features are only available in version v4.0.0 and onwards:
* a modern web-based UI
* a simplified command-line interface
* support for Kotlin, Scala, Go, Rust, and R
* support for Java 17 language features
* a Java API for third-party integration
