#!/bin/bash
# Setup JavaFX SDK for Windows (Git Bash)
# - Downloads Windows JavaFX SDK zip (JARs + native DLLs)
# - Installs to lib/javafx-sdk/{lib,bin} under current workspace
# - Compatible with Git Bash on Windows

set -euo pipefail

VERSION="11.0.2"
WORKSPACE_ROOT="$(pwd)"
DEST_ROOT="$WORKSPACE_ROOT/lib/javafx-sdk"
DEST_LIB="$DEST_ROOT/lib"
DEST_BIN="$DEST_ROOT/bin"

ZIP_URL="https://download2.gluonhq.com/openjfx/${VERSION}/openjfx-${VERSION}_windows-x64_bin-sdk.zip"
TMP_DIR="$(mktemp -d)"
ZIP_FILE="$TMP_DIR/javafx-${VERSION}.zip"

echo "Downloading JavaFX SDK $VERSION (Windows x64)..."
curl -L -o "$ZIP_FILE" "$ZIP_URL"

echo "Extracting JavaFX SDK..."
unzip -q -d "$TMP_DIR" "$ZIP_FILE"

# Find extracted SDK directory
SDK_DIR="$(find "$TMP_DIR" -maxdepth 2 -type d -name "javafx-sdk*" | head -n 1)"
if [[ -z "$SDK_DIR" ]]; then
	echo "Error: JavaFX SDK directory not found after extraction." >&2
	exit 1
fi

echo "Preparing destination: $DEST_ROOT"
mkdir -p "$DEST_LIB" "$DEST_BIN"

echo "Copying JARs to $DEST_LIB"
cp "$SDK_DIR/lib"/*.jar "$DEST_LIB/"

echo "Copying native DLLs to $DEST_BIN"
cp "$SDK_DIR/bin"/*.dll "$DEST_BIN/"

echo "Cleaning up temp files..."
rm -rf "$TMP_DIR"

echo "JavaFX SDK $VERSION installed successfully."
echo "  JARs: $DEST_LIB"
echo "  DLLs: $DEST_BIN"

echo "Next steps:"
echo "  - Use VS Code Run config \"Run MainApp with JavaFX\""
echo "  - Or run from PowerShell with:"
echo "      java -Djava.library.path=\"$DEST_BIN\" --module-path \"$DEST_LIB\" --add-modules javafx.controls,javafx.fxml -cp \"bin;lib/*;$DEST_LIB/*\" app.MainApp"
