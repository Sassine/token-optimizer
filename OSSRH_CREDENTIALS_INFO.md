# 🔐 Informações sobre Credenciais OSSRH

## OSSRH_USERNAME

No Sonatype OSSRH, o `OSSRH_USERNAME` geralmente é:

✅ **O EMAIL usado no cadastro** (mais comum)
- Exemplo: `sassineasmar@gmail.com`
- É o mesmo email que você usa para fazer login no portal

❌ **NÃO é:**
- Nome de usuário/nickname
- Nome completo
- Outro identificador

## Como Verificar

1. **Acesse o portal:**
   - https://oss.sonatype.org/
   - OU https://central.sonatype.com/

2. **Tente fazer login:**
   - Use seu **EMAIL** como username
   - Use sua **SENHA**

3. **Se funcionar:**
   - Esse é o `OSSRH_USERNAME` correto!
   - Use o mesmo email no GitHub Secret

## OSSRH_PASSWORD

É a **senha** que você usa para fazer login no portal Sonatype.

⚠️ **Importante:**
- Se você mudou a senha desde a última publicação (há 3 anos), atualize o secret!
- A senha deve ser a mesma que você usa para fazer login no portal

## Configuração no GitHub Secrets

1. Acesse: https://github.com/Sassine/token-optimizer/settings/secrets/actions

2. Atualize os secrets:
   - **OSSRH_USERNAME**: Seu email (ex: `sassineasmar@gmail.com`)
   - **OSSRH_PASSWORD**: Sua senha do Sonatype

3. Salve e execute o workflow novamente

## Teste Local (Opcional)

Se quiser testar localmente antes:

```bash
cd lib-java

# Criar settings.xml temporário
mkdir -p ~/.m2
cat > ~/.m2/settings.xml <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>SEU_EMAIL_AQUI</username>
      <password>SUA_SENHA_AQUI</password>
    </server>
  </servers>
</settings>
EOF

# Testar publicação (dry-run)
mvn clean deploy -P central -DskipTests -DdryRun=true
```

## Links Úteis

- Sonatype OSSRH Portal: https://oss.sonatype.org/
- Sonatype Central Portal: https://central.sonatype.com/
- Documentação: https://central.sonatype.org/publish/publish-guide/

