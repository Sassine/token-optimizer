# Passos Finais para Publicar v1.0.0

## ✅ O que já está pronto:

- ✅ Versão 1.0.0 configurada
- ✅ Merge develop → main concluído
- ✅ Tag v1.0.0 criada e pushada
- ✅ JAR buildado: `lib-java/target/token-optimizer-1.0.0.jar`
- ✅ Workflow de publicação Maven configurado
- ✅ pom.xml com metadata completa

## 🚀 Último Passo: Criar Release no GitHub

### Opção 1: Via GitHub CLI (Automático)

**1. Fazer login no GitHub CLI:**

Abra PowerShell e execute:
```powershell
"C:\Program Files\GitHub CLI\gh.exe" auth login
```

Ou se `gh` estiver no PATH:
```powershell
gh auth login
```

Siga as instruções na tela para autenticar.

**2. Criar a release:**

Depois do login, execute:
```powershell
cd C:\Users\Arabe\desenv\token-optimizer
"C:\Program Files\GitHub CLI\gh.exe" release create v1.0.0 `
    --title "v1.0.0 - First Stable Release" `
    --notes-file RELEASE_NOTES.md `
    lib-java\target\token-optimizer-1.0.0.jar
```

### Opção 2: Via GitHub Web (Manual)

1. Acesse: https://github.com/Sassine/token-optimizer/releases/new

2. Preencha:
   - **Tag**: `v1.0.0` (selecione na lista ou digite)
   - **Título**: `v1.0.0 - First Stable Release`
   - **Descrição**: Copie todo o conteúdo de `RELEASE_NOTES.md`

3. Anexar arquivo:
   - Clique em "Attach binaries"
   - Selecione: `lib-java/target/token-optimizer-1.0.0.jar`

4. Marcar como: ✅ "Set as the latest release"

5. Clique em: **"Publish release"**

## 📦 O que acontece depois:

1. **Release criada** → GitHub Actions detecta automaticamente
2. **Workflow executa** → `.github/workflows/publish.yml` será executado
3. **Publicação Maven** → Biblioteca publicada no GitHub Packages
4. **Dependência disponível** → `dev.sassine:token-optimizer:1.0.0`

## 🔍 Verificar Publicação:

Após criar a release, verifique:

- **Release**: https://github.com/Sassine/token-optimizer/releases
- **Workflow**: https://github.com/Sassine/token-optimizer/actions
- **Packages**: https://github.com/Sassine/token-optimizer/packages

## ⏱️ Tempo Estimado:

- Criação da release: Imediato
- Execução do workflow: 2-5 minutos
- Disponibilidade no GitHub Packages: 5-10 minutos

## 📝 Nota:

O workflow de publicação é executado automaticamente quando você cria uma release. Não é necessário fazer nada além de criar a release!

