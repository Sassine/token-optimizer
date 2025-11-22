# PowerShell script para criar release no GitHub
# Uso: .\create-release-ps1.ps1

$REPO = "Sassine/token-optimizer"
$TAG = "v1.0.0"
$VERSION = "1.0.0"
$JAR_PATH = "lib-java\target\token-optimizer-1.0.0.jar"
$RELEASE_NOTES = "RELEASE_NOTES.md"

Write-Host "🚀 Criando release v1.0.0 no GitHub..." -ForegroundColor Cyan
Write-Host ""

# Verificar se o JAR existe
if (-not (Test-Path $JAR_PATH)) {
    Write-Host "❌ Erro: JAR não encontrado em $JAR_PATH" -ForegroundColor Red
    Write-Host "   Build o JAR primeiro: cd lib-java && mvn clean package -DskipTests" -ForegroundColor Yellow
    exit 1
}

# Verificar se o GitHub CLI está disponível
$ghPath = Get-Command gh -ErrorAction SilentlyContinue
if (-not $ghPath) {
    Write-Host "❌ GitHub CLI não encontrado no PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "Opções:" -ForegroundColor Yellow
    Write-Host "1. Reiniciar o terminal após instalação"
    Write-Host "2. Adicionar ao PATH manualmente"
    Write-Host "3. Usar o caminho completo do gh.exe"
    Write-Host ""
    Write-Host "Ou criar manualmente em:" -ForegroundColor Cyan
    Write-Host "   https://github.com/$REPO/releases/new" -ForegroundColor Blue
    exit 1
}

# Verificar autenticação
Write-Host "🔐 Verificando autenticação..." -ForegroundColor Cyan
$authStatus = gh auth status 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "⚠️  Não autenticado. Executando gh auth login..." -ForegroundColor Yellow
    gh auth login
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Falha na autenticação" -ForegroundColor Red
        exit 1
    }
}

# Ler release notes
$notes = Get-Content $RELEASE_NOTES -Raw

# Criar release
Write-Host "📦 Criando release..." -ForegroundColor Cyan
gh release create $TAG `
    --title "v$VERSION - First Stable Release" `
    --notes $notes `
    $JAR_PATH

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ Release criada com sucesso!" -ForegroundColor Green
    Write-Host ""
    Write-Host "🔗 Release: https://github.com/$REPO/releases/tag/$TAG" -ForegroundColor Blue
    Write-Host ""
    Write-Host "📦 Próximos passos:" -ForegroundColor Cyan
    Write-Host "  1. O workflow de publicação será executado automaticamente"
    Write-Host "  2. Aguarde alguns minutos para o pacote aparecer no GitHub Packages"
    Write-Host "  3. Verifique em: https://github.com/$REPO/packages"
} else {
    Write-Host ""
    Write-Host "❌ Erro ao criar release" -ForegroundColor Red
    Write-Host "   Verifique se a release já existe ou se há problemas de autenticação"
}

