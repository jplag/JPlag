# JPlag C language module

This module allows the use of JPlag with submissions in c.

## Usage

To parse C submissions run JPlag with: `<jplag> <options> c` or use the `-l c` options.
To use the module from the API configure your `JPlagOption` object with `new CLanguage()` as 'Language' as described in the usage information in the [readme](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

## C++

This module might work with C++ submissions. However you should use the [cpp module](https://github.com/jplag/JPlag/tree/main/languages/cpp) for that.