@echo off
REM Script para criar release no GitHub automaticamente (Windows)
REM Requer: GITHUB_TOKEN como variável de ambiente

set REPO=Sassine/token-optimizer
set TAG=v1.0.0
set VERSION=1.0.0
set JAR_PATH=lib-java\target\token-optimizer-1.0.0.jar

REM Verificar se o token está disponível
if "%GITHUB_TOKEN%"=="" (
    echo ❌ Erro: GITHUB_TOKEN não encontrado
    echo.
    echo Defina a variável de ambiente:
    echo   set GITHUB_TOKEN=seu_token_aqui
    echo.
    echo Para criar um token:
    echo   https://github.com/settings/tokens
    echo   Permissões necessárias: repo (acesso completo aos repositórios)
    exit /b 1
)

echo 🚀 Criando release v1.0.0 no GitHub...
echo.

REM Ler release notes (simplificado para Windows)
set RELEASE_NOTES=RELEASE_NOTES.md

REM Criar release via API usando PowerShell
powershell -Command "$headers = @{'Authorization'='token %GITHUB_TOKEN%'; 'Accept'='application/vnd.github.v3+json'}; $body = Get-Content '%RELEASE_NOTES%' -Raw | ConvertTo-Json; $response = Invoke-RestMethod -Uri 'https://api.github.com/repos/%REPO%/releases' -Method Post -Headers $headers -Body @{tag_name='%TAG%'; name='v%VERSION% - First Stable Release'; body=$body; draft=$false; prerelease=$false} -ContentType 'application/json'; Write-Host '✅ Release criada com sucesso!'; Write-Host '   Release ID:' $response.id; $releaseId = $response.id"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo 🔗 Release disponível em:
    echo    https://github.com/%REPO%/releases/tag/%TAG%
    echo.
    echo 📦 O workflow de publicação Maven será executado automaticamente!
) else (
    echo ❌ Erro ao criar release
    echo    Verifique se a release já existe em:
    echo    https://github.com/%REPO%/releases
)

pause

