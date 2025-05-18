// FASE 1: Análisis Léxico (Lexer)

// =============================================================================

import java.util.*;
import java.util.regex.*;

class Token {
    private String type;
    private String value;
    private int line;
    private int column;

    public Token(String type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public String getType() { return type; }
    public String getValue() { return value; }
    public int getLine() { return line; }
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return "Token(" + type + ", '" + value + "', línea:" + line + ", col:" + column + ")";
    }
}

class Lexer {
    private String sourceCode;
    private List<Token> tokens;
    private int currentLine;
    private int currentColumn;

    private static final Map<String, String> TOKEN_TYPES = new LinkedHashMap<>();

    static {
        TOKEN_TYPES.put("WHITESPACE", "\\s+");
        TOKEN_TYPES.put("COMMENT", "//.*");
        TOKEN_TYPES.put("NUMBER", "\\d+(\\.\\d+)?");
        TOKEN_TYPES.put("STRING", "\"[^\"]*\"");
        TOKEN_TYPES.put("PRINT", "print\\b");
        TOKEN_TYPES.put("LET", "let\\b");
        TOKEN_TYPES.put("IF", "if\\b");
        TOKEN_TYPES.put("ELSE", "else\\b");
        TOKEN_TYPES.put("WHILE", "while\\b");
        TOKEN_TYPES.put("LE", "<="); // Menor o igual
        TOKEN_TYPES.put("GE", ">="); // Mayor o igual
        TOKEN_TYPES.put("EQ", "=="); // Igualdad
        TOKEN_TYPES.put("ASSIGN", "="); // Asignación
        TOKEN_TYPES.put("PLUS", "\\+");
        TOKEN_TYPES.put("MINUS", "-");
        TOKEN_TYPES.put("MULTIPLY", "\\*");
        TOKEN_TYPES.put("DIVIDE", "/");
        TOKEN_TYPES.put("LT", "<");
        TOKEN_TYPES.put("GT", ">");
        TOKEN_TYPES.put("LPAREN", "\\(");
        TOKEN_TYPES.put("RPAREN", "\\)");
        TOKEN_TYPES.put("LBRACE", "\\{");
        TOKEN_TYPES.put("RBRACE", "\\}");
        TOKEN_TYPES.put("SEMICOLON", ";");
        TOKEN_TYPES.put("COMMA", ",");
        TOKEN_TYPES.put("IDENTIFIER", "[a-zA-Z_][a-zA-Z0-9_]*");
    }

    public Lexer(String sourceCode) {
        this.sourceCode = sourceCode;
        this.tokens = new ArrayList<>();
        this.currentLine = 1;
        this.currentColumn = 1;
    }

    public List<Token> tokenize() {
        String remainingCode = sourceCode;
        while (!remainingCode.isEmpty()) {
            Matcher match = null;
            String matchedTokenType = null;
            int matchedLength = -1;

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
                if (!matchedTokenType.equals("WHITESPACE") && !matchedTokenType.equals("COMMENT")) {
                    tokens.add(new Token(matchedTokenType, value, currentLine, currentColumn));
                }

                for (char c : value.toCharArray()) {
                    if (c == '\n') {
                        currentLine++;
                        currentColumn = 1;
                    } else {
                        currentColumn++;
                    }
                }
                remainingCode = remainingCode.substring(matchedLength);
            } else {
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
