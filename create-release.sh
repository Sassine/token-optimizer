#!/bin/bash

# Script para criar release no GitHub automaticamente
# Requer: GITHUB_TOKEN como variável de ambiente ou arquivo .github_token

REPO="Sassine/token-optimizer"
TAG="v1.0.0"
VERSION="1.0.0"
JAR_PATH="lib-java/target/token-optimizer-1.0.0.jar"

# Verificar se o token está disponível
if [ -z "$GITHUB_TOKEN" ]; then
    if [ -f .github_token ]; then
        GITHUB_TOKEN=$(cat .github_token)
    else
        echo "❌ Erro: GITHUB_TOKEN não encontrado"
        echo ""
        echo "Opção 1: Definir variável de ambiente:"
        echo "  export GITHUB_TOKEN=seu_token_aqui"
        echo ""
        echo "Opção 2: Criar arquivo .github_token com o token"
        echo "  echo 'seu_token_aqui' > .github_token"
        echo ""
        echo "Para criar um token:"
        echo "  https://github.com/settings/tokens"
        echo "  Permissões necessárias: repo (acesso completo aos repositórios)"
        exit 1
    fi
fi

echo "🚀 Criando release v1.0.0 no GitHub..."
echo ""

# Ler release notes
RELEASE_NOTES=$(cat RELEASE_NOTES.md)

# Criar release via API
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
  -H "Authorization: token $GITHUB_TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  "https://api.github.com/repos/$REPO/releases" \
  -d "{
    \"tag_name\": \"$TAG\",
    \"name\": \"v$VERSION - First Stable Release\",
    \"body\": $(echo "$RELEASE_NOTES" | jq -Rs .),
    \"draft\": false,
    \"prerelease\": false
  }")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RELEASE_NOTES" | jq -Rs .)

if [ "$HTTP_CODE" = "201" ]; then
    echo "✅ Release criada com sucesso!"
    RELEASE_ID=$(echo "$RESPONSE" | head -n-1 | jq -r '.id')
    echo "   Release ID: $RELEASE_ID"
    echo ""
    
    # Upload do JAR
    if [ -f "$JAR_PATH" ]; then
        echo "📦 Fazendo upload do JAR..."
        UPLOAD_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
          -H "Authorization: token $GITHUB_TOKEN" \
          -H "Content-Type: application/java-archive" \
          --data-binary "@$JAR_PATH" \
          "https://uploads.github.com/repos/$REPO/releases/$RELEASE_ID/assets?name=token-optimizer-$VERSION.jar")
        
        UPLOAD_CODE=$(echo "$UPLOAD_RESPONSE" | tail -n1)
        if [ "$UPLOAD_CODE" = "201" ]; then
            echo "✅ JAR anexado com sucesso!"
        else
            echo "⚠️  Erro ao anexar JAR (código: $UPLOAD_CODE)"
            echo "   Você pode anexar manualmente em:"
            echo "   https://github.com/$REPO/releases/edit/$RELEASE_ID"
        fi
    else
        echo "⚠️  JAR não encontrado em $JAR_PATH"
        echo "   Build o JAR primeiro: cd lib-java && mvn clean package -DskipTests"
    fi
    
    echo ""
    echo "🔗 Release disponível em:"
    echo "   https://github.com/$REPO/releases/tag/$TAG"
    echo ""
    echo "📦 O workflow de publicação Maven será executado automaticamente!"
else
    echo "❌ Erro ao criar release (código HTTP: $HTTP_CODE)"
    echo ""
    if [ "$HTTP_CODE" = "422" ]; then
        echo "   A release pode já existir. Verifique em:"
        echo "   https://github.com/$REPO/releases"
    fi
    exit 1
fi

