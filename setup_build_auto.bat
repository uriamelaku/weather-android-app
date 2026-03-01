@REM Automated Build Setup Script for MyKotlinApp
@REM This script attempts to build the app with automatic JAVA_HOME detection

@echo off
setlocal enabledelayedexpansion

echo.
echo ========================================
echo MyKotlinApp - Build Setup
echo ========================================
echo.

REM Check if java is available
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Java found in PATH
    java -version
    echo.
) else (
    echo ✗ Java NOT found in PATH
    echo.
    echo ERROR: Java is required but not installed.
    echo.
    echo Please install Java first:
    echo 1. Visit: https://adoptium.net/temurin/releases/
    echo 2. Download JDK 21 for Windows x64
    echo 3. Run the installer and check "Set JAVA_HOME"
    echo 4. Restart your terminal
    echo.
    echo For detailed instructions, see: INSTALL_JAVA.md
    echo.
    exit /b 1
)

REM Check GRADLE
echo Checking Gradle wrapper...
if not exist "gradlew.bat" (
    echo ✗ Gradle wrapper not found!
    exit /b 1
)
echo ✓ Gradle wrapper found
echo.

REM Try to build
echo Starting build process...
echo.
call gradlew.bat clean build

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo ✓ BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo APK location:
    echo   app\build\outputs\apk\debug\app-debug.apk
    echo.
) else (
    echo.
    echo ✗ BUILD FAILED
    echo.
    echo Troubleshooting:
    echo 1. Ensure Java is installed: java -version
    echo 2. Ensure JAVA_HOME is set: echo %%JAVA_HOME%%
    echo 3. Try: gradlew clean --refresh-dependencies
    echo.
)

pause

