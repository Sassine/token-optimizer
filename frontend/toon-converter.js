/**
 * TOON Converter - Implementation based on official TOON specification
 * https://github.com/toon-format/spec
 */

export class ToonConverter {
    
    /**
     * Converts a JavaScript object to TOON format
     * @param {any} obj - The object to convert
     * @returns {string} TOON formatted string
     */
    static stringify(obj) {
        if (obj === null || obj === undefined) {
            return 'null';
        }
        return this.convertValue(obj, '');
    }
    
    /**
     * Converts a value to TOON format
     * @param {any} value - The value to convert
     * @param {string} indent - Current indentation
     * @returns {string} TOON formatted string
     */
    static convertValue(value, indent) {
        if (value === null || value === undefined) {
            return 'null';
        }
        
        if (Array.isArray(value)) {
            return this.convertArray(value, indent);
        }
        
        if (typeof value === 'object') {
            return this.convertObject(value, indent);
        }
        
        return this.formatPrimitive(value);
    }
    
    /**
     * Converts an array to TOON format
     */
    static convertArray(arr, indent) {
        if (arr.length === 0) {
            return '[]:';
        }
        
        // Check if array of objects
        const isObjectArray = arr.every(item => 
            item !== null && 
            typeof item === 'object' && 
            !Array.isArray(item)
        );
        
        if (isObjectArray) {
            return this.convertArrayOfObjects(arr, indent);
        } else {
            return this.convertSimpleArray(arr);
        }
    }
    
    /**
     * Converts array of objects: [count]{prop1,prop2}:
     *                              val1,val2
     *                              val3,val4
     */
    static convertArrayOfObjects(arr, indent) {
        const firstObj = arr[0];
        const keys = Object.keys(firstObj);
        const count = arr.length;
        const itemIndent = indent + '  ';
        
        let result = `[${count}]{${keys.join(',')}}:`;
        
        for (const item of arr) {
            result += '\n' + itemIndent;
            const values = keys.map(key => this.formatArrayValue(item[key]));
            result += values.join(',');
        }
        
        return result;
    }
    
    /**
     * Converts simple array: [count]: val1,val2,val3
     */
    static convertSimpleArray(arr) {
        const count = arr.length;
        const values = arr.map(item => this.formatArrayValue(item));
        return `[${count}]: ${values.join(',')}`;
    }
    
    /**
     * Converts an object to TOON format
     */
    static convertObject(obj, indent) {
        const keys = Object.keys(obj);
        if (keys.length === 0) {
            return '';
        }
        
        let result = '';
        const newIndent = indent + '  ';
        
        for (let i = 0; i < keys.length; i++) {
            if (i > 0) {
                result += '\n';
            }
            const key = keys[i];
            const value = obj[key];
            
            result += indent + key + ':';
            
            if (value === null || value === undefined) {
                result += ' null';
            } else if (Array.isArray(value)) {
                const arrayStr = this.convertArray(value, newIndent);
                // If array starts with newline, it's already indented
                if (arrayStr.startsWith('[')) {
                    result += ' ' + arrayStr;
                } else {
                    result += '\n' + arrayStr;
                }
            } else if (typeof value === 'object') {
                result += '\n' + this.convertObject(value, newIndent);
            } else {
                result += ' ' + this.formatPrimitive(value);
            }
        }
        
        return result;
    }
    
    /**
     * Formats a primitive value for object properties
     */
    static formatPrimitive(value) {
        if (value === null || value === undefined) {
            return 'null';
        }
        if (typeof value === 'string') {
            // Strings don't need quotes in object properties (unless special)
            return value;
        }
        if (typeof value === 'boolean') {
            return String(value);
        }
        if (typeof value === 'number') {
            return String(value);
        }
        return String(value);
    }
    
    /**
     * Formats a value for array (may need quotes)
     */
    static formatArrayValue(value) {
        if (value === null || value === undefined) {
            return 'null';
        }
        if (typeof value === 'string') {
            if (this.needsQuotesInArray(value)) {
                return `"${value}"`;
            }
            return value;
        }
        if (typeof value === 'boolean') {
            return String(value);
        }
        if (typeof value === 'number') {
            return String(value);
        }
        if (Array.isArray(value)) {
            return this.convertSimpleArray(value);
        }
        return String(value);
    }
    
    /**
     * Checks if a string needs quotes in array context
     */
    static needsQuotesInArray(str) {
        if (str.length === 0) {
            return true;
        }
        
        // Pure numbers need quotes to differentiate from number type
        if (/^-?\d+(\.\d+)?([eE][+-]?\d+)?$/.test(str)) {
            return true;
        }
        
        // Special characters that require quotes
        if (/[\s,\[\]{}:"]/.test(str)) {
            return true;
        }
        
        return false;
    }
}

