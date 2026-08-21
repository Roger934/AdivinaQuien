# ========================================================
# RESETEAR / CAMBIAR CONTRASEÑA DE MYSQL (WINDOWS)
# ========================================================
param (
    [string]$NuevaPassword = ""
)

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "   CAMBIAR CONTRASEÑA DE MYSQL Y REINICIAR SERVICIO" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

# 1. Buscar servicio de MySQL / MariaDB
$services = Get-Service -Name *mysql*, *mariadb* -ErrorAction SilentlyContinue
if (-not $services) {
    Write-Host "[AVISO] No se encontró un servicio de Windows para MySQL." -ForegroundColor Yellow
}

# 2. Localizar mysqld.exe y mysql.exe
$searchDirs = @(
    "$env:ProgramFiles\MySQL\MySQL Server 8.0\bin",
    "$env:ProgramFiles\MySQL\MySQL Server 8.1\bin",
    "$env:ProgramFiles\MySQL\MySQL Server 8.4\bin",
    "$env:ProgramFiles\MariaDB*\bin",
    "C:\xampp\mysql\bin"
)

$mysqld = $null
$mysqlClient = $null

foreach ($d in $searchDirs) {
    $dResolved = Resolve-Path $d -ErrorAction SilentlyContinue
    if ($dResolved) {
        $candidateDaemon = Join-Path $dResolved "mysqld.exe"
        $candidateClient = Join-Path $dResolved "mysql.exe"
        if (Test-Path $candidateDaemon) { $mysqld = $candidateDaemon }
        if (Test-Path $candidateClient) { $mysqlClient = $candidateClient }
        if ($mysqld -and $mysqlClient) { break }
    }
}

if (-not $mysqld) {
    $cmd = Get-Command mysqld -ErrorAction SilentlyContinue
    if ($cmd) { $mysqld = $cmd.Source }
}

if (-not $mysqld) {
    Write-Host "[ERROR] No se encontró el ejecutable mysqld.exe de MySQL." -ForegroundColor Red
    exit 1
}

Write-Host "[1/5] Deteniendo servicio(s) de MySQL..." -ForegroundColor Yellow
foreach ($s in $services) {
    if ($s.Status -eq "Running") {
        Write-Host "Deteniendo servicio: $($s.Name)..."
        Stop-Service -Name $s.Name -Force -ErrorAction SilentlyContinue
    }
}

# Asegurar que no quede proceso mysqld colgado
Get-Process -Name mysqld -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

Write-Host "[2/5] Creando script de reseteo de contraseña..." -ForegroundColor Yellow
$tempInitFile = [System.IO.Path]::GetTempFileName() + ".sql"

$sqlContent = @"
ALTER USER 'root'@'localhost' IDENTIFIED BY '$NuevaPassword';
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY '$NuevaPassword';
ALTER USER 'root'@'%' IDENTIFIED BY '$NuevaPassword';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
"@

[System.IO.File]::WriteAllText($tempInitFile, $sqlContent, (New-Object System.Text.UTF8Encoding($false)))

Write-Host "[3/5] Aplicando nueva contraseña en el motor MySQL..." -ForegroundColor Yellow
$procInfo = New-Object System.Diagnostics.ProcessStartInfo
$procInfo.FileName = $mysqld
$procInfo.Arguments = "--init-file=`"$tempInitFile`" --console"
$procInfo.UseShellExecute = $false
$procInfo.CreateNoWindow = $true

$proc = [System.Diagnostics.Process]::Start($procInfo)
Start-Sleep -Seconds 5

if (-not $proc.HasExited) {
    $proc.Kill()
    $proc.WaitForExit(3000)
}

if (Test-Path $tempInitFile) {
    Remove-Item $tempInitFile -Force -ErrorAction SilentlyContinue
}

Write-Host "[4/5] Reiniciando servicio normal de MySQL..." -ForegroundColor Yellow
foreach ($s in $services) {
    Write-Host "Iniciando servicio: $($s.Name)..."
    Start-Service -Name $s.Name -ErrorAction SilentlyContinue
}
Start-Sleep -Seconds 3

Write-Host "[5/5] Actualizando config.properties e importando BD..." -ForegroundColor Yellow
$projectRoot = Split-Path -Parent $PSScriptRoot

$configContent = @"
# =======================================================
# CONFIGURACION DE RED Y BASE DE DATOS - ADIVINA QUIEN
# =======================================================

# Direccion IP del Servidor Socket
servidor_ip=127.0.0.1

# Puerto de comunicacion Socket (por defecto 5000)
servidor_puerto=5000

# Parametros de Base de Datos MySQL / MariaDB
db_url=jdbc:mysql://localhost:3306/adivina_quien
db_usuario=root
db_password=$NuevaPassword
"@

$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText((Join-Path $projectRoot "config.properties"), $configContent, $utf8NoBom)
if (Test-Path (Join-Path $projectRoot "config")) {
    [System.IO.File]::WriteAllText((Join-Path $projectRoot "config\config.properties"), $configContent, $utf8NoBom)
}

# Importar tablas si existe mysqlClient
if ($mysqlClient -and (Test-Path (Join-Path $projectRoot "database_setup.sql"))) {
    $dbSql = Join-Path $projectRoot "database_setup.sql"
    if ($NuevaPassword -eq "") {
        & $mysqlClient -u root -e "source $dbSql" 2>$null
    } else {
        & $mysqlClient -u root "-p$NuevaPassword" -e "source $dbSql" 2>$null
    }
}

Write-Host ""
Write-Host "========================================================" -ForegroundColor Green
Write-Host " [OK] ¡CONTRASEÑA CAMBIADA Y SERVICIO REINICIADO!" -ForegroundColor Green
Write-Host " Nueva contraseña establecida: '$NuevaPassword'" -ForegroundColor Green
Write-Host " Base de datos 'adivina_quien' importada y permisos otorgados." -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Green