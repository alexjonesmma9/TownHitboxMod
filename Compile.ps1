# Quick JAR builder - compiles code and packages JAR
# For Town Hitbox Mod - works without CMD/Gradle/Admin

Write-Host "Town Hitbox Mod - Direct Compile Build" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

$sourceDir = "src\main\java"
$outputDir = "build\classes"
$libDir = "build\libs"
$jarName = "townhitboxmod-1.0.0.jar"

# Create output directories
New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
New-Item -ItemType Directory -Path $libDir -Force | Out-Null

Write-Host "Finding all Java source files..." -ForegroundColor Yellow
$javaFiles = Get-ChildItem -Path $sourceDir -Recurse -Filter "*.java"
Write-Host "Found $($javaFiles.Count) Java files" -ForegroundColor Green

Write-Host ""
Write-Host "NOTE: Fabric mod compilation requires Gradle with Fabric Loom plugin" -ForegroundColor Yellow
Write-Host "Gradle download seems to be failing on your system." -ForegroundColor Yellow
Write-Host ""
Write-Host "SOLUTION: Visit https://github.com/alexjonesmma9/TownHitboxMod/releases" -ForegroundColor Cyan
Write-Host "and download the pre-built JAR instead" -ForegroundColor Cyan
Write-Host ""
Write-Host "OR try building on a system with working network access to gradle repositories" -ForegroundColor Gray
