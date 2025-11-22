#!/bin/bash
# Script para testar publicação no Maven Central localmente

echo "🔐 Teste Local - Maven Central"
echo ""

# Solicitar credenciais
read -p "Digite seu OSSRH_USERNAME (email): " OSSRH_USERNAME
read -s -p "Digite sua OSSRH_PASSWORD: " OSSRH_PASSWORD
echo ""

echo "📋 Criando settings.xml temporário..."

# Criar settings.xml
mkdir -p ~/.m2

cat > ~/.m2/settings.xml <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
  <servers>
    <server>
      <id>ossrh</id>
      <username>$OSSRH_USERNAME</username>
      <password>$OSSRH_PASSWORD</password>
    </server>
  </servers>
</settings>
EOF

echo "✅ Settings.xml criado"
echo ""

cd lib-java

echo "🚀 Testando publicação..."
echo ""

# Testar publicação (dry-run primeiro)
echo "Testando com dry-run..."
mvn clean deploy -P central -DskipTests -DdryRun=true

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Teste bem-sucedido! Credenciais estão corretas."
    echo ""
    echo "Para publicar de verdade, execute:"
    echo "  mvn clean deploy -P central -DskipTests"
else
    echo ""
    echo "❌ Erro no teste. Verifique as credenciais."
    echo ""
    echo "Possíveis problemas:"
    echo "  1. Credenciais incorretas"
    echo "  2. Plugin pode precisar de token ao invés de usuário/senha"
    echo "  3. Pode ser necessário usar método tradicional (staging repository)"
fi

