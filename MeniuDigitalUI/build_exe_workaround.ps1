# PowerShell Script to build .exe for MeniuDigitalUI
# Bypasses jlink limits by using jpackage directly with classpath.

# 1. Setup
$java_home = "$env:JAVA_HOME"
if (-not $java_home) {
    Write-Host "Error: JAVA_HOME is not set." -ForegroundColor Red
    exit 1
}
$jpackage = Join-Path $java_home "bin\jpackage.exe"
if (-not (Test-Path $jpackage)) {
    Write-Host "Error: jpackage not found at $jpackage" -ForegroundColor Red
    exit 1
}

Write-Host "Found jpackage at: $jpackage" -ForegroundColor Green

# 2. Build and Package
Write-Host "Building project with Maven..." -ForegroundColor Cyan
.\mvnw clean package -DskipTests
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# 3. Copy Dependencies
Write-Host "Copying dependencies..." -ForegroundColor Cyan
.\mvnw dependency:copy-dependencies -DoutputDirectory=target/libs
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# 4. Prepare Input Directory
# jpackage needs a directory with all jars (main + libs)
$inputDir = "target\jpackage-input"
if (Test-Path $inputDir) { Remove-Item -Recurse -Force $inputDir }
New-Item -ItemType Directory -Path $inputDir | Out-Null

Copy-Item "target\MeniuDigitalUI-1.0-SNAPSHOT.jar" -Destination $inputDir
Copy-Item "target\libs\*" -Destination $inputDir

# 5. Run jpackage
# Configuration:
# Name: RestaurantPOS (from user request)
# Main Class: unitbv.devops.meniudigitalui.RestaurantApp (from pom.xml original, user snippet had wrong package)
Write-Host "Running jpackage..." -ForegroundColor Cyan

& $jpackage `
    --name "RestaurantPOS" `
    --type app-image `
    --input $inputDir `
    --main-jar "MeniuDigitalUI-1.0-SNAPSHOT.jar" `
    --main-class "unitbv.devops.meniudigitalui.Launcher" `
    --dest "target/dist" `
    --vendor "UnitBV DevOps" `
    --app-version "1.0.0" `
    --win-console

if ($LASTEXITCODE -eq 0) {
    Write-Host "SUCCESS! Application image created at target/dist/RestaurantPOS" -ForegroundColor Green
    Write-Host "You can run it via: target\dist\RestaurantPOS\RestaurantPOS.exe"
}
else {
    Write-Host "jpackage failed." -ForegroundColor Red
}
