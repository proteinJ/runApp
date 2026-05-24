$PROJECT_DIR = "C:\Users\sshuser\IdeaProjects\runApp"  # 실제 프로젝트 경로로 변경
$JAR_NAME = "runApp-0.0.1-SNAPSHOT.jar"

Write-Host "[$(Get-Date)] Deploy started..." -ForegroundColor Cyan

# 1. Git pull
Set-Location $PROJECT_DIR
git pull origin main

if ($LASTEXITCODE -ne 0) {
    Write-Host "Git pull FAILED!" -ForegroundColor Red
    exit 1
}

# 2. .env 파일 읽어서 환경변수 설정
Write-Host "Loading .env..." -ForegroundColor Yellow
Get-Content "$PROJECT_DIR\.env" | ForEach-Object {
    if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
        $key = $matches[1].Trim()
        $value = $matches[2].Trim().Trim('"')  # 따옴표 자동 제거
        [System.Environment]::SetEnvironmentVariable($key, $value, "Process")
        Write-Host "  SET $key" -ForegroundColor Gray
    }
}

# 3. Gradle 빌드
Write-Host "Building..." -ForegroundColor Yellow
& "$PROJECT_DIR\gradlew.bat" clean bootJar

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build FAILED!" -ForegroundColor Red
    exit 1
}

# 4. 기존 프로세스 종료
$oldProcess = Get-Process -Name "java" -ErrorAction SilentlyContinue
if ($oldProcess) {
    Write-Host "Stopping old process (PID: $($oldProcess.Id))..." -ForegroundColor Yellow
    Stop-Process -Id $oldProcess.Id -Force
    Start-Sleep -Seconds 3
}

# 5. JAR 실행
$jarPath = "$PROJECT_DIR\build\libs\$JAR_NAME"
Write-Host "Starting: $jarPath" -ForegroundColor Yellow

Start-Process -FilePath "java" `
    -ArgumentList "-jar $jarPath" `
    -WorkingDirectory $PROJECT_DIR

Write-Host "[$(Get-Date)] Deploy complete!" -ForegroundColor Green