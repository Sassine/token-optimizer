# 🔐 Instruções para GitHub Secrets

## ✅ Chave GPG Criada

- **KEY_ID**: `BC3670CB86C085FD`
- **Email**: `sassineasmar@gmail.com`
- **Arquivo**: `private-key.asc`

## 📋 Passo a Passo

### 1. Acessar GitHub Secrets

Acesse: https://github.com/Sassine/token-optimizer/settings/secrets/actions

### 2. Adicionar os 4 Secrets

Clique em **"New repository secret"** para cada um:

#### Secret 1: GPG_PRIVATE_KEY

- **Name**: `GPG_PRIVATE_KEY`
- **Secret**: Copie TODO o conteúdo do arquivo `private-key.asc`
  - Inclua as linhas `-----BEGIN PGP PRIVATE KEY BLOCK-----`
  - Inclua todas as linhas do meio
  - Inclua a linha `-----END PGP PRIVATE KEY BLOCK-----`

**Para copiar:**
```bash
cat private-key.asc
```
Copie TUDO que aparecer!

#### Secret 2: GPG_PASSPHRASE

- **Name**: `GPG_PASSPHRASE`
- **Secret**: A senha que você criou ao gerar a chave GPG

#### Secret 3: OSSRH_USERNAME

- **Name**: `OSSRH_USERNAME`
- **Secret**: Seu usuário do Sonatype OSSRH (o mesmo que você usa para `dev.sassine.api`)

#### Secret 4: OSSRH_PASSWORD

- **Name**: `OSSRH_PASSWORD`
- **Secret**: Sua senha do Sonatype OSSRH

## ✅ Após Configurar

Depois de adicionar os 4 secrets, você pode:

1. **Criar uma nova release** (o workflow publicará automaticamente)
2. **OU executar manualmente**: https://github.com/Sassine/token-optimizer/actions/workflows/publish-central.yml

## 🔍 Verificar

Após a publicação, verifique em:
- **Maven Central**: https://search.maven.org/artifact/dev.sassine/token-optimizer
- **Tempo de sincronização**: ~2 horas

