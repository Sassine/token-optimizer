# Instruções para Publicar v1.0.0

## ✅ O que já está pronto:

- ✅ Versão atualizada para 1.0.0
- ✅ Merge develop → main concluído
- ✅ Tag v1.0.0 criada
- ✅ JAR buildado: `lib-java/target/token-optimizer-1.0.0.jar`
- ✅ Workflow de publicação Maven configurado
- ✅ pom.xml com metadata completa

## 🚀 Criar Release no GitHub

### Opção 1: Script Automático (Recomendado)

**Windows:**
```cmd
set GITHUB_TOKEN=seu_token_github
create-release.bat
```

**Linux/Mac:**
```bash
export GITHUB_TOKEN=seu_token_github
./create-release.sh
```

**Para obter o token:**
1. Acesse: https://github.com/settings/tokens
2. Clique em "Generate new token (classic)"
3. Permissões: `repo` (acesso completo aos repositórios)
4. Copie o token e use como `GITHUB_TOKEN`

### Opção 2: Manual via GitHub Web

1. Acesse: https://github.com/Sassine/token-optimizer/releases/new
2. Preencha:
   - **Tag**: `v1.0.0` (selecione na lista)
   - **Título**: `v1.0.0 - First Stable Release`
   - **Descrição**: Copie o conteúdo de `RELEASE_NOTES.md`
3. Anexar arquivo:
   - `lib-java/target/token-optimizer-1.0.0.jar`
4. Marcar como: "Set as the latest release"
5. Clique em: "Publish release"

## 📦 O que acontece depois:

1. **Release criada** → Workflow de publicação será executado automaticamente
2. **Workflow publica** → Biblioteca disponível no GitHub Packages
3. **Dependência disponível** → `dev.sassine:token-optimizer:1.0.0`

## 🔍 Verificar Publicação:

Após a release ser criada:
- **GitHub Packages**: https://github.com/Sassine/token-optimizer/packages
- **Workflow**: https://github.com/Sassine/token-optimizer/actions
- **Release**: https://github.com/Sassine/token-optimizer/releases

## 📝 Notas:

- O workflow de publicação é executado automaticamente quando você cria uma release
- Pode levar alguns minutos para o pacote aparecer no GitHub Packages
- A dependência ficará disponível em: `https://maven.pkg.github.com/Sassine/token-optimizer`

