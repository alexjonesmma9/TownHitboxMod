# Build Town Hitbox Mod - Pure PowerShell Build Script
# No CMD, no admin needed - runs on restricted systems

Write-Host "Building Town Hitbox Mod..." -ForegroundColor Cyan

$appHome = Split-Path -Path $MyInvocation.MyCommand.Definition
Set-Location $appHome

# Gradle wrapper JAR
$wrapperJar = Join-Path $appHome "gradle\wrapper\gradle-wrapper.jar"

if (-not (Test-Path $wrapperJar)) {
    Write-Host "ERROR: gradle-wrapper.jar not found" -ForegroundColor Red
    exit 1
}

Write-Host "Building with Java..." -ForegroundColor Gray
& java -classpath $wrapperJar org.gradle.wrapper.GradleWrapperMain build -x test

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "SUCCESS!" -ForegroundColor Green
    Write-Host "JAR: build\libs\townhitboxmod-1.0.0.jar" -ForegroundColor Cyan
}
else {
    Write-Host ""
    Write-Host "Build failed (code $LASTEXITCODE)" -ForegroundColor Red
    exit $LASTEXITCODE
}
