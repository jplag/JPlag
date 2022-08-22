# EMF metamodel language frontend
The EMF metamodel language frontend allows the use of JPlag with metamodel submissions.
It is based on the EMF API.

### EMF specification compatibility
This frontend is based on the EMF dependencies available on maven central. These might not be the newest versions of EMF. For details, the [JPlag aggregator pom](https://github.com/jplag/JPlag/blob/263e85e544152cc8b0caa3399127debb7a458746/pom.xml#L84-L86).

### Token Extraction
For the token extraction, we visit the containment tree of the metamodel and extract tokens for all metamodel elements based on their concrete metaclass. In this module, we thus extract tokens based on a [dynamic token set](https://github.com/jplag/JPlag/blob/263e85e544152cc8b0caa3399127debb7a458746/jplag.frontend.emf-metamodel-dynamic/src/main/java/de/jplag/emf/dynamic/DynamicMetamodelTokenConstants.java).

### Usage
To use this frontend, add the `-l emf-metamodel-dynamic` flag in the CLI, or use a `JPlagOption` object set to `LanguageOption.EMF_DYNAMIC` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

### More Info
More information can be found in the paper *"Token-based Plagiarism Detection for Metamodels"* (MODELS-C 2022, accepted for publication, link coming soon). 