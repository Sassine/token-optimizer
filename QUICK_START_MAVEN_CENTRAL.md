# ⚡ Quick Start - Maven Central

Como você já tem acesso ao `dev.sassine.*`, só precisa configurar:

## 🔑 1. Chave GPG

**Opção A: Reutilizar chave antiga (se tiver backup)**
```bash
# Se você tem o arquivo da chave privada antiga:
gpg --import private-key-antiga.asc
```

**Opção B: Criar nova chave**
```bash
gpg --gen-key
# Tipo: 1 (RSA and RSA)
# Tamanho: 4096
# Validade: 0
# Nome: Sassine
# Email: sassineasmar@gmail.com

# Listar para pegar KEY_ID
gpg --list-keys

# Publicar (substitua KEY_ID)
gpg --keyserver keyserver.ubuntu.com --send-keys KEY_ID

# Exportar chave privada
gpg --export-secret-keys -a KEY_ID > private-key.asc
```

## 🔐 2. GitHub Secrets

Acesse: https://github.com/Sassine/token-optimizer/settings/secrets/actions

Adicione:
- **OSSRH_USERNAME**: Seu usuário Sonatype
- **OSSRH_PASSWORD**: Sua senha Sonatype
- **GPG_PRIVATE_KEY**: Conteúdo completo de `private-key.asc`
- **GPG_PASSPHRASE**: Senha da chave GPG

## 🚀 3. Publicar

**Automático (recomendado):**
- Crie uma nova release OU
- Execute manualmente: https://github.com/Sassine/token-optimizer/actions/workflows/publish-central.yml

**Manual (local):**
```bash
cd lib-java
mvn clean deploy -P central -DskipTests
```

## ✅ Pronto!

Após ~2 horas, estará em: https://search.maven.org/artifact/dev.sassine/token-optimizer

