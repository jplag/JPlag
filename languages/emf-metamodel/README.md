# EMF metamodel language module
The EMF metamodel language module allows the use of JPlag with metamodel submissions.
It is based on the EMF API.

### EMF specification compatibility
This module is based on the EMF dependencies available on maven central. These might not be the newest versions of EMF. For details, the [JPlag aggregator pom](https://github.com/jplag/JPlag/blob/263e85e544152cc8b0caa3399127debb7a458746/pom.xml#L84-L86).

### Token Extraction
For the token extraction, we visit the containment tree of the metamodel and extract tokens for certain metamodel elements based on their metaclass. In this module, we extract tokens based on a [handcrafted token set](https://github.com/jplag/JPlag/blob/master/languages/emf-metamodel/src/main/java/de/jplag/emf/MetamodelTokenType.java). Note that not for all concrete metaclasses tokens are extracted. `EFactory`, `EGenericType`, and `EObject` are ignored. Moreover, for some metaclasses, multiple token types are extracted. Finally, some references are also used for token extraction.

### Usage
To use this module, add the `-l emf-metamodel` flag in the CLI, or use a `JPlagOption` object with `new de.jplag.emf.Language()` as `language` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

### More Info
More information can be found in the paper [*"Token-based Plagiarism Detection for Metamodels" (MODELS-C'22)*](https://dl.acm.org/doi/10.1145/3550356.3556508).
A short summary can be found on [Kudos](https://www.growkudos.com/publications/10.1145%25252F3550356.3556508/reader).
