# Multi-Language module

This module allows submissions to be compared with files in multiple programming languages. The parsing will be delegated to a different language module per file extension.

This does not entail comparing implementations of the same functionality in different languages to each other, but allows comparing submissions that use more than one language each.

By default, all supported languages (except text) are parsed. This can be changed by specifying the language modules to use with the `--languages` parameter: `java -jar jplag.jar <root folder> multi --languages <firstLanguage>,<secondLanguage>,...`
