// FASE 1: Análisis Léxico (Lexer)

// =============================================================================



// Clase para representar un token

import java.util.*;
import java.util.regex.*;

class Token {
    private String type; // Tipo del token (e.g., "NUMBER", "IDENTIFIER", "PLUS")
    private String value; // Valor del token (e.g., "10", "x", "+")
    private int line; // Número de línea donde aparece el token
    private int column; // Número de columna donde aparece el token

    public Token(String type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public String getType() {
        return type;
    }
    public String getValue() {
        return value;
    }
    public int getLine() {
        return line;
    }
    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "Token(" + type + ", '" + value + "', línea:" + line + ", col:" + column + ")";
    }
}

class Lexer {
    private String sourceCode; // Código fuente a tokenizar
    private List<Token> tokens; // Lista de tokens generados
    private int currentLine; // Línea actual durante el análisis
    private int currentColumn; // Columna actual durante el análisis

    // Mapa que define los tipos de tokens y sus patrones regex
    private static final Map<String, String> TOKEN_TYPES = new LinkedHashMap<>();

    static {
        TOKEN_TYPES.put("WHITESPACE", "\\s+"); // Espacios en blanco
        TOKEN_TYPES.put("COMMENT", "//.*"); // Comentarios de una línea
        TOKEN_TYPES.put("NUMBER", "\\d+(\\.\\d+)?"); // Números enteros y decimales
        TOKEN_TYPES.put("STRING", "\"[^\"]*\""); // Cadenas de texto
        TOKEN_TYPES.put("PRINT", "print\\b"); // Palabra clave "print"
        TOKEN_TYPES.put("LET", "let\\b"); // Palabra clave "let"
        TOKEN_TYPES.put("IF", "if\\b");       // Palabra clave "if"
        TOKEN_TYPES.put("ELSE", "else\\b");   // Palabra clave "else"
        TOKEN_TYPES.put("WHILE", "while\\b"); // Palabra clave "while"
        TOKEN_TYPES.put("EQUALS", "==");    // Operador de igualdad
        TOKEN_TYPES.put("ASSIGN", "=");     // Operador de asignación
        TOKEN_TYPES.put("PLUS", "\\+"); // Operador de suma
        TOKEN_TYPES.put("MINUS", "-"); // Operador de resta
        TOKEN_TYPES.put("MULTIPLY", "\\*"); // Operador de multiplicación
        TOKEN_TYPES.put("DIVIDE", "/"); // Operador de división
        TOKEN_TYPES.put("LPAREN", "\\("); // Paréntesis izquierdo
        TOKEN_TYPES.put("RPAREN", "\\)"); // Paréntesis derecho
        TOKEN_TYPES.put("LBRACE", "\\{"); // Llave izquierda
        TOKEN_TYPES.put("RBRACE", "\\}"); // Llave derecha
        TOKEN_TYPES.put("SEMICOLON", ";"); // Punto y coma
        TOKEN_TYPES.put("COMMA", ","); // Coma
        TOKEN_TYPES.put("GREATER", ">");  // Operador mayor que
        TOKEN_TYPES.put("LESS", "<");     // Operador menor que
        TOKEN_TYPES.put("IDENTIFIER", "[a-zA-Z_][a-zA-Z0-9_]*"); // Identificadores
    }

    public Lexer(String sourceCode) {
        this.sourceCode = sourceCode;
        this.tokens = new ArrayList<>();
        this.currentLine = 1;
        this.currentColumn = 1;
    }

    // Método para realizar el análisis léxico y generar la lista de tokens
    public List<Token> tokenize() {
        String remainingCode = sourceCode;
        int offset = 0;
        while (!remainingCode.isEmpty()) {
            Matcher match = null;
            String matchedTokenType = null;
            int matchedLength = -1;

            // Buscar el primer token que coincida
            for (Map.Entry<String, String> entry : TOKEN_TYPES.entrySet()) {
                String tokenType = entry.getKey();
                String pattern = entry.getValue();
                Pattern regex = Pattern.compile("^" + pattern);
                Matcher m = regex.matcher(remainingCode);
                if (m.find()) {
                    match = m;
                    matchedTokenType = tokenType;
                    matchedLength = m.group().length();
                    break;
                }
            }

            if (match != null && matchedLength > 0) {
                String value = match.group();
                // Si el token no es un espacio en blanco ni un comentario, se agrega a la lista
                if (!matchedTokenType.equals("WHITESPACE") && !matchedTokenType.equals("COMMENT")) {
                    tokens.add(new Token(matchedTokenType, value, currentLine, currentColumn));
                }
                // Actualiza línea y columna
                for (char c : value.toCharArray()) {
                    if (c == '\n') {
                        currentLine++;
                        currentColumn = 1;
                    } else {
                        currentColumn++;
                    }
                }
                // Avanza el código restante
                remainingCode = remainingCode.substring(matchedLength);
            } else {
                // Si no se encuentra ningún token válido, reporta error
                char errorChar = remainingCode.charAt(0);
                throw new IllegalArgumentException("Carácter no reconocido: '" + errorChar + "' en la línea " + currentLine + ", columna " + currentColumn);
            }
        }
        return tokens;
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
