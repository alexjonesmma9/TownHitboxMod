# Town Hitbox Mod - Build Script for PowerShell
# Usage: powershell -ExecutionPolicy Bypass -File Build.ps1

Write-Host "Town Hitbox Mod - Build Script" -ForegroundColor Cyan
Write-Host "================================`n" -ForegroundColor Cyan

# Check Java
Write-Host "Checking Java..." -ForegroundColor Yellow
$javaVersion = & java -version 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Java not found. Please install Java 17+" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Java found: $(($javaVersion | Select-Object -First 1))" -ForegroundColor Green

# Run Gradle build
Write-Host "`nBuilding with Gradle..." -ForegroundColor Yellow
Write-Host "This may take a few minutes on first build...`n" -ForegroundColor Gray

# Use gradle wrapper
$gradlewPath = ".\gradlew.bat"
if (-NOT (Test-Path $gradlewPath)) {
    Write-Host "ERROR: gradlew.bat not found in current directory" -ForegroundColor Red
    exit 1
}

# Build
& cmd /c $gradlewPath build -x test
$buildResult = $LASTEXITCODE

if ($buildResult -eq 0) {
    Write-Host "`n✓ Build successful!" -ForegroundColor Green
    Write-Host "JAR location: build\libs\townhitboxmod-1.0.0.jar" -ForegroundColor Cyan
    Write-Host "`nTo install:"
    Write-Host "1. Copy build\libs\townhitboxmod-1.0.0.jar to your mods folder"
    Write-Host "2. Launch Minecraft with Fabric profile`n" -ForegroundColor Gray
} else {
    Write-Host "`nBuild failed with exit code: $buildResult" -ForegroundColor Red
    exit $buildResult
}
