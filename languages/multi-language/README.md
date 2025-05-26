# Multi-Language module

This module allows to compare submissions that contain code files in multiple programming languages. The parsing will be delegated to the language modules associated with the respective file extensions.

This does not entail comparing implementations of the same functionality in different languages to each other, but allows comparing submissions that use more than one language each.

By default, all supported languages (except text) are parsed. This can be changed by specifying the language modules to use with the `--languages` parameter: `java -jar jplag.jar <root folder> multi --languages <firstLanguage>,<secondLanguage>,...`
