# KodeLib Setup Guide for Windows 11

A simple step-by-step guide to get the app running on any Windows 11 laptop.

## Prerequisites
- **Java 11+** installed (check by opening PowerShell and typing `java -version`)
- **VS Code** with Extension Pack for Java installed
- **Git Bash** (comes with Git for Windows, which you can download online)

## Step 1: Install JavaFX SDK (One-Time Setup)

1. Right click on the KodeLib-FoP-WIX1002-main folder, then click 'Show more options'
2. Click open **Git Bash** in the project root folder, then type the code below and press enter:

```bash
./setup-javafx.sh
```

This downloads and installs JavaFX SDK (JARs + native DLLs) to `lib/javafx-sdk/`.

> **Note:** This is a one-time setup. You only need to do this once per device.

---

## Step 2: Run the App in VS Code (Easiest)

1. Open the project folder in VS Code.
2. Click **Run and Debug** on the left sidebar (or press `Ctrl+Shift+D`).
3. Select **"Run MainApp (SDK path)"** from the dropdown at the top.
4. Click the **green play button** (▶) or press `F5`.

The app should launch in a new window.

---

## Step 3: Alternative - Run from PowerShell (Terminal) *risky option, proceed with caution*

If you prefer the Powershell terminal or VS Code doesn't work at all:

```powershell
# Navigate to the project root
Set-Location "C:\path\to\KodeLib-FoP-WIX1002-main"

# Compile
$sdk = "$PWD\lib\javafx-sdk"
$sources = Get-ChildItem -Recurse -Path "src\main\java" -Filter *.java | ForEach-Object { $_.FullName }
javac -d "bin" -cp "lib\*;$sdk\lib\*;src\main\java" $sources

# Run
java -Djava.library.path="$sdk\bin" --module-path "$sdk\lib" --add-modules javafx.controls,javafx.fxml -cp "bin;lib\*;$sdk\lib\*" app.MainApp
```

---

## Troubleshooting

### "Module javafx.controls not found"
- Confirm `lib/javafx-sdk/lib/` exists and contains `javafx.controls.jar`.
- Run the setup script again: `./setup-javafx.sh`

### "Error initializing QuantumRenderer"
- Confirm `lib/javafx-sdk/bin/` exists and contains DLL files (e.g., `prism_sw.dll`).
- If the setup script failed, re-run it or manually download JavaFX from [gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/).

### "Could not find or load main class"
- Ensure you compiled with `javac` first (see Step 3).
- Confirm `bin/` folder exists and contains compiled classes.

### Java version mismatch
- Java 11 requires JavaFX 11. Java 17+ requires JavaFX 17+.
- Check: `java -version` and match the JavaFX download version.

---

## File Structure of `lib` Folder After Setup

```
lib/
  ├── javafx-sdk/          ← Created by setup script (contains everything needed)
  │   ├── lib/             ← JARs
  │   │   ├── javafx.base.jar
  │   │   ├── javafx.controls.jar
  │   │   ├── javafx.fxml.jar
  │   │   └── ...
  │   └── bin/             ← Native DLLs (required for Windows)
  │       ├── prism_sw.dll
  │       ├── glass.dll
  │       └── ...
  ├── jackson-annotations.jar
  ├── jackson-core.jar
  └── jackson-databind.jar
```

> **Note:** Only `javafx-sdk/` is needed. Any `javafx/` folder (if it exists) is redundant and can be safely deleted.

---

## Summary

1. Right-click on `KodeLib-FoP-WIX1002-main` folder then click "Show more options".
2. Click "Open Git Bash here" and then run `./setup-javafx.sh`.
3. Open VS Code → Run and Debug → "Run MainApp (SDK path)" → Press F5 or click the green ▶ button.
4. Done! The app should launch.

For help, see **Troubleshooting** above.