# ========================================================
# COMPILADOR AUTOMATICO ADIVINA QUIEN (WINDOWS / POWERSHELL)
# ========================================================
$ErrorActionPreference = "Stop"

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "   COMPILANDO ADIVINA QUIEN (CLIENTE Y SERVIDOR)" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

# 1. Buscar en JDK local portable
$javac = $null
if (Test-Path "$projectRoot\jdk\bin\javac.exe") {
    $javac = "$projectRoot\jdk\bin\javac.exe"
}

# 2. Buscar javac en PATH
if (-not $javac) {
    $javacCmd = Get-Command javac -ErrorAction SilentlyContinue
    if ($javacCmd) {
        $javac = $javacCmd.Source
    }
}

# 3. Buscar en carpetas estandar
if (-not $javac) {
    $searchPaths = @(
        "$env:ProgramFiles\Java\jdk*",
        "$env:ProgramFiles\Microsoft\jdk*",
        "$env:ProgramFiles\Eclipse Adoptium\jdk*",
        "$env:LOCALAPPDATA\Programs\Eclipse Adoptium\jdk*"
    )
    foreach ($p in $searchPaths) {
        $found = Get-ChildItem -Path $p -Filter "javac.exe" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found) {
            $javac = $found.FullName
            break
        }
    }
}

if (-not $javac -or -not (Test-Path $javac)) {
    Write-Host "[ERROR] No se pudo encontrar javac. Instala Java JDK desde https://adoptium.net/" -ForegroundColor Red
    exit 1
}

Write-Host "[OK] Compilador encontrado: $javac" -ForegroundColor Green

# Crear carpeta bin
$binDir = Join-Path $projectRoot "bin"
if (-not (Test-Path $binDir)) {
    New-Item -ItemType Directory -Path $binDir | Out-Null
}

# Obtener todos los archivos Java
$javaFiles = Get-ChildItem -Path (Join-Path $projectRoot "src") -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
$sourcesFile = Join-Path $projectRoot "sources.txt"

# Escribir sources.txt SIN BOM
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllLines($sourcesFile, [string[]]$javaFiles, $utf8NoBom)

$mysqlJar = Join-Path $projectRoot "XAMMP_JAR\mysql-connector-j-9.2.0\mysql-connector-j-9.2.0.jar"
$cp = ".;$mysqlJar"

Write-Host "Compilando $($javaFiles.Count) archivos Java..." -ForegroundColor Cyan
& $javac -encoding UTF-8 -cp $cp -d $binDir "@$sourcesFile"

if (Test-Path $sourcesFile) {
    Remove-Item $sourcesFile -Force
}

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "[OK] Compilación completada con éxito en la carpeta 'bin'!" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "[ERROR] Hubo un error durante la compilación." -ForegroundColor Red
}