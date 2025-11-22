# 🚀 Setup Maven Central - Token Optimizer

## ✅ Situação

Você já tem:
- ✅ Conta no Sonatype OSSRH
- ✅ Acesso ao namespace `dev.sassine.*`
- ✅ Experiência anterior (publicou `dev.sassine.api` há 3 anos)

## 📋 Checklist Rápido

### 1. Verificar/Configurar Chave GPG

**Se você já tem uma chave GPG do projeto anterior:**

```bash
# Listar chaves existentes
gpg --list-keys

# Se encontrar uma chave com seu email (sassineasmar@gmail.com), pode reutilizar!
# Verificar se está publicada:
gpg --keyserver keyserver.ubuntu.com --search-keys sassineasmar@gmail.com
```

**Se não tiver ou quiser criar uma nova:**

```bash
# Gerar nova chave
gpg --gen-key

# Seguir instruções:
# - Tipo: 1 (RSA and RSA)
# - Tamanho: 4096
# - Validade: 0 (sem expiração)
# - Nome: Sassine
# - Email: sassineasmar@gmail.com
# - Senha: (criar uma forte)

# Listar para pegar o KEY_ID
gpg --list-keys

# Publicar chave
gpg --keyserver keyserver.ubuntu.com --send-keys SEU_KEY_ID
gpg --keyserver pgp.mit.edu --send-keys SEU_KEY_ID

# Exportar chave privada (para GitHub Secrets)
gpg --export-secret-keys -a SEU_KEY_ID > private-key.asc
```

### 2. Configurar GitHub Secrets

Acesse: https://github.com/Sassine/token-optimizer/settings/secrets/actions

Adicione os seguintes secrets:

**OSSRH_USERNAME**: Seu usuário do Sonatype OSSRH
**OSSRH_PASSWORD**: Sua senha do Sonatype OSSRH
**GPG_PRIVATE_KEY**: Conteúdo do arquivo `private-key.asc` (todo o conteúdo, incluindo `-----BEGIN PGP PRIVATE KEY BLOCK-----` e `-----END PGP PRIVATE KEY BLOCK-----`)
**GPG_PASSPHRASE**: A senha da chave GPG

### 3. Verificar pom.xml

O `pom.xml` já está configurado com:
- ✅ GroupId: `dev.sassine` (você tem acesso!)
- ✅ Plugins GPG e Central Publishing
- ✅ Profiles para GitHub Packages e Maven Central

### 4. Publicar

**Opção A: Automático (GitHub Actions) - RECOMENDADO**

Após configurar os secrets, crie uma nova release ou execute manualmente:

1. Acesse: https://github.com/Sassine/token-optimizer/actions
2. Selecione o workflow "Publish to Maven Central"
3. Clique em "Run workflow"

**Opção B: Manual (local)**

```bash
cd lib-java

# Configurar ~/.m2/settings.xml:
# <servers>
#   <server>
#     <id>central</id>
#     <username>SEU_USUARIO_SONATYPE</username>
#     <password>SUA_SENHA_SONATYPE</password>
#   </server>
# </servers>

# Publicar
mvn clean deploy -P central -DskipTests
```

## ⚠️ Notas Importantes

1. **Chave GPG**: Se você já tem uma chave do projeto anterior, pode reutilizar! Só precisa exportar novamente para os secrets.

2. **Credenciais Sonatype**: Use as mesmas credenciais do projeto anterior (`dev.sassine.api`).

3. **Primeira publicação**: Como você já tem acesso, a publicação deve ser mais rápida (não precisa de aprovação inicial).

4. **Sincronização**: Após publicação, leva ~2 horas para aparecer no Maven Central.

## 🔗 Links Úteis

- Sonatype OSSRH: https://oss.sonatype.org/
- Maven Central Search: https://search.maven.org/
- Seu projeto anterior: https://search.maven.org/search?q=g:dev.sassine

## ✅ Após Publicação

Sua biblioteca estará disponível em:
- **Maven Central**: https://search.maven.org/artifact/dev.sassine/token-optimizer
- **Dependência**:
  ```xml
  <dependency>
      <groupId>dev.sassine</groupId>
      <artifactId>token-optimizer</artifactId>
      <version>1.0.0</version>
  </dependency>
  ```

