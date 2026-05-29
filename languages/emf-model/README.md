# Dynamic EMF model language module
The EMF model language module allows the use of JPlag with EMF based modeling file submissions.
It is based on the EMF API.

### EMF specification compatibility
This module is based on the EMF dependencies available on maven central. These might not be the newest versions of EMF. For details, the [JPlag aggregator pom](https://github.com/jplag/JPlag/blob/263e85e544152cc8b0caa3399127debb7a458746/pom.xml#L84-L86).

### Token Extraction
For the token extraction, we visit the containment tree of the model and extract tokens for all model elements based on their concrete metaclass. In this module, we thus extract tokens based on a dynamic token set. This works well for structural models with tree-like structures. It is less effective for models where the containment structure is not semantically relevant (e.g. state charts). These kinds of models require a dedicated language module.

### Usage
The input for this is an EMF metamodel and a set of corresponding instances.
To use this module, add the `emf-model` subcommand in the CLI, or use a `JPlagOption` object with `new EmfModelLanguage()` as `language` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

In order to correctly parse model instances, the required metamodel files need to be specified. This can be done via the language options of the `emf-model` language (see the language options via `jplag.jar emf-model -h`). Specify one or more metamodel files like this (multiple paths are comma-separated):

`java -jar jplag.jar path/to/model/instances emf-model --metamodel path/to/metamodel/file.ecore`

To ensure only the intended model files are parsed in the submissions, you can use `-p` to specify allowed file types, for example: `-p ecore,xmi,mysuffix`. Note: Normal JPlag parameters need to be specified before the `emf-model` language identifier.

### Report Viewer
In the report viewer, a simple textual syntax is used to generate a tree-based model view.
To provide a custom visualization of a specific metamodel, a custom language module is required.

### Literature
* [*"Token-based Plagiarism Detection for Metamodels" (MODELS-C'22)*](https://dl.acm.org/doi/10.1145/3550356.3556508).
* Its [Kudos Summary](https://www.growkudos.com/publications/10.1145%25252F3550356.3556508/reader).
* [*"Token-based Plagiarism Detection for Metamodels" (MODELS-C'22)*]
* *"Automated Detection of AI-Obfuscated Plagiarism in Modeling Assignments" (ICSE-SEET'24)*