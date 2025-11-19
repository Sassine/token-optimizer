@echo off
REM Script para criar repositório no GitHub e fazer push
REM Uso: setup-github.bat [nome-do-repositorio] [descricao]

set REPO_NAME=%~1
if "%REPO_NAME%"=="" set REPO_NAME=token-optimizer

set DESCRIPTION=%~2
if "%DESCRIPTION%"=="" set DESCRIPTION=Java library to optimize payload format for minimum token consumption

echo 🚀 Configurando repositório GitHub: %REPO_NAME%
echo.

REM Verificar se gh CLI está instalado
where gh >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo ✅ GitHub CLI encontrado
    echo.
    echo Criando repositório no GitHub...
    gh repo create %REPO_NAME% --public --description "%DESCRIPTION%" --source=. --remote=origin --push
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo ✅ Repositório criado e código enviado com sucesso!
    ) else (
        echo ❌ Erro ao criar repositório
        exit /b 1
    )
) else (
    echo ⚠️  GitHub CLI não encontrado
    echo.
    echo Opção 1: Instalar GitHub CLI (recomendado)
    echo   Download: https://cli.github.com/
    echo.
    echo Opção 2: Criar manualmente no GitHub:
    echo   1. Acesse: https://github.com/new
    echo   2. Nome do repositório: %REPO_NAME%
    echo   3. Descrição: %DESCRIPTION%
    echo   4. Público/Privado (escolha)
    echo   5. NÃO marque 'Initialize with README'
    echo   6. Clique em 'Create repository'
    echo.
    echo Depois execute:
    echo   git remote add origin https://github.com/SEU_USUARIO/%REPO_NAME%.git
    echo   git branch -M main
    echo   git push -u origin main
)

pause

