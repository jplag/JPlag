# Multi-Language module

This module allows parsing multiple languages in one run of JPlag. The parsing will be delegated to a different language-module per file.

This does not allow comparing code from different languages, but it can be used to compare multiple languages in the same run.

By default, all supported languages (except text) are parsed. This can be changed by specifying the language modules to use with the --languages parameter: `java -jar jplag.jar <root folder> multi --languages <firstLanguage>,<secondLanguage>,...`
