# Native Library Building

This directory contains scripts and configuration for building native Tree-sitter libraries for JPlag.

## Overview

JPlag uses Tree-sitter for parsing source code in various languages. The native libraries need to be compiled for different platforms and architectures. This build system automates the process of building these libraries and placing them in the correct locations within the project structure.

## Supported Libraries

- **tree-sitter**: Core Tree-sitter parsing engine
- **tree-sitter-python**: Python language grammar for Tree-sitter

## Build Process

### Prerequisites

- Git
- Make
- GCC (or appropriate C compiler for your platform)
- Maven

### Building Libraries

#### Using Maven Profile

The easiest way to build all native libraries is using the Maven profile:

```bash
mvn -Pbuild-native-libraries generate-resources
```

This will:
1. Clone the required repositories
2. Build the libraries using `make`
3. Copy the resulting libraries to the appropriate `resources/native/{OS}` directories

#### Manual Building

You can also build individual libraries manually:

```bash
# Build tree-sitter core
./scripts/build-native-libraries.sh tree-sitter 0.25.1

# Build tree-sitter-python
./scripts/build-native-libraries.sh tree-sitter-python 0.23.6
```

### Output Structure

The built libraries are placed in the following versioned structure:

```
language-tree-sitter-utils/src/main/resources/native/
├── v0.25.1/
│   ├── linux/
│   │   └── libtree-sitter.so
│   ├── mac/
│   │   └── libtree-sitter.dylib
│   └── windows/
│       └── libtree-sitter.dll

languages/python/src/main/resources/native/
├── v0.23.6/
│   ├── linux/
│   │   └── libtree-sitter-python.so
│   ├── mac/
│   │   └── libtree-sitter-python.dylib
│   └── windows/
│       └── libtree-sitter-python.dll
```

This versioned structure allows multiple library versions to coexist and enables proper cache management in the native library loading system.

## CI/CD Integration

The GitHub Actions workflow `.github/workflows/build-native-libraries.yml` automatically builds native libraries for different platforms:

- **Linux**: Ubuntu with build-essential
- **macOS**: Latest macOS with Xcode command line tools
- **Windows**: Windows with Visual Studio Build Tools

The workflow runs on:
- Changes to the build script
- Changes to the language-tree-sitter-utils module
- Manual workflow dispatch

## Version Management

Library versions are managed in the main `pom.xml` file:

```xml
<tree-sitter.version>0.25.1</tree-sitter.version>
<tree-sitter-python.version>0.23.6</tree-sitter-python.version>
```

## Troubleshooting

### Common Issues

1. **Missing dependencies**: Ensure you have git, make, and a C compiler installed
2. **Permission denied**: Make sure the build script is executable (`chmod +x scripts/build-native-libraries.sh`)
3. **Build failures**: Check that the repository URLs and versions are correct

### Platform-Specific Notes

- **Linux**: Requires `build-essential` package
- **macOS**: Requires Xcode command line tools (`xcode-select --install`)
- **Windows**: Requires MinGW-w64 (make and gcc)

## Adding New Libraries

To add support for a new Tree-sitter language library:

1. Add the library configuration to `get_library_config()` in `build-native-libraries.sh`
2. Add version property to `pom.xml`
3. Add build execution to the Maven profile
4. Update the CI workflow if needed

Example for a new library:

```bash
"tree-sitter-python")
    REPO_URL="https://github.com/tree-sitter/tree-sitter-python"
    TARGET_DIR="languages/python/src/main/resources/native"
    LIBRARY_NAME="libtree-sitter-python"
    ;;
``` 
