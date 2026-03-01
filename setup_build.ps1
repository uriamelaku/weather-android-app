# PowerShell script to set JAVA_HOME and build the project
# Usage: .\setup_build.ps1

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  MyKotlinApp Build Setup Script" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Check for Java installation
$javaFound = $false
$javaHome = ""

# Try common installation paths
$paths = @(
    "C:\Program Files\Java\jdk-21",
    "C:\Program Files\Java\openjdk-21",
    "C:\Program Files\Java\jdk-17",
    "C:\Program Files\Android\Android Studio\jbr",
    "C:\Program Files (x86)\Java\jdk-21",
    "C:\Program Files (x86)\Java\openjdk-21"
)

foreach ($path in $paths) {
    if (Test-Path "$path\bin\java.exe") {
        $javaHome = $path
        $javaFound = $true
        Write-Host "✓ Found Java at: $javaHome" -ForegroundColor Green
        break
    }
}

if (-not $javaFound) {
    Write-Host "✗ Java Development Kit (JDK) not found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install JDK 17 or newer:" -ForegroundColor Yellow
    Write-Host "  • https://adoptium.net/ (Recommended - Free & Open Source)" -ForegroundColor Gray
    Write-Host "  • https://www.oracle.com/java/technologies/downloads/" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Make sure to check 'Set JAVA_HOME' during installation!" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter after installing Java"
    exit 1
}

# Set JAVA_HOME for this session
$env:JAVA_HOME = $javaHome
Write-Host "✓ JAVA_HOME set to: $env:JAVA_HOME" -ForegroundColor Green

# Verify Java works
Write-Host ""
Write-Host "Verifying Java installation..." -ForegroundColor Cyan
& "$javaHome\bin\java.exe" -version 2>&1

Write-Host ""
Write-Host "Starting build..." -ForegroundColor Cyan
Write-Host ""

# Run gradle build
Set-Location (Split-Path -Parent $MyInvocation.MyCommand.Path)
& ".\gradlew.bat" clean build

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✓ BUILD SUCCESSFUL!" -ForegroundColor Green
    Write-Host ""
    Write-Host "APK location:" -ForegroundColor Cyan
    Write-Host "  app/build/outputs/apk/debug/app-debug.apk" -ForegroundColor Gray
} else {
    Write-Host ""
    Write-Host "✗ BUILD FAILED!" -ForegroundColor Red
    Write-Host "Check the error messages above." -ForegroundColor Yellow
}

Read-Host "Press Enter to exit"

