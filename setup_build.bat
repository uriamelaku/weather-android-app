@echo off
REM This script sets up the build environment

REM Try to find JDK in common locations
if exist "C:\Program Files\Java\jdk-21" (
    set JAVA_HOME=C:\Program Files\Java\jdk-21
) else if exist "C:\Program Files\Java\openjdk-21" (
    set JAVA_HOME=C:\Program Files\Java\openjdk-21
) else if exist "C:\Program Files\android-studio\jbr" (
    set JAVA_HOME=C:\Program Files\android-studio\jbr
) else (
    echo ERROR: Java Development Kit (JDK) not found!
    echo.
    echo Please install Java 17+ or set JAVA_HOME environment variable manually.
    echo.
    echo To set JAVA_HOME:
    echo 1. Install JDK from: https://adoptium.net/ or https://www.oracle.com/java/
    echo 2. Then run: setx JAVA_HOME "C:\Path\To\Your\JDK"
    echo.
    pause
    exit /b 1
)

echo JAVA_HOME is set to: %JAVA_HOME%
echo.

REM Run gradle build
call gradlew.bat clean build

pause

