@echo off
REM Script para testar publicação no Maven Central localmente

echo 🔐 Teste Local - Maven Central
echo.

REM Solicitar credenciais
set /p OSSRH_USERNAME="Digite seu OSSRH_USERNAME (email): "
set /p OSSRH_PASSWORD="Digite sua OSSRH_PASSWORD: "

echo.
echo 📋 Criando settings.xml temporário...

REM Criar settings.xml
if not exist "%USERPROFILE%\.m2" mkdir "%USERPROFILE%\.m2"

echo ^<?xml version="1.0" encoding="UTF-8"?^> > "%USERPROFILE%\.m2\settings.xml"
echo ^<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"^> >> "%USERPROFILE%\.m2\settings.xml"
echo   ^<servers^> >> "%USERPROFILE%\.m2\settings.xml"
echo     ^<server^> >> "%USERPROFILE%\.m2\settings.xml"
echo       ^<id^>ossrh^</id^> >> "%USERPROFILE%\.m2\settings.xml"
echo       ^<username^>%OSSRH_USERNAME%^</username^> >> "%USERPROFILE%\.m2\settings.xml"
echo       ^<password^>%OSSRH_PASSWORD%^</password^> >> "%USERPROFILE%\.m2\settings.xml"
echo     ^</server^> >> "%USERPROFILE%\.m2\settings.xml"
echo   ^</servers^> >> "%USERPROFILE%\.m2\settings.xml"
echo ^</settings^> >> "%USERPROFILE%\.m2\settings.xml"

echo ✅ Settings.xml criado
echo.

cd lib-java

echo 🚀 Testando publicação...
echo.

REM Testar publicação (dry-run primeiro)
echo Testando com dry-run...
call mvn clean deploy -P central -DskipTests -DdryRun=true

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Teste bem-sucedido! Credenciais estão corretas.
    echo.
    echo Para publicar de verdade, execute:
    echo   mvn clean deploy -P central -DskipTests
) else (
    echo.
    echo ❌ Erro no teste. Verifique as credenciais.
    echo.
    echo Possíveis problemas:
    echo   1. Credenciais incorretas
    echo   2. Plugin pode precisar de token ao invés de usuário/senha
    echo   3. Pode ser necessário usar método tradicional (staging repository)
)

echo.
pause

