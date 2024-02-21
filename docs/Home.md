<p align="center"> 
	<img alt="JPlag logo" src="https://user-images.githubusercontent.com/4396919/190650441-5c9407a0-94db-4b05-ae0d-518427db3529.png" width=300px>
</p>

## What is JPlag
JPlag finds pairwise similarities among a set of multiple programs. It can reliably detect software plagiarism and collusion in software development. All similarities are calculated locally, and no source code or plagiarism results are ever uploaded to the internet. JPlag supports a large number of programming and modeling languages. JPlag does not merely compare bytes of text but is aware of programming language syntax and program structure and hence is robust against many kinds of attempts to disguise similarities (_obfusction_) between plagiarized files.

JPlag is typically used to detect and thus discourage the unallowed copying of student exercise programs in programming education. But in principle, it can also be used to detect stolen software parts among large amounts of source text or modules that have been duplicated (and only slightly modified). JPlag has already played a part in several intellectual property cases where it has been successfully used by expert witnesses.

**Just to make it clear**: JPlag does not compare to the internet! It is designed to find similarities among the student solutions, which is usually sufficient for computer programs.

* 📈 [JPlag Demo](https://jplag.github.io/Demo/)

* 🏛️ [JPlag on Helmholtz RSD](https://helmholtz.software/software/jplag)

* 🤩 [Give us Feedback in a **short (<5 min) survey**](https://docs.google.com/forms/d/e/1FAIpQLSckqUlXhIlJ-H2jtu2VmGf_mJt4hcnHXaDlwhpUL3XG1I8UYw/viewform?usp=sf_link)

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
  <version><!--desired version--></version>
</dependency>
```

## JPlag legacy version
In case you depend on the legacy version of JPlag, we refer to the [legacy release v2.12.1](https://github.com/jplag/jplag/releases/tag/v2.12.1-SNAPSHOT) and the [legacy branch](https://github.com/jplag/jplag/tree/legacy). Note that the legacy CLI and report UI are different and provide fewer features.
