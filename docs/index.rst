JPlag - Detecting Software Plagiarism
=====================================

.. toctree::
   :maxdepth: 2
   :caption: Contents:


What is JPlag
-------------

JPlag is a system that finds similarities among multiple sets of source code files. This way it can detect software plagiarism and collusion in software development. JPlag does not merely compare bytes of text but is aware of programming language syntax and program structure and hence is robust against many kinds of attempts to disguise similarities between plagiarized files. JPlag currently supports Java, C#, C, C++, Python 3, Scheme, and natural language text.

JPlag is typically used to detect and thus discourage the unallowed copying of student exercise programs in programming education. But in principle, it can also be used to detect stolen software parts among large amounts of source text or modules that have been duplicated (and only slightly modified). JPlag has already played a part in several intellectual property cases where it has been successfully used by expert witnesses.

**Just to make it clear:** JPlag does not compare to the internet! It is designed to find similarities among the student solutions, which is usually sufficient for computer programs.

.. uml::
    :caption: JPlag Architecture (WIP)

    interface "front\nend" as ifrontend
    interface jPlag as ijplag
    interface utils as ifeutils

    component JPlag as jplag
    component CLI as cli

    component "Java Frontend" as java
    component "[More Frontends]" as morefrontends

    component "Utils 4 Frontend" as feutils


    ijplag -right- jplag
    ifeutils -- feutils
    ifrontend -- java
    ifrontend -- morefrontends

    java -right-( ifeutils
    morefrontends -left-( ifeutils
    jplag -right-( ifrontend
    cli -right-( ijplag


History
-------

Originally, JPlag was developed in 1996 by Guido Mahlpohl and others at the chair of Prof. Walter Tichy at Karlsruhe Institute of Technology (KIT). It was first documented in a `Tech Report <https://publikationen.bibliothek.kit.edu/542000>`__ in 2002 and later more formally in the `Journal of Universal Computer Science <http://www.ipd.kit.edu/tichy/uploads/publikationen/16/finding_plagiarisms_among_a_set_of_progr_638847.pdf>`__. Since 2015 JPlag is hosted here on GitHub. After 25 years of its creation, JPlag is still used frequently in many universities in different countries around the world.

Download JPlag
--------------

Download the latest version of JPlag `here <https://github.com/jplag/jplag/releases>`__. If you encounter bugs or other issues, please report them `here <https://github.com/jplag/jplag/issues>`__.

Include JPlag as a Dependency
-----------------------------

JPlag is released on `Maven Central <https://search.maven.org/search?q=de.jplag>`__, it can be included as follows:

.. code-block:: xml

    <dependency>
      <groupId>de.jplag</groupId>
      <artifactId>jplag</artifactId>
    </dependency>


JPlag legacy version
--------------------

In case you depend on the legacy version of JPlag we refer to the `legacy release v2.12.1 <https://github.com/jplag/jplag/releases/tag/v2.12.1-SNAPSHOT>`__ and the `legacy branch <https://github.com/jplag/jplag/tree/legacy>`__. Note that the legacy CLI usage is slightly different.

The following features are only available in version v3.0.0 and onwards:

* a Java API for third-party integration
* a simplified command-line interface
* support for Java files containing new language features
* improved colors for source codes matches in the report
* a parallel comparison mode

The following features are currently only supported in the legacy version but will be eventually restored:

* Result clustering
* Comparison based on maximum similarity

.. toctree::
    :hidden:

    howto/howto
    contributing/contributing