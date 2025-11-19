// Import our TOON converter
import { ToonConverter } from './toon-converter.js';

// TOON library loader (try to load official library)
let toonLibrary = null;
let usingOfficialLibrary = false;

// Load TOON library
async function loadToonLibrary() {
    try {
        // Try to load from CDN
        const module = await import('https://cdn.jsdelivr.net/npm/@toon-format/toon@latest/dist/index.esm.js');
        toonLibrary = module.toon || module.default || module;
        usingOfficialLibrary = true;
        console.log('✅ Biblioteca TOON oficial carregada');
        return true;
    } catch (error) {
        console.warn('⚠️ Biblioteca TOON oficial não disponível, usando implementação local:', error);
        // Use our implementation
        toonLibrary = {
            stringify: (obj) => ToonConverter.stringify(obj)
        };
        usingOfficialLibrary = false;
        return false;
    }
}

// Examples from our Java Example.java
const examples = [
    {
        name: "Example 1: Simple Object",
        json: {
            name: "John",
            age: 30,
            city: "New York"
        }
    },
    {
        name: "Example 2: Blog with Posts",
        json: {
            blog: {
                posts: [
                    {
                        id: "post-1",
                        title: "Getting Started with JSON",
                        author: "Jane Doe",
                        publishedAt: "2025-01-10",
                        tags: ["tutorial", "json", "beginner"],
                        comments: 15,
                        likes: 42
                    },
                    {
                        id: "post-2",
                        title: "Advanced Data Structures",
                        author: "John Smith",
                        publishedAt: "2025-01-12",
                        tags: ["advanced", "algorithms", "data-structures"],
                        comments: 8,
                        likes: 28
                    }
                ],
                analytics: {
                    totalViews: 1250,
                    uniqueVisitors: 890
                }
            }
        }
    },
    {
        name: "Example 3: Complex Object",
        json: {
            name: "Carl",
            age: 35,
            address: {
                street: "Main Street",
                number: 123,
                zipCode: "12345-678"
            }
        }
    },
    {
        name: "Example 4: Array of Objects",
        json: {
            metrics: Array.from({ length: 25 }, (_, i) => ({
                applicationPerformanceMonitoringMetricIdentifier: String(i + 1),
                applicationPerformanceMonitoringMetricValue: i + 1,
                applicationPerformanceMonitoringMetricTimestamp: i + 1,
                applicationPerformanceMonitoringMetricType: "T",
                applicationPerformanceMonitoringMetricStatus: "A",
                applicationPerformanceMonitoringMetricCategory: "C"
            }))
        }
    }
];

// Count tokens (simple approximation)
function countTokens(text) {
    if (!text) return 0;
    const charBased = Math.ceil(text.length / 4.0);
    const words = text.trim().split(/\s+/).filter(w => w.length > 0);
    const wordBased = Math.ceil(words.length / 0.75);
    return Math.floor((charBased + wordBased) / 2);
}

// Format JSON for display
function formatJSON(obj) {
    return JSON.stringify(obj, null, 2);
}

// Compare two TOON strings (normalize whitespace)
function compareToon(ourToon, officialToon) {
    const normalize = (str) => str.replace(/\s+/g, ' ').trim();
    return normalize(ourToon) === normalize(officialToon);
}

