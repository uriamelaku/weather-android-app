# 🔧 Build Issue: JAVA_HOME Not Set

## Problem
The Kotlin app cannot be built because **Java Development Kit (JDK) is not installed or JAVA_HOME environment variable is not configured**.

## Error Message
```
ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 
Please set the JAVA_HOME variable in your environment to match the location of your Java installation.
```

## Root Cause
- The app uses **Kotlin** which requires Java
- The **Kotlin plugin** was missing from build.gradle.kts (ALREADY FIXED ✅)
- But Java itself is not installed on this machine

## Solution: Install Java

### Option 1: Install OpenJDK 17+ (Recommended)
1. Download from: **https://adoptium.net/** (Eclipse Adoptium - Free & Open Source)
   - Choose JDK version 17 or newer
   - Download the Windows installer
   
2. Run the installer and select:
   - ✅ Set JAVA_HOME environment variable (IMPORTANT!)
   - ✅ Add Java to PATH

3. Verify installation:
   ```powershell
   java -version
   echo $env:JAVA_HOME
   ```

### Option 2: Install Oracle JDK
1. Download from: **https://www.oracle.com/java/technologies/downloads/**
   - Choose JDK 17 or newer
   
2. Run the installer with default settings

### Option 3: Use Android Studio's Bundled JDK
If you have Android Studio installed:
1. Find the JDK path: Usually at `C:\Program Files\Android\Android Studio\jbr`
2. Set JAVA_HOME manually:
   ```powershell
   [Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Android\Android Studio\jbr", "User")
   ```

### Option 4: Set JAVA_HOME Temporarily (Quick Test)
```powershell
# Windows PowerShell
$env:JAVA_HOME = "C:\Path\To\Your\JDK"
cd C:\Users\User\AndroidStudioProjects\MyKotlinApp
.\gradlew build
```

## After Installing Java

### Verify Java Installation
```powershell
java -version
javac -version
echo $env:JAVA_HOME
```

You should see output like:
```
openjdk version "21.0.1" 2023-10-17
OpenJDK Runtime Environment (build 21.0.1+12-39)
```

### Build the App
```powershell
cd C:\Users\User\AndroidStudioProjects\MyKotlinApp

# Option 1: Use the batch script
.\setup_build.bat

# Option 2: Manual build
.\gradlew clean build

# Option 3: Build APK only
.\gradlew assembleDebug
```

## What Was Fixed

✅ **Already Fixed in build files:**
1. Added `kotlin = "2.0.0"` version to `gradle/libs.versions.toml`
2. Added `kotlin-android` plugin to `gradle/libs.versions.toml`
3. Added `alias(libs.plugins.kotlin.android)` to `app/build.gradle.kts`

❌ **Still Need to Fix (Your Action):**
- Install Java/JDK and set JAVA_HOME environment variable

## Troubleshooting

### If you still get "JAVA_HOME not set" after installation:
1. **Restart your terminal/IDE** (important!)
2. **Restart your computer** (if needed)
3. Verify with: `echo $env:JAVA_HOME`

### If you get "Kotlin compiler not found":
- Run: `.\gradlew --refresh-dependencies`
- Then: `.\gradlew clean build`

### If gradle can't find Android SDK:
- Check `local.properties` - should have: `sdk.dir=C:\\Users\\User\\AppData\\Local\\Android\\Sdk`
- Or download Android SDK through Android Studio

## Next Steps

1. **Install Java** (as described above)
2. **Restart your PowerShell/Terminal**
3. **Run the build:**
   ```powershell
   cd C:\Users\User\AndroidStudioProjects\MyKotlinApp
   .\gradlew clean build
   ```

4. **If build succeeds**, you should see:
   ```
   BUILD SUCCESSFUL in XXs
   ```

---

**Need Help?** Check the error message again after installing Java - it should be more specific about what's missing next.

