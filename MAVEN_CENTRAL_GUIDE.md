# 🚀 Guia Completo: Publicar no Maven Central

## 📋 Situação Atual

✅ **GitHub Packages**: Já está funcionando!
- Publicado automaticamente quando você cria uma release
- Disponível em: `https://maven.pkg.github.com/Sassine/token-optimizer`

❌ **Maven Central**: Precisa de configuração adicional
- Não vai automaticamente
- Requer registro no Sonatype OSSRH
- Requer chave GPG para assinar artefatos

---

## 🎯 Passo a Passo para Maven Central

### 1️⃣ Registrar no Sonatype OSSRH

**1.1. Criar conta:**
- Acesse: https://issues.sonatype.org/
- Clique em "Sign up" e crie uma conta
- Use o mesmo email do GitHub: `sassineasmar@gmail.com`

**1.2. Solicitar acesso ao groupId:**

⚠️ **IMPORTANTE**: Para o groupId `dev.sassine`, você precisa provar que possui o domínio `sassine.dev`.

**Opção A: Usar groupId baseado no GitHub (RECOMENDADO - Mais fácil!)**
- GroupId: `io.github.sassine`
- Não precisa provar domínio
- Aprovação mais rápida

**Opção B: Usar `dev.sassine`**
- Precisa provar que possui o domínio `sassine.dev`
- Mais complexo

**Como solicitar:**

1. Acesse: https://issues.sonatype.org/projects/OSSRH/
2. Clique em "Create" → "New Project"
3. Preencha:
   - **Summary**: `Publish io.github.sassine groupId` (ou `dev.sassine`)
   - **Description**:
     ```
     I would like to publish the following project to Maven Central:
     
     Group ID: io.github.sassine (ou dev.sassine)
     Project URL: https://github.com/Sassine/token-optimizer
     SCM URL: https://github.com/Sassine/token-optimizer.git
     License: MIT
     ```
   - **Component/s**: `OSSRH`
4. Envie o ticket

**Tempo de aprovação**: 1-2 dias úteis

---

### 2️⃣ Gerar Chave GPG

**2.1. Instalar GPG:**

**Windows:**
- Baixe: https://www.gpg4win.org/
- Instale o Gpg4win

**Linux:**
```bash
sudo apt install gnupg
```

**Mac:**
```bash
brew install gnupg
```

**2.2. Gerar chave GPG:**

```bash
gpg --gen-key
```

Siga as instruções:
- Tipo: `1` (RSA and RSA)
- Tamanho: `4096`
- Validade: `0` (sem expiração)
- Nome: `Sassine`
- Email: `sassineasmar@gmail.com`
- Senha: (crie uma senha forte e guarde!)

**2.3. Listar e exportar chave:**

```bash
# Listar chaves
gpg --list-keys

# Você verá algo como:
# pub   rsa4096 2024-01-01 [SC]
#       ABC123DEF456...
# uid           [ultimate] Sassine <sassineasmar@gmail.com>

# Exportar chave pública (substitua ABC123DEF456... pelo seu KEY_ID)
gpg --export -a ABC123DEF456... > public-key.asc

# Enviar para servidores de chaves
gpg --keyserver keyserver.ubuntu.com --send-keys ABC123DEF456...
gpg --keyserver pgp.mit.edu --send-keys ABC123DEF456...
```

**2.4. Exportar chave privada (para GitHub Secrets):**

```bash
# Exportar chave privada (substitua ABC123DEF456... pelo seu KEY_ID)
gpg --export-secret-keys -a ABC123DEF456... > private-key.asc
```

⚠️ **GUARDE A CHAVE PRIVADA EM SEGREDO!**

---

### 3️⃣ Configurar GitHub Secrets

Para publicar automaticamente via GitHub Actions, você precisa adicionar secrets:

1. Acesse: https://github.com/Sassine/token-optimizer/settings/secrets/actions
2. Clique em "New repository secret"
3. Adicione os seguintes secrets:

**OSSRH_USERNAME**: Seu usuário do Sonatype OSSRH
**OSSRH_PASSWORD**: Sua senha do Sonatype OSSRH
**GPG_PRIVATE_KEY**: Conteúdo do arquivo `private-key.asc` (todo o conteúdo)
**GPG_PASSPHRASE**: A senha que você criou ao gerar a chave GPG

---

### 4️⃣ Atualizar pom.xml (se necessário)

Se você optar por usar `io.github.sassine` em vez de `dev.sassine`, atualize:

```xml
<groupId>io.github.sassine</groupId>
```

O `pom.xml` já está configurado com os plugins necessários! ✅

---

### 5️⃣ Publicar

**Opção A: Automático (GitHub Actions) - RECOMENDADO**

Após configurar os secrets, o workflow `.github/workflows/publish-central.yml` será executado automaticamente quando você criar uma release.

**Opção B: Manual (local)**

```bash
cd lib-java

# Configurar settings.xml com credenciais do Sonatype
# Edite ~/.m2/settings.xml:
# <servers>
#   <server>
#     <id>central</id>
#     <username>SEU_USUARIO</username>
#     <password>SUA_SENHA</password>
#   </server>
# </servers>

# Publicar
mvn clean deploy -P central -DskipTests
```

---

## ⏱️ Timeline Esperado

1. **Registro no Sonatype**: 1-2 dias úteis para aprovação
2. **Primeira publicação**: Após aprovação, leva ~2 horas para sincronizar
3. **Versões futuras**: Publicação imediata (após sincronização)

---

## ✅ Checklist

- [ ] Conta criada no Sonatype OSSRH
- [ ] Ticket aberto solicitando acesso ao groupId
- [ ] Aprovação recebida do Sonatype
- [ ] Chave GPG gerada e publicada
- [ ] Chave privada exportada
- [ ] GitHub Secrets configurados
- [ ] pom.xml atualizado (se mudou groupId)
- [ ] Primeira publicação realizada

---

## 🔗 Links Úteis

- [Sonatype OSSRH](https://central.sonatype.org/publish/publish-guide/)
- [GPG Keyserver](https://keyserver.ubuntu.com/)
- [Maven Central Search](https://search.maven.org/)
- [Documentação Oficial](https://central.sonatype.org/publish/publish-guide/)

---

## 📝 Notas Importantes

1. **Primeira publicação**: Pode levar 1-2 dias para aprovação inicial
2. **Sincronização**: Após publicação, leva ~2 horas para aparecer no Maven Central
3. **Versões**: Não pode deletar versões publicadas
4. **GPG**: Mantenha sua chave privada segura!
5. **groupId**: Se usar `io.github.sassine`, a aprovação é mais rápida

---

## 🎉 Após Publicação

Sua biblioteca estará disponível em:
- **Maven Central**: https://search.maven.org/artifact/io.github.sassine/token-optimizer
- **Dependência**:
  ```xml
  <dependency>
      <groupId>io.github.sassine</groupId>
      <artifactId>token-optimizer</artifactId>
      <version>1.0.0</version>
  </dependency>
  ```

**Sem necessidade de autenticação!** 🚀

