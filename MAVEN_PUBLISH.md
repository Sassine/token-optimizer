# Publicação no Maven

Este guia explica como publicar a biblioteca Token Optimizer no Maven para que a dependência `dev.sassine:token-optimizer:1.0.0` fique acessível.

## 📦 Opções de Publicação

### Opção 1: GitHub Packages (Recomendado - Mais Simples)

O GitHub Packages permite publicar pacotes Maven diretamente no seu repositório GitHub.

#### Vantagens:
- ✅ Configuração simples
- ✅ Integração nativa com GitHub
- ✅ Automatizado via GitHub Actions
- ✅ Gratuito para repositórios públicos

#### Como Usar:

**1. Publicação Automática (via GitHub Actions)**

Quando você criar uma release no GitHub, o workflow `.github/workflows/publish.yml` publicará automaticamente.

**2. Publicação Manual**

```bash
cd lib-java

# Configurar token do GitHub
export GITHUB_TOKEN=seu_token_github

# Publicar
mvn clean deploy -DskipTests -s settings.xml
```

**3. Usar a Dependência**

Adicione ao seu `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/Sassine/token-optimizer</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.sassine</groupId>
        <artifactId>token-optimizer</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

**Autenticação:**

Para usar a dependência, você precisa de um Personal Access Token do GitHub com permissão `read:packages`.

Adicione ao `~/.m2/settings.xml`:

```xml
<servers>
    <server>
        <id>github</id>
        <username>SEU_USUARIO</username>
        <password>SEU_TOKEN</password>
    </server>
</servers>
```

---

### Opção 2: Maven Central (Mais Complexo)

Para publicar no Maven Central (repositório oficial), você precisa:

#### Pré-requisitos:
1. Conta no Sonatype OSSRH
2. Solicitar acesso ao groupId `dev.sassine`
3. Chave GPG para assinar artefatos
4. Configuração adicional no pom.xml

#### Passos:

**1. Registrar no Sonatype OSSRH**

- Acesse: https://issues.sonatype.org/
- Crie uma conta
- Abra um ticket solicitando acesso ao groupId `dev.sassine`
- Forneça informações do projeto

**2. Gerar Chave GPG**

```bash
# Gerar chave
gpg --gen-key

# Exportar chave pública
gpg --export -a "Seu Nome" > public-key.asc

# Enviar para servidor de chaves
gpg --keyserver hkp://keyserver.ubuntu.com --send-keys SEU_KEY_ID
```

**3. Configurar pom.xml**

Adicionar plugins de assinatura e deploy:

```xml
<build>
    <plugins>
        <!-- ... plugins existentes ... -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-gpg-plugin</artifactId>
            <version>3.1.0</version>
            <executions>
                <execution>
                    <id>sign-artifacts</id>
                    <phase>verify</phase>
                    <goals>
                        <goal>sign</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        <plugin>
            <groupId>org.sonatype.central</groupId>
            <artifactId>central-publishing-maven-plugin</artifactId>
            <version>0.6.0</version>
            <extensions>true</extensions>
            <configuration>
                <publishingServerId>central</publishingServerId>
                <autoPublish>true</autoPublish>
                <waitUntil>published</waitUntil>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**4. Configurar settings.xml**

Adicionar credenciais do Sonatype:

```xml
<servers>
    <server>
        <id>central</id>
        <username>seu-usuario-sonatype</username>
        <password>sua-senha-sonatype</password>
    </server>
</servers>
```

**5. Publicar**

```bash
mvn clean deploy
```

---

## 🚀 Publicação Automática (GitHub Actions)

O workflow `.github/workflows/publish.yml` está configurado para publicar automaticamente quando você criar uma release no GitHub.

### Como Funciona:

1. Crie uma release no GitHub (ex: v1.0.0)
2. O workflow será executado automaticamente
3. A biblioteca será publicada no GitHub Packages
4. A dependência ficará disponível em: `https://maven.pkg.github.com/Sassine/token-optimizer`

### Verificar Publicação:

Após a publicação, você pode verificar em:
- GitHub: https://github.com/Sassine/token-optimizer/packages
- Ou acessar: https://maven.pkg.github.com/Sassine/token-optimizer/dev/sassine/token-optimizer/

---

## 📝 Notas Importantes

1. **GitHub Packages**: Requer autenticação para baixar dependências (mesmo para repositórios públicos)
2. **Maven Central**: Não requer autenticação, mas o processo de aprovação pode levar alguns dias
3. **Versões**: Use versionamento semântico (1.0.0, 1.0.1, 1.1.0, etc.)
4. **SNAPSHOT**: Versões SNAPSHOT não devem ser publicadas em releases

---

## 🔗 Links Úteis

- [GitHub Packages Documentation](https://docs.github.com/en/packages)
- [Maven Central Publishing Guide](https://central.sonatype.org/publish/publish-guide/)
- [Sonatype OSSRH](https://central.sonatype.org/publish/requirements/coordinates/)

