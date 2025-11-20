# Release Guide - Token Optimizer v1.0.0

Este guia explica como publicar a primeira versão do Token Optimizer.

## 📋 Pré-requisitos

- [ ] Todos os testes passando
- [ ] Documentação completa
- [ ] README atualizado
- [ ] Código revisado e estável

## 🚀 Passos para Publicação

### 1. Atualizar Versão no pom.xml

Mudar de `0.0.1-SNAPSHOT` para `1.0.0`:

```xml
<version>1.0.0</version>
```

### 2. Fazer Commit e Push

```bash
git add lib-java/pom.xml
git commit -m "chore: Bump version to 1.0.0"
git push origin develop
```

### 3. Merge para main

```bash
git checkout main
git merge develop
git push origin main
```

### 4. Criar Tag de Release

```bash
git tag -a v1.0.0 -m "Release version 1.0.0

First stable release of Token Optimizer library.

Features:
- JSON vs TOON format comparison
- Generic token estimation
- Tiktoken integration for model-specific counting
- 100% TOON spec v2.0 compliant
- Complete documentation site"

git push origin v1.0.0
```

### 5. Criar Release no GitHub

1. Acesse: https://github.com/Sassine/token-optimizer/releases/new
2. Selecione a tag: `v1.0.0`
3. Título: `v1.0.0 - First Stable Release`
4. Descrição: (veja RELEASE_NOTES.md)
5. Anexar arquivos:
   - `lib-java/target/token-optimizer-1.0.0.jar`
   - `lib-java/target/token-optimizer-1.0.0-sources.jar` (se disponível)
6. Marcar como "Latest release"
7. Publicar

### 6. Build do JAR para Release

```bash
cd lib-java
mvn clean package -DskipTests
```

O JAR estará em: `lib-java/target/token-optimizer-1.0.0.jar`

## 📦 Publicação no Maven Central (Opcional)

Para publicar no Maven Central, você precisa:

1. Conta no Sonatype OSSRH
2. GPG key para assinar os artefatos
3. Configuração do pom.xml com maven-deploy-plugin

Isso é um processo mais complexo e pode ser feito em uma versão futura.

## ✅ Checklist Pós-Release

- [ ] Release criada no GitHub
- [ ] Tag criada e pushada
- [ ] JAR anexado na release
- [ ] README atualizado com versão
- [ ] Documentação atualizada
- [ ] Changelog atualizado (se houver)

## 🔄 Próximas Versões

Para próximas versões, seguir o mesmo processo:
- Atualizar versão no pom.xml
- Criar tag
- Criar release no GitHub

