# Script automático para criar release no GitHub
# Executa login se necessário e cria a release

$REPO = "Sassine/token-optimizer"
$TAG = "v1.0.0"
$VERSION = "1.0.0"
$JAR_PATH = "lib-java\target\token-optimizer-1.0.0.jar"
$RELEASE_NOTES = "RELEASE_NOTES.md"
$GH_PATH = "C:\Program Files\GitHub CLI\gh.exe"

Write-Host "🚀 Criando release v1.0.0 no GitHub..." -ForegroundColor Cyan
Write-Host ""

# Verificar se o JAR existe
if (-not (Test-Path $JAR_PATH)) {
    Write-Host "❌ Erro: JAR não encontrado em $JAR_PATH" -ForegroundColor Red
    exit 1
}

# Verificar autenticação
Write-Host "🔐 Verificando autenticação..." -ForegroundColor Cyan
$authCheck = & $GH_PATH auth status 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "⚠️  Não autenticado. Executando login..." -ForegroundColor Yellow
    Write-Host "   Siga as instruções na tela para fazer login" -ForegroundColor Yellow
    & $GH_PATH auth login
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Falha na autenticação" -ForegroundColor Red
        exit 1
    }
}

# Criar release
Write-Host "📦 Criando release..." -ForegroundColor Cyan
& $GH_PATH release create $TAG `
    --title "v$VERSION - First Stable Release" `
    --notes-file $RELEASE_NOTES `
    $JAR_PATH

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ Release criada com sucesso!" -ForegroundColor Green
    Write-Host ""
    Write-Host "🔗 Release: https://github.com/$REPO/releases/tag/$TAG" -ForegroundColor Blue
    Write-Host ""
    Write-Host "📦 O workflow de publicação será executado automaticamente!" -ForegroundColor Cyan
    Write-Host "   Verifique em: https://github.com/$REPO/actions" -ForegroundColor Blue
} else {
    Write-Host ""
    Write-Host "❌ Erro ao criar release" -ForegroundColor Red
    Write-Host "   Verifique se a release já existe ou se há problemas" -ForegroundColor Yellow
}

