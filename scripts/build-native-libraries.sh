#!/bin/bash

# Build script for Tree-sitter native libraries
# This script builds native libraries for different platforms and architectures
# Usage: ./build-native-libraries.sh <library-name> <version>
# Example: ./build-native-libraries.sh tree-sitter 0.25.1

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if required tools are available
check_dependencies() {
    local missing_deps=()
    
    if ! command -v git &> /dev/null; then
        missing_deps+=("git")
    fi
    
    # Check for build tools based on platform
    if [[ "$PLATFORM" == "windows" ]]; then
        if ! command -v zig &> /dev/null; then
            missing_deps+=("zig")
        fi
    else
        if ! command -v make &> /dev/null; then
            missing_deps+=("make")
        fi
    fi
    
    if [ ${#missing_deps[@]} -ne 0 ]; then
        print_error "Missing required dependencies: ${missing_deps[*]}"
        exit 1
    fi
}

# Detect current platform
detect_platform() {
    local os=$(uname -s | tr '[:upper:]' '[:lower:]')
    local arch=$(uname -m)
    
    case $os in
        "linux")
            PLATFORM="linux"
            ;;
        "darwin")
            PLATFORM="mac"
            ;;
        "msys_nt"*|"cygwin"*|"mingw"*)
            PLATFORM="windows"
            ;;
        *)
            print_error "Unsupported operating system: $os"
            exit 1
            ;;
    esac
    
    case $arch in
        "x86_64")
            ARCH="amd64"
            ;;
        "aarch64"|"arm64")
            ARCH="aarch64"
            ;;
        "i386"|"i686")
            ARCH="i386"
            ;;
        *)
            print_error "Unsupported architecture: $arch"
            exit 1
            ;;
    esac
    
    print_info "Detected platform: $PLATFORM-$ARCH"
}

# Get library configuration
get_library_config() {
    local library_name=$1
    local version=$2
    
    case $library_name in
        "tree-sitter")
            REPO_URL="https://github.com/tree-sitter/tree-sitter"
            TARGET_DIR="language-tree-sitter-utils/src/main/resources/native"
            LIBRARY_NAME="libtree-sitter"
            ;;
        "tree-sitter-python")
            REPO_URL="https://github.com/tree-sitter/tree-sitter-python"
            TARGET_DIR="languages/python/src/main/resources/native"
            LIBRARY_NAME="libtree-sitter-python"
            ;;
        *)
            print_error "Unknown library: $library_name"
            print_info "Supported libraries: tree-sitter, tree-sitter-python"
            exit 1
            ;;
    esac
}

# Build library for current platform
build_library() {
    local library_name=$1
    local version=$2
    local build_dir="build/native/$library_name-$version"
    
    print_info "Building $library_name version $version for $PLATFORM-$ARCH"
    
    # Create build directory
    mkdir -p "$build_dir"
    cd "$build_dir"
    
    # Clone repository if not exists
    if [ ! -d "$library_name" ]; then
        print_info "Cloning $library_name repository..."
        git clone --depth 1 --branch "v$version" "$REPO_URL" "$library_name"
    fi
    
    cd "$library_name"
    
    # Build the library using appropriate build system
    if [[ "$PLATFORM" == "windows" ]]; then
        print_info "Compiling $library_name using Zig..."
        zig build-lib -Doptimize=ReleaseFast --name $LIBRARY_NAME
    else
        print_info "Compiling $library_name using make..."
        make
    fi
    
    # Copy to target directory with version
    local target_path="../../../../$TARGET_DIR/$PLATFORM/$version"
    mkdir -p "$target_path"
    
    case $PLATFORM in
        "linux")
            cp "$LIBRARY_NAME.so" "$target_path/"
            ;;
        "mac")
            cp "$LIBRARY_NAME.dylib" "$target_path/"
            ;;
        "windows")
            # Zig puts output in zig-out/lib/ directory
            if [ -f "zig-out/bin/$LIBRARY_NAME.dll" ]; then
                cp "zig-out/bin/$LIBRARY_NAME.dll" "$target_path/"
            else
                print_error "Could not find compiled library file"
                print_info "Contents of zig-out directory:"
                find zig-out -type f 2>/dev/null || echo "zig-out directory not found"
                exit 1
            fi
            ;;
    esac
    
    print_success "Built $library_name for $PLATFORM-$ARCH"
    
    # Return to project root
    cd ../../../..
}

# Clean up build directories
cleanup_build_directories() {
    print_info "Cleaning up build directories..."
    if [ -d "build/native" ]; then
        print_info "Found build directory, removing..."
        rm -rf build/native/
        if [ -d "build/native" ]; then
            print_error "Failed to remove build directory"
        else
            print_success "Build directories cleaned up"
        fi
    else
        print_info "No build directories to clean up"
    fi
}

# Main execution
main() {
    if [ $# -ne 2 ]; then
        print_error "Usage: $0 <library-name> <version>"
        print_info "Example: $0 tree-sitter 0.25.1"
        exit 1
    fi
    
    local library_name=$1
    local version=$2
    
    print_info "Starting native library build process"
    print_info "Library: $library_name"
    print_info "Version: $version"
    
    # Set up trap to ensure cleanup on exit
    trap cleanup_build_directories EXIT
    
    # Check dependencies
    check_dependencies
    
    # Detect platform
    detect_platform
    
    # Get library configuration
    get_library_config "$library_name" "$version"
    
    # Build library
    build_library "$library_name" "$version"
    
    print_success "Native library build completed successfully"
}

# Run main function with all arguments
main "$@" 
