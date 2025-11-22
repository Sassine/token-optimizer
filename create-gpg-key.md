# 🔑 Criar Nova Chave GPG

## Passo a Passo

### 1. Gerar a Chave

Execute no terminal:

```bash
gpg --gen-key
```

**Siga as instruções:**

1. **Tipo de chave**: Digite `1` (RSA and RSA)
2. **Tamanho da chave**: Digite `4096`
3. **Validade**: Digite `0` (sem expiração)
4. **Confirmação**: Digite `y`
5. **Nome real**: `Sassine`
6. **Endereço de email**: `sassineasmar@gmail.com`
7. **Comentário**: (pode deixar vazio, pressione Enter)
8. **Confirmação**: Digite `O` (Ok)
9. **Senha**: Crie uma senha forte e **GUARDE BEM** (você vai precisar!)

### 2. Listar e Identificar a Chave

```bash
gpg --list-keys
```

Você verá algo como:
```
pub   rsa4096 2024-01-01 [SC]
      ABC123DEF4567890ABCDEF1234567890ABCDEF12
uid           [ultimate] Sassine <sassineasmar@gmail.com>
```

O `ABC123DEF4567890ABCDEF1234567890ABCDEF12` é o **KEY_ID** (use os últimos 16 caracteres ou o ID completo).

### 3. Publicar a Chave

```bash
# Substitua KEY_ID pelo ID da sua chave
gpg --keyserver keyserver.ubuntu.com --send-keys KEY_ID
gpg --keyserver pgp.mit.edu --send-keys KEY_ID
```

### 4. Exportar Chave Privada (para GitHub Secrets)

```bash
# Substitua KEY_ID pelo ID da sua chave
gpg --export-secret-keys -a KEY_ID > private-key.asc
```

### 5. Verificar o Arquivo

```bash
cat private-key.asc
```

Você deve ver algo como:
```
-----BEGIN PGP PRIVATE KEY BLOCK-----
...
-----END PGP PRIVATE KEY BLOCK-----
```

**⚠️ IMPORTANTE**: Copie TODO o conteúdo (incluindo as linhas `-----BEGIN...` e `-----END...`) para o GitHub Secret `GPG_PRIVATE_KEY`.

## ✅ Próximo Passo

Depois de criar a chave, configure os GitHub Secrets:
- `GPG_PRIVATE_KEY`: Conteúdo completo de `private-key.asc`
- `GPG_PASSPHRASE`: A senha que você criou

