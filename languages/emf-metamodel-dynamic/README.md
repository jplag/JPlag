# Dynamic EMF metamodel language module
The dynamic EMF metamodel language module allows the use of JPlag with EMF metamodel submissions.
It is based on the EMF API.

### EMF specification compatibility
This module is based on the EMF dependencies available on maven central. These might not be the newest versions of EMF. For details, the [JPlag aggregator pom](https://github.com/jplag/JPlag/blob/263e85e544152cc8b0caa3399127debb7a458746/pom.xml#L84-L86).

### Token Extraction
For the token extraction, we visit the containment tree of the metamodel and extract tokens for all metamodel elements based on their concrete metaclass. In this module, we thus extract tokens based on a dynamic token set.

### Usage
Note that this language module is currently not offered via the CLI.
Use the non-dymamic version instead (`-l emf`).

### Report Viewer
In the report viewer, Emfatic is used as a textual model view.

### Literature
* [*"Token-based Plagiarism Detection for Metamodels" (MODELS-C'22)*](https://dl.acm.org/doi/10.1145/3550356.3556508).
* Its [Kudos Summary](https://www.growkudos.com/publications/10.1145%25252F3550356.3556508/reader).
* [*"Token-based Plagiarism Detection for Metamodels" (MODELS-C'22)*]
* *"Automated Detection of AI-Obfuscated Plagiarism in Modeling Assignments" (ICSE-SEET'24)*
