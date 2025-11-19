# TOON Format Comparison - Frontend

Projeto front-end e servidor para comparar nossa implementação Java com a biblioteca TOON oficial.

## 🚀 Como usar

### 1. Instalar dependências

```bash
cd frontend
npm install
```

### 2. Iniciar o servidor TOON (OBRIGATÓRIO para comparação)

O servidor usa a biblioteca TOON oficial do npm:

```bash
npm run server
```

O servidor irá:
- ✅ Rodar na porta 3000
- ✅ Usar a biblioteca `@toon-format/toon` oficial
- ✅ Expor endpoint `/api/toon/convert` para conversão
- ✅ Expor endpoint `/health` para verificação

### 3. Interface Web (opcional)

Para a interface web de comparação:

```bash
npm start
# ou
npx http-server . -p 8080 -o
```

Acesse `http://localhost:8080` no navegador.

## 📡 API Endpoints

### POST `/api/toon/convert`

Converte JSON para TOON usando a biblioteca oficial.

**Request:**
```json
{
  "json": {
    "key": "value"
  }
}
```

**Response:**
```json
{
  "success": true,
  "toon": "key: value",
  "json": "{\"key\":\"value\"}"
}
```

### GET `/health`

Verifica se o servidor está rodando.

**Response:**
```json
{
  "status": "ok",
  "library": "official-toon"
}
```

## 🔗 Integração com Java

O código Java (`Example.java`) automaticamente:
- ✅ Verifica se o servidor está disponível
- ✅ Chama o endpoint `/api/toon/convert` para cada exemplo
- ✅ Compara nossa implementação com a oficial
- ✅ Mostra diferenças se houver

**Para usar:**
1. Inicie o servidor: `npm run server`
2. Execute o Example.java: `mvn compile exec:java -Dexec.mainClass="dev.sassine.tokenoptimizer.Example"`

## 📋 Funcionalidades

- ✅ Usa biblioteca TOON oficial (`@toon-format/toon`)
- ✅ API REST para conversão
- ✅ Interface web para comparação visual
- ✅ Integração automática com código Java
- ✅ Comparação lado a lado dos resultados

