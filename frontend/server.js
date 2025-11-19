/**
 * Simple HTTP server with TOON conversion endpoint
 * Uses the official TOON library from npm
 */

import express from 'express';
import cors from 'cors';
import { encode } from '@toon-format/toon';

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json({ limit: '10mb' }));

// Health check
app.get('/health', (req, res) => {
    res.json({ status: 'ok', library: 'official-toon' });
});

// TOON conversion endpoint
app.post('/api/toon/convert', (req, res) => {
    try {
        const { json } = req.body;
        
        if (!json) {
            return res.status(400).json({ 
                error: 'JSON object is required in request body' 
            });
        }
        
        // Convert to TOON using official library
        // The library exports 'encode' function, not 'stringify'
        const toonResult = encode(json);
        
        res.json({
            success: true,
            toon: toonResult,
            json: JSON.stringify(json)
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            error: error.message,
            stack: error.stack
        });
    }
});

// Start server
app.listen(PORT, () => {
    console.log(`🚀 TOON Comparison Server running on http://localhost:${PORT}`);
    console.log(`📡 Endpoint: POST http://localhost:${PORT}/api/toon/convert`);
    console.log(`💚 Health check: GET http://localhost:${PORT}/health`);
});

