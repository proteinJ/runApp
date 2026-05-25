$PROJECT_DIR = "C:\Users\sshuser\IdeaProjects\runApp"
$JAR_NAME = "runApp-0.0.1-SNAPSHOT.jar"

Write-Host "[$(Get-Date)] Deploy started..." -ForegroundColor Cyan

# 1. Git pull
Set-Location $PROJECT_DIR
git pull origin develop

if ($LASTEXITCODE -ne 0) {
    Write-Host "Git pull FAILED!" -ForegroundColor Red
    exit 1
}

# 2. .env 파일 읽어서 환경변수 설정
Write-Host "Loading .env..." -ForegroundColor Yellow
$envVars = @{}
Get-Content "$PROJECT_DIR\.env" | ForEach-Object {
    if ($_ -match '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*)$') {
        $key = $matches[1].Trim()
        $value = $matches[2].Trim().Trim('"')
        $envVars[$key] = $value
        [System.Environment]::SetEnvironmentVariable($key, $value, "Process")
        Write-Host "  SET $key" -ForegroundColor Gray
    }
}

# 3. Docker 컨테이너 확인 및 시작
Write-Host "Checking Docker containers..." -ForegroundColor Yellow

$postgres = docker ps --filter "name=postgres-container" --format "{{.Names}}"
if (-not $postgres) {
    Write-Host "Starting postgres-container..." -ForegroundColor Yellow
    docker start postgres-container
    Start-Sleep -Seconds 5
} else {
    Write-Host "postgres-container already running" -ForegroundColor Gray
}

$redis = docker ps --filter "name=redis-container" --format "{{.Names}}"
if (-not $redis) {
    Write-Host "Starting redis-container..." -ForegroundColor Yellow
    docker start redis-container
    Start-Sleep -Seconds 3
} else {
    Write-Host "redis-container already running" -ForegroundColor Gray
}

# 4. Gradle 빌드
Write-Host "Building..." -ForegroundColor Yellow
& "$PROJECT_DIR\gradlew.bat" clean bootJar

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build FAILED!" -ForegroundColor Red
    exit 1
}

# 5. 기존 프로세스 종료 (포트 8080으로 정확하게 찾기)
Write-Host "Stopping old process..." -ForegroundColor Yellow
$portProcess = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue |
    Where-Object State -eq "Listen" |
    Select-Object -ExpandProperty OwningProcess

if ($portProcess) {
    Write-Host "Stopping process on port 8080 (PID: $portProcess)..." -ForegroundColor Yellow
    Stop-Process -Id $portProcess -Force
    Start-Sleep -Seconds 3
} else {
    Write-Host "No process found on port 8080" -ForegroundColor Gray
}

# 6. JAR 실행 (환경변수 직접 전달)
$jarPath = "$PROJECT_DIR\build\libs\$JAR_NAME"
Write-Host "Starting: $jarPath" -ForegroundColor Yellow

$psi = New-Object System.Diagnostics.ProcessStartInfo
$psi.FileName = "java"
$psi.Arguments = "-jar `"$jarPath`""
$psi.WorkingDirectory = $PROJECT_DIR
$psi.UseShellExecute = $true

foreach ($key in $envVars.Keys) {
    $psi.EnvironmentVariables[$key] = $envVars[$key]
}

[System.Diagnostics.Process]::Start($psi) | Out-Null

Write-Host "[$(Get-Date)] Deploy complete!" -ForegroundColor Green