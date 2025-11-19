@echo off
REM Script to run Example.java
REM This script should be run from the lib-java directory
REM Note: Example classes are in src/examples/ and are not included in the library JAR

echo Configurando JAVA_HOME para JDK 24...
set JAVA_HOME=C:\Users\Arabe\jdk-24
set PATH=%JAVA_HOME%\bin;%PATH%

echo Java version:
java -version
echo.

echo Compilando o projeto (incluindo exemplos)...
call mvn clean compile

REM Compile examples separately
echo Compilando classes de exemplo...
for /f "delims=" %%i in ('mvn dependency:build-classpath -q -DincludeScope=compile ^| findstr /r ".*"') do set CP=%%i
javac -cp "target/classes;%CP%" -d target/classes src/examples/java/dev/sassine/tokenoptimizer/*.java

if %ERRORLEVEL% NEQ 0 (
    echo Erro na compilacao!
    pause
    exit /b 1
)

echo.
echo Executando Example.java...
echo.
java -cp "target/classes;%CP%" dev.sassine.tokenoptimizer.Example
pause

pause

