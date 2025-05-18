import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Clase para representar un token
class Token {
    private String type;   // Tipo del token (ej., "IDENTIFIER", "NUMBER", "PLUS")
    private String value;  // Valor del token (ej., "a", "10", "+")
    private int line;     // Número de línea en el código fuente
    private int column;   // Número de columna en la línea

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

// Clase para realizar el análisis léxico (tokenización)
class Lexer {
    private String sourceCode;       // El código fuente a analizar
    private List<Token> tokens;         // Lista para almacenar los tokens generados
    private int currentLine;          // Número de línea actual
    private int currentColumn;        // Número de columna actual
    private static final java.util.Map<String, String> TOKEN_TYPES = new java.util.HashMap<>();  // Mapa de tipos de tokens y sus patrones regex

    // Inicialización estática del mapa de tipos de tokens
    static {
        TOKEN_TYPES.put("NUMBER", "\\d+(\\.\\d+)?");       // Números (enteros y decimales)
        TOKEN_TYPES.put("PLUS", "\\+");                   // Operador de suma
        TOKEN_TYPES.put("MINUS", "-");                  // Operador de resta
        TOKEN_TYPES.put("MULTIPLY", "\\*");              // Operador de multiplicación
        TOKEN_TYPES.put("DIVIDE", "/");                   // Operador de división
        TOKEN_TYPES.put("LPAREN", "\\(");                 // Paréntesis izquierdo
        TOKEN_TYPES.put("RPAREN", "\\)");                 // Paréntesis derecho
        TOKEN_TYPES.put("ASSIGN", "=");                   // Operador de asignación
        TOKEN_TYPES.put("SEMICOLON", ";");                // Punto y coma
        TOKEN_TYPES.put("IDENTIFIER", "[a-zA-Z_][a-zA-Z0-9_]*");  // Identificadores (nombres de variables, etc.)
        TOKEN_TYPES.put("PRINT", "print");                 // Palabra clave "print"
        TOKEN_TYPES.put("IF", "if");                      // Palabra clave "if"
        TOKEN_TYPES.put("ELSE", "else");                  // Palabra clave "else"
        TOKEN_TYPES.put("WHILE", "while");                // Palabra clave "while"
        TOKEN_TYPES.put("EQUALS", "==");                  // Operador de igualdad
        TOKEN_TYPES.put("LBRACE", "\\{");                 // Llave izquierda
        TOKEN_TYPES.put("RBRACE", "\\}");                 // Llave derecha
        TOKEN_TYPES.put("GREATER", ">");                  // Operador mayor que
        TOKEN_TYPES.put("LESS", "<");                     // Operador menor que
        TOKEN_TYPES.put("WHITESPACE", "\\s+");             // Espacios en blanco
        TOKEN_TYPES.put("COMMENT", "//.*");               // Comentarios de una sola línea
    }

    // Constructor de la clase Lexer
    public Lexer(String sourceCode) {
        this.sourceCode = sourceCode;
        this.tokens = new ArrayList<>();
        this.currentLine = 1;
        this.currentColumn = 1;
    }

    // Método para realizar el análisis léxico y generar la lista de tokens
    public List<Token> tokenize() {
        /*
         * Convierte el código fuente en una lista de tokens.
         */
        String remainingCode = sourceCode;  // Código restante por analizar

        while (!remainingCode.isEmpty()) {
            Matcher match = null;            // Objeto Matcher para la coincidencia de expresiones regulares
            String matchedTokenType = null;  // Tipo de token que coincide

            // Itera sobre los tipos de tokens definidos
            for (java.util.Map.Entry<String, String> entry : TOKEN_TYPES.entrySet()) {
                String tokenType = entry.getKey();
                String pattern = entry.getValue();
                Pattern regex = Pattern.compile("^" + pattern);  // Compila la expresión regular
                match = regex.matcher(remainingCode);         // Intenta hacer coincidir con el inicio del código restante

                if (match.find()) {
                    matchedTokenType = tokenType;
                    break;  // Si hay coincidencia, sale del bucle interno
                }
            }

            if (match != null) {
                String value = match.group(0);  // Obtiene el valor del token coincidente

                // Ignora espacios en blanco y comentarios
                if (!matchedTokenType.equals("WHITESPACE") && !matchedTokenType.equals("COMMENT")) {
                    // Maneja las palabras clave (keywords)
                    if (matchedTokenType.equals("IDENTIFIER")) {
                        if (value.equals("print")) {
                            matchedTokenType = "PRINT";
                        } else if (value.equals("if")) {
                            matchedTokenType = "IF";
                        } else if (value.equals("else")) {
                            matchedTokenType = "ELSE";
                        } else if (value.equals("while")) {
                            matchedTokenType = "WHILE";
                        }
                    }
                    Token token = new Token(matchedTokenType, value, currentLine, currentColumn);  // Crea un nuevo token
                    tokens.add(token);                                          // Agrega el token a la lista
                }

                // Actualiza la posición de línea y columna
                for (char c : value.toCharArray()) {
                    if (c == '\n') {
                        currentLine++;
                        currentColumn = 1;
                    } else {
                        currentColumn++;
                    }
                }

                // Elimina la parte del código que ya se ha procesado (el token)
                remainingCode = remainingCode.substring(value.length());
            } else {
                // Si no se encuentra ninguna coincidencia, hay un error: carácter no reconocido
                char errorChar = remainingCode.charAt(0);
                throw new IllegalArgumentException("Carácter no reconocido: '" + errorChar + "' en la línea " + currentLine + ", columna " + currentColumn);
            }
        }
        return tokens;  // Devuelve la lista de tokens generados
    }

    // Método para obtener la lista de tokens
    public List<Token> getTokens() {
        return tokens;
    }
}

// Clase principal que contiene el método main para ejecutar el analizador léxico
public class Main {
    public static void main(String[] args) {
        // Código fuente de ejemplo
        String sourceCode = "print a = 10; if (a == 10) { print \"a es 10\"; } else { print \"a no es 10\"; } // Esto es un comentario\nwhile (a > 0) { a = a - 1; print a; }";
        Lexer lexer = new Lexer(sourceCode);      // Crea una instancia del analizador léxico
        List<Token> tokens = lexer.tokenize(); // Obtiene la lista de tokens

        // Imprime los tokens generados
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}

