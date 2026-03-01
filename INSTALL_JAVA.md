# 🚀 Installation Guide: Java for MyKotlinApp

## Current Status
❌ **Java is NOT installed** - This is required to build and run the Kotlin app

## Quick Start (3 Steps)

### Step 1: Download Java
Visit: **https://adoptium.net/temurin/releases/**
- Choose **JDK 21** (or 17+)
- Select **Windows** → **x64** → **.msi Installer**
- Click **Download**

### Step 2: Install Java
1. Run the downloaded `.msi` file
2. Click through the installer (keep defaults)
3. **IMPORTANT:** When asked, ensure these are checked:
   - ☑️ "Set JAVA_HOME environment variable"
   - ☑️ "Add to PATH"
4. Click **Finish**

### Step 3: Restart and Verify
1. **Close ALL terminal windows and VS Code**
2. **Open a NEW PowerShell window**
3. Run this command:
   ```powershell
   java -version
   ```
4. You should see output like:
   ```
   openjdk version "21.0.1" 2023-10-17
   OpenJDK Runtime Environment Temurin-21.0.1+12
   ```

## Manual JAVA_HOME Setup (If Above Doesn't Work)

If the installer doesn't set JAVA_HOME, do this:

### Windows PowerShell
```powershell
# Find Java installation (if you know the path)
# Common path: C:\Program Files\Eclipse Adoptium\jdk-21.0.1+12
# Or: C:\Program Files\Java\jdk-21

# Set JAVA_HOME (replace path if different)
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-21.0.1+12", "User")

# Verify (restart PowerShell first)
echo $env:JAVA_HOME
```

## Troubleshooting

### "java not found" after installation?
- Close and **restart your terminal completely**
- Open a **new PowerShell window**
- Try `java -version` again

### Can't find Java installation path?
```powershell
# This will search for java.exe
Get-ChildItem -Path "C:\Program Files" -Recurse -Filter "java.exe" -ErrorAction SilentlyContinue
```

### Java installed but JAVA_HOME still not set?
```powershell
# Find the JDK folder (you'll see it from the search above)
# Then manually set it:
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\YOUR\JAVA\PATH\HERE", "User")

# Verify
echo $env:JAVA_HOME
java -version
```

---

## ✅ Once Java is Installed

Run the app with:
```powershell
cd C:\Users\User\AndroidStudioProjects\MyKotlinApp
.\gradlew clean build
```

Or use the setup script:
```powershell
.\setup_build.ps1
```

---

**Need Help?** Check these docs:
- `BUILD_SETUP_GUIDE.md` - Detailed setup instructions
- `BUILD_FIX_SUMMARY.md` - What was fixed