// Render example comparison
function renderExample(example, index) {
    const container = document.getElementById('examples-container');
    
    try {
        // Convert to TOON using official library
        const officialToon = toonLibrary.stringify(example.json);
        const officialToonTokens = countTokens(officialToon);
        
        // Get JSON string
        const jsonString = JSON.stringify(example.json);
        const jsonTokens = countTokens(jsonString);
        
        // Create example section
        const section = document.createElement('div');
        section.className = 'example-section';
        section.innerHTML = `
            <div class="example-title">
                ${example.name}
                <span class="status-badge status-match">
                    ${window.usingOfficialLibrary ? '✓ TOON Oficial' : '✓ TOON Local'}
                </span>
            </div>
            
            <div class="comparison-grid">
                <div class="comparison-box">
                    <h3>📄 JSON (Original)</h3>
                    <div class="json-output">${formatJSON(example.json)}</div>
                </div>
                <div class="comparison-box">
                    <h3>🎨 TOON (Oficial)</h3>
                    <div class="toon-output">${officialToon || 'Erro na conversão'}</div>
                </div>
            </div>
            
            <div class="stats">
                <div class="stat-box">
                    <div class="stat-label">JSON Tokens</div>
                    <div class="stat-value">${jsonTokens}</div>
                </div>
                <div class="stat-box">
                    <div class="stat-label">TOON Tokens</div>
                    <div class="stat-value">${officialToonTokens}</div>
                </div>
                <div class="stat-box">
                    <div class="stat-label">Economia</div>
                    <div class="stat-value ${officialToonTokens < jsonTokens ? 'match' : 'diff'}">
                        ${jsonTokens - officialToonTokens}
                    </div>
                </div>
                <div class="stat-box">
                    <div class="stat-label">% Economia</div>
                    <div class="stat-value ${officialToonTokens < jsonTokens ? 'match' : 'diff'}">
                        ${jsonTokens > 0 ? ((jsonTokens - officialToonTokens) / jsonTokens * 100).toFixed(2) : 0}%
                    </div>
                </div>
            </div>
        `;
        
        container.appendChild(section);
    } catch (error) {
        const section = document.createElement('div');
        section.className = 'example-section';
        section.innerHTML = `
            <div class="example-title">${example.name}</div>
            <div class="error">Erro: ${error.message}</div>
        `;
        container.appendChild(section);
    }
}

// Test custom JSON
function testCustom() {
    const customJsonText = document.getElementById('custom-json').value;
    const resultDiv = document.getElementById('custom-result');
    
    try {
        const customJson = JSON.parse(customJsonText);
        const officialToon = toonLibrary.stringify(customJson);
        const jsonString = JSON.stringify(customJson);
        
        const jsonTokens = countTokens(jsonString);
        const toonTokens = countTokens(officialToon);
        
        resultDiv.innerHTML = `
            <div class="comparison-grid" style="margin-top: 15px;">
                <div class="comparison-box">
                    <h3>📄 JSON</h3>
                    <div class="json-output">${jsonString}</div>
                    <div style="margin-top: 10px; font-weight: bold;">Tokens: ${jsonTokens}</div>
                </div>
                <div class="comparison-box">
                    <h3>🎨 TOON (Oficial)</h3>
                    <div class="toon-output">${officialToon}</div>
                    <div style="margin-top: 10px; font-weight: bold;">Tokens: ${toonTokens}</div>
                </div>
            </div>
            <div class="stats" style="margin-top: 15px;">
                <div class="stat-box">
                    <div class="stat-label">Economia</div>
                    <div class="stat-value ${toonTokens < jsonTokens ? 'match' : 'diff'}">
                        ${jsonTokens - toonTokens} tokens
                    </div>
                </div>
                <div class="stat-box">
                    <div class="stat-label">% Economia</div>
                    <div class="stat-value ${toonTokens < jsonTokens ? 'match' : 'diff'}">
                        ${jsonTokens > 0 ? ((jsonTokens - toonTokens) / jsonTokens * 100).toFixed(2) : 0}%
                    </div>
                </div>
                <div class="stat-box">
                    <div class="stat-label">Formato Otimizado</div>
                    <div class="stat-value ${toonTokens < jsonTokens ? 'match' : 'diff'}">
                        ${toonTokens < jsonTokens ? 'TOON' : 'JSON'}
                    </div>
                </div>
            </div>
        `;
    } catch (error) {
        resultDiv.innerHTML = `<div class="error">Erro: ${error.message}</div>`;
    }
}

// Make usingOfficialLibrary available globally
window.usingOfficialLibrary = false;

// Initialize examples on load
document.addEventListener('DOMContentLoaded', async () => {
    const container = document.getElementById('examples-container');
    container.innerHTML = '<div class="loading">Carregando biblioteca TOON...</div>';
    
    // Load TOON library
    await loadToonLibrary();
    window.usingOfficialLibrary = usingOfficialLibrary;
    
    if (!usingOfficialLibrary) {
        const info = document.createElement('div');
        info.className = 'example-section';
        info.style.background = '#fff3cd';
        info.style.borderColor = '#ffc107';
        info.innerHTML = '<div style="padding: 10px;">⚠️ Biblioteca TOON oficial não disponível via CDN. Usando implementação local baseada na especificação oficial.</div>';
        container.appendChild(info);
    }
    
    // Clear loading and render examples
    const loadingDiv = container.querySelector('.loading');
    if (loadingDiv) {
        loadingDiv.remove();
    }
    
    examples.forEach((example, index) => {
        renderExample(example, index);
    });
});

