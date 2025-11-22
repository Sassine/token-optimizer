#!/bin/bash
# Script para configurar GPG para Maven Central

echo "🔐 Configuração GPG para Maven Central"
echo ""

# Verificar se já tem chaves
echo "📋 Verificando chaves existentes..."
gpg --list-keys

echo ""
read -p "Você já tem uma chave GPG do projeto anterior (dev.sassine.api)? (s/n) " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Ss]$ ]]; then
    echo "✅ Você pode reutilizar a chave existente!"
    echo ""
    echo "Para exportar a chave privada:"
    echo "1. Liste as chaves: gpg --list-keys"
    echo "2. Exporte a chave privada: gpg --export-secret-keys -a SEU_KEY_ID > private-key.asc"
    echo "3. Adicione o conteúdo de private-key.asc ao GitHub Secret GPG_PRIVATE_KEY"
else
    echo "🔑 Vamos criar uma nova chave GPG..."
    echo ""
    echo "Execute:"
    echo "  gpg --gen-key"
    echo ""
    echo "Siga as instruções:"
    echo "  - Tipo: 1 (RSA and RSA)"
    echo "  - Tamanho: 4096"
    echo "  - Validade: 0 (sem expiração)"
    echo "  - Nome: Sassine"
    echo "  - Email: sassineasmar@gmail.com"
    echo "  - Senha: (crie uma senha forte)"
    echo ""
    echo "Depois de criar:"
    echo "1. Liste: gpg --list-keys"
    echo "2. Publique: gpg --keyserver keyserver.ubuntu.com --send-keys SEU_KEY_ID"
    echo "3. Exporte: gpg --export-secret-keys -a SEU_KEY_ID > private-key.asc"
fi

