#!/bin/bash

# Script para criar repositório no GitHub e fazer push
# Uso: ./setup-github.sh <nome-do-repositorio> [descricao]

REPO_NAME=${1:-"token-optimizer"}
DESCRIPTION=${2:-"Java library to optimize payload format for minimum token consumption"}

echo "🚀 Configurando repositório GitHub: $REPO_NAME"
echo ""

# Verificar se gh CLI está instalado
if command -v gh &> /dev/null; then
    echo "✅ GitHub CLI encontrado"
    echo ""
    echo "Criando repositório no GitHub..."
    gh repo create "$REPO_NAME" \
        --public \
        --description "$DESCRIPTION" \
        --source=. \
        --remote=origin \
        --push
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ Repositório criado e código enviado com sucesso!"
        echo "🌐 URL: https://github.com/$(gh api user --jq .login)/$REPO_NAME"
    else
        echo "❌ Erro ao criar repositório"
        exit 1
    fi
else
    echo "⚠️  GitHub CLI não encontrado"
    echo ""
    echo "Opção 1: Instalar GitHub CLI (recomendado)"
    echo "  Windows: https://cli.github.com/"
    echo "  Linux/Mac: brew install gh ou apt install gh"
    echo ""
    echo "Opção 2: Criar manualmente no GitHub:"
    echo "  1. Acesse: https://github.com/new"
    echo "  2. Nome do repositório: $REPO_NAME"
    echo "  3. Descrição: $DESCRIPTION"
    echo "  4. Público/Privado (escolha)"
    echo "  5. NÃO marque 'Initialize with README'"
    echo "  6. Clique em 'Create repository'"
    echo ""
    echo "Depois execute:"
    echo "  git remote add origin https://github.com/SEU_USUARIO/$REPO_NAME.git"
    echo "  git branch -M main"
    echo "  git push -u origin main"
fi

