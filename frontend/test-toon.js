/**
 * Script para testar conversão TOON de um JSON específico
 * Usa a biblioteca oficial @toon-format/toon
 */

import { toon } from '@toon-format/toon';

const testJson = {
    "blog": {
        "posts": [
            {
                "id": "post-1",
                "title": "Getting Started with JSON",
                "author": "Jane Doe",
                "publishedAt": "2025-01-10",
                "tags": ["tutorial", "json", "beginner"],
                "comments": 15,
                "likes": 42
            },
            {
                "id": "post-2",
                "title": "Advanced Data Structures",
                "author": "John Smith",
                "publishedAt": "2025-01-12",
                "tags": ["advanced", "algorithms", "data-structures"],
                "comments": 8,
                "likes": 28
            }
        ],
        "analytics": {
            "totalViews": 1250,
            "uniqueVisitors": 890
        }
    }
};

try {
    console.log("=== JSON Original ===");
    console.log(JSON.stringify(testJson, null, 2));
    console.log("\n=== TOON (Biblioteca Oficial) ===");
    const toonResult = toon.stringify(testJson);
    console.log(toonResult);
    console.log("\n=== Comparação ===");
    console.log(`JSON length: ${JSON.stringify(testJson).length} chars`);
    console.log(`TOON length: ${toonResult.length} chars`);
} catch (error) {
    console.error("Erro:", error.message);
    process.exit(1);
}

