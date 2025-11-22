# Configuração para Maven Central

## 📋 Diferença entre GitHub Packages e Maven Central

- **GitHub Packages**: Já está funcionando! ✅
  - Requer autenticação para usar
  - Disponível apenas para quem tem acesso ao repositório
  
- **Maven Central**: Precisa de configuração adicional
  - Não requer autenticação (público)
  - Padrão da indústria
  - Processo de aprovação inicial (1-2 dias)

## 🚀 Passos para Publicar no Maven Central

### 1. Registrar no Sonatype OSSRH

**1.1. Criar conta:**
- Acesse: https://issues.sonatype.org/
- Crie uma conta (use o mesmo email do GitHub)

**1.2. Solicitar acesso ao groupId:**
- Abra um ticket: https://issues.sonatype.org/projects/OSSRH/
- Tipo: "New Project"
- Summary: "Publish dev.sassine groupId"
- Description:
  ```
  I would like to publish the following project to Maven Central:
  
  Group ID: dev.sassine
  Project URL: https://github.com/Sassine/token-optimizer
  SCM URL: https://github.com/Sassine/token-optimizer.git
  License: MIT
  ```

**1.3. Verificar domínio (se necessário):**
- Se o groupId for `dev.sassine`, você precisa provar que possui o domínio `sassine.dev`
- OU usar um groupId baseado no GitHub: `io.github.sassine` (mais fácil!)

### 2. Gerar Chave GPG

**2.1. Instalar GPG (se não tiver):**
- Windows: https://www.gpg4win.org/
- Linux: `sudo apt install gnupg`
- Mac: `brew install gnupg`

**2.2. Gerar chave:**
```bash
gpg --gen-key
# Escolha: (1) RSA and RSA
# Tamanho: 4096
# Validade: 0 (sem expiração)
# Nome: Seu Nome
# Email: sassineasmar@gmail.com
# Senha: (crie uma senha forte)
```

**2.3. Exportar e publicar chave:**
```bash
# Listar chaves
gpg --list-keys

# Exportar chave pública (substitua KEY_ID)
gpg --export -a KEY_ID > public-key.asc

# Enviar para servidor de chaves
gpg --keyserver keyserver.ubuntu.com --send-keys KEY_ID
gpg --keyserver pgp.mit.edu --send-keys KEY_ID
```

### 3. Configurar pom.xml

O `pom.xml` já está configurado com os plugins necessários! ✅

### 4. Configurar settings.xml

Adicione ao `~/.m2/settings.xml` (ou `lib-java/settings.xml`):

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>SEU_USUARIO_SONATYPE</username>
      <password>SUA_SENHA_SONATYPE</password>
    </server>
  </servers>
</settings>
```

### 5. Publicar

**Opção A: Manual (local)**
```bash
cd lib-java
mvn clean deploy -P release
```

**Opção B: Automático (GitHub Actions)**
- O workflow será atualizado para publicar no Maven Central também
- Será executado quando você criar uma release

## ⚠️ Importante

1. **Primeira publicação**: Pode levar 1-2 dias para aprovação
2. **Sincronização**: Após aprovação, leva ~2 horas para aparecer no Maven Central
3. **Versões**: Não pode deletar versões publicadas
4. **GPG**: Mantenha sua chave privada segura!

## 🔗 Links Úteis

- [Sonatype OSSRH](https://central.sonatype.org/publish/publish-guide/)
- [GPG Keyserver](https://keyserver.ubuntu.com/)
- [Maven Central Search](https://search.maven.org/)

