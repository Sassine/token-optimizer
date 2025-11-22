@echo off
REM Script para configurar GPG para Maven Central (Windows)

echo 🔐 Configuração GPG para Maven Central
echo.

REM Verificar se já tem chaves
echo 📋 Verificando chaves existentes...
gpg --list-keys

echo.
echo Você já tem uma chave GPG do projeto anterior (dev.sassine.api)?
echo.
echo Se SIM:
echo   1. Liste as chaves: gpg --list-keys
echo   2. Exporte a chave privada: gpg --export-secret-keys -a SEU_KEY_ID ^> private-key.asc
echo   3. Adicione o conteúdo de private-key.asc ao GitHub Secret GPG_PRIVATE_KEY
echo.
echo Se NÃO, vamos criar uma nova:
echo   1. Execute: gpg --gen-key
echo   2. Siga as instruções:
echo      - Tipo: 1 (RSA and RSA)
echo      - Tamanho: 4096
echo      - Validade: 0 (sem expiração)
echo      - Nome: Sassine
echo      - Email: sassineasmar@gmail.com
echo      - Senha: (crie uma senha forte)
echo   3. Depois de criar:
echo      - Liste: gpg --list-keys
echo      - Publique: gpg --keyserver keyserver.ubuntu.com --send-keys SEU_KEY_ID
echo      - Exporte: gpg --export-secret-keys -a SEU_KEY_ID ^> private-key.asc
echo.

pause

