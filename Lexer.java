// =============================================================================
// FASE 1: Análisis Léxico (Lexer)
// =============================================================================

// Importación de utilidades básicas de Java
import java.util.*;
import java.util.regex.*;

// -----------------------------------------------------------------------------
// Clase Token
// Representa la unidad léxica básica: cada palabra clave, operador, identificador o literal del código fuente
// -----------------------------------------------------------------------------
class Token {
    // Tipo de token (ej: LET, IDENTIFIER, NUMBER, PLUS, etc.)
    private String type;
    // Valor del token (ej: nombre de variable, valor de número, texto de string, etc.)
    private String value;
    // Línea y columna donde aparece el token (para mensajes de error claros)
    private int line;
    private int column;

    // Constructor para inicializar los campos del token
    public Token(String type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }
    // Métodos de acceso
    public String getType() { return type; }
    public String getValue() { return value; }
    public int getLine() { return line; }
    public int getColumn() { return column; }

    // Devuelve una representación legible del token (para depuración o mensajes)
    @Override
    public String toString() {
        return "Token(" + type + ", '" + value + "', línea:" + line + ", col:" + column + ")";
    }
}

// -----------------------------------------------------------------------------
// Clase Lexer
// Encargada de transformar el código fuente (texto) en una secuencia de tokens
// -----------------------------------------------------------------------------
class Lexer {
    // Código fuente completo como string
    private String sourceCode;
    // Lista de tokens generados
    private List<Token> tokens;
    // Seguimiento de la posición para reportar línea/columna en errores
    private int currentLine;
    private int currentColumn;

    // Diccionario de tipos de token asociados a su expresión regular (regex)
    // El orden ES IMPORTANTE: primero patrones más largos o específicos
    private static final Map<String, String> TOKEN_TYPES = new LinkedHashMap<>();

    static {
        // Ignorables (no generan token): espacios y comentarios
        TOKEN_TYPES.put("WHITESPACE", "\\s+");
        TOKEN_TYPES.put("COMMENT", "//.*");

        // Literales y palabras clave (palabras reservadas del lenguaje)
        TOKEN_TYPES.put("NUMBER", "\\d+(\\.\\d+)?");          // Números enteros o decimales
        TOKEN_TYPES.put("STRING", "\"[^\"]*\"");              // Cadenas entre comillas dobles
        TOKEN_TYPES.put("PRINT", "print\\b");
        TOKEN_TYPES.put("LET", "let\\b");
        TOKEN_TYPES.put("IF", "if\\b");
        TOKEN_TYPES.put("ELSE", "else\\b");
        TOKEN_TYPES.put("WHILE", "while\\b");

        // Operadores (ordenados para evitar ambigüedad con los de un solo carácter)
        TOKEN_TYPES.put("LE", "<=");      // Menor o igual
        TOKEN_TYPES.put("GE", ">=");      // Mayor o igual
        TOKEN_TYPES.put("EQ", "==");      // Igualdad
        TOKEN_TYPES.put("ASSIGN", "=");   // Asignación
        TOKEN_TYPES.put("PLUS", "\\+");   // Suma
        TOKEN_TYPES.put("MINUS", "-");    // Resta
        TOKEN_TYPES.put("MULTIPLY", "\\*"); // Multiplicación
        TOKEN_TYPES.put("DIVIDE", "/");   // División
        TOKEN_TYPES.put("LT", "<");       // Menor que (NOTA: después de <=)
        TOKEN_TYPES.put("GT", ">");       // Mayor que (NOTA: después de >=)

        // Signos de agrupación y puntuación
        TOKEN_TYPES.put("LPAREN", "\\(");
        TOKEN_TYPES.put("RPAREN", "\\)");
        TOKEN_TYPES.put("LBRACE", "\\{");
        TOKEN_TYPES.put("RBRACE", "\\}");
        TOKEN_TYPES.put("SEMICOLON", ";");
        TOKEN_TYPES.put("COMMA", ",");

        // Identificadores (nombres de variables, funciones, etc.)
        TOKEN_TYPES.put("IDENTIFIER", "[a-zA-Z_][a-zA-Z0-9_]*");
    }

    // Constructor: inicializa los campos y posición inicial
    public Lexer(String sourceCode) {
        this.sourceCode = sourceCode;
        this.tokens = new ArrayList<>();
        this.currentLine = 1;
        this.currentColumn = 1;
    }

    // -------------------------------------------------------------------------
    // Método principal: convierte el código fuente en una lista de tokens
    // -------------------------------------------------------------------------
    public List<Token> tokenize() {
        String remainingCode = sourceCode;
        // Recorre el código fuente hasta agotarlo
        while (!remainingCode.isEmpty()) {
            Matcher match = null;
            String matchedTokenType = null;
            int matchedLength = -1;

            // Intenta encontrar el patrón de token más largo posible al principio del string restante
            for (Map.Entry<String, String> entry : TOKEN_TYPES.entrySet()) {
                String tokenType = entry.getKey();
                String pattern = entry.getValue();
                Pattern regex = Pattern.compile("^" + pattern); // solo al principio
                Matcher m = regex.matcher(remainingCode);
                if (m.find()) {
                    match = m;
                    matchedTokenType = tokenType;
                    matchedLength = m.group().length();
                    break; // sale al encontrar el primer match (por eso el orden es clave)
                }
            }

            if (match != null && matchedLength > 0) {
                String value = match.group();
                // No se agregan al resultado los espacios ni comentarios (solo se ignoran)
                if (!matchedTokenType.equals("WHITESPACE") && !matchedTokenType.equals("COMMENT")) {
                    tokens.add(new Token(matchedTokenType, value, currentLine, currentColumn));
                }

                // Actualiza la posición de línea y columna para cada carácter leído
                for (char c : value.toCharArray()) {
                    if (c == '\n') {
                        currentLine++;
                        currentColumn = 1;
                    } else {
                        currentColumn++;
                    }
                }
                // Elimina del código fuente lo que ya fue analizado
                remainingCode = remainingCode.substring(matchedLength);
            } else {
                // Si ningún patrón reconoce el siguiente carácter, lanza error (carácter ilegal)
                char errorChar = remainingCode.charAt(0);
                throw new IllegalArgumentException("Carácter no reconocido: '" + errorChar + "' en la línea " + currentLine + ", columna " + currentColumn);
            }
        }
        return tokens;
    }

    // Permite acceder a los tokens generados desde fuera (útil para depuración)
    public List<Token> getTokens() {
        return tokens;
    }
}
