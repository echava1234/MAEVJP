// FASE 1: Análisis Léxico (Lexer)

// =============================================================================



// Clase para representar un token

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



// Clase para realizar el análisis léxico (tokenización)

class Lexer {

    private String sourceCode; // Código fuente a tokenizar

    private List<Token> tokens; // Lista de tokens generados

    private int currentLine; // Línea actual durante el análisis

    private int currentColumn; // Columna actual durante el análisis

    // Mapa que define los tipos de tokens y sus patrones regex  private static final Map<String, String> TOKEN_TYPES = new HashMap<>();



    // Inicializa el mapa de tipos de tokens

    static {

        TOKEN_TYPES.put("NUMBER", "\\d+(\\.\\d+)?"); // Números enteros y decimales

        TOKEN_TYPES.put("PLUS", "\\+"); // Operador de suma

        TOKEN_TYPES.put("MINUS", "-"); // Operador de resta

        TOKEN_TYPES.put("MULTIPLY", "\\*"); // Operador de multiplicación

        TOKEN_TYPES.put("DIVIDE", "/"); // Operador de división

        TOKEN_TYPES.put("LPAREN", "\\("); // Paréntesis izquierdo

        TOKEN_TYPES.put("RPAREN", "\\)"); // Paréntesis derecho

        TOKEN_TYPES.put("ASSIGN", "="); // Operador de asignación

        TOKEN_TYPES.put("SEMICOLON", ";"); // Punto y coma

        TOKEN_TYPES.put("IDENTIFIER", "[a-zA-Z_][a-zA-Z0-9_]*"); // Identificadores (nombres de variables, funciones, etc.)

        TOKEN_TYPES.put("PRINT", "print"); // Palabra clave "print"

        TOKEN_TYPES.put("IF", "if");       // Palabra clave "if"

        TOKEN_TYPES.put("ELSE", "else");   // Palabra clave "else"

        TOKEN_TYPES.put("WHILE", "while"); // Palabra clave "while"

        TOKEN_TYPES.put("EQUALS", "==");    // Operador de igualdad

        TOKEN_TYPES.put("LBRACE", "\\{"); // Llave izquierda

        TOKEN_TYPES.put("RBRACE", "\\}"); // Llave derecha

        TOKEN_TYPES.put("GREATER", ">");  // Operador mayor que

        TOKEN_TYPES.put("LESS", "<");     // Operador menor que

        TOKEN_TYPES.put("WHITESPACE", "\\s+"); // Espacios en blanco

        TOKEN_TYPES.put("COMMENT", "//.*"); // Comentarios de una línea

        TOKEN_TYPES.put("STRING", "\"[^\"]*\""); // Cadenas de texto   }



    public Lexer(String sourceCode) {

        this.sourceCode = sourceCode; // Inicializa el código fuente

        this.tokens = new ArrayList<>(); // Inicializa la lista de tokens

        this.currentLine = 1; // Inicializa la línea actual

        this.currentColumn = 1; // Inicializa la columna actual  }



    // Método para realizar el análisis léxico y generar la lista de tokens

    public List<Token> tokenize() {

        String remainingCode = sourceCode; // Copia del código fuente para ir consumiendo  while (!remainingCode.isEmpty()) { // Mientras haya código por procesar

            Matcher match = null; // Almacena el resultado de la búsqueda de un token

            String matchedTokenType = null; // Almacena el tipo del token encontrado  // Itera sobre los tipos de tokens y sus patrones regex

            for (Map.Entry<String, String> entry : TOKEN_TYPES.entrySet()) {

                String tokenType = entry.getKey(); // Obtiene el tipo del token

                String pattern = entry.getValue(); // Obtiene el patrón regex del token

                Pattern regex = Pattern.compile("^" + pattern); // Compila el patrón regex

                match = regex.matcher(remainingCode); // Intenta encontrar el patrón al inicio del código restante



                if (match.find()) { // Si se encuentra una coincidencia

                    matchedTokenType = tokenType; // Guarda el tipo del token

                    break; // Termina la búsqueda de tokens  }

            }



            if (match != null) { // Si se encontró un token

                String value = match.group(0); // Obtiene el valor del token (la parte que coincide con el patrón) 

                // Si el token no es un espacio en blanco ni un comentario, se agrega a la lista

                if (!matchedTokenType.equals("WHITESPACE") && !matchedTokenType.equals("COMMENT")) {

                    // Identifica palabras clave

                    if (matchedTokenType.equals("IDENTIFIER")) {

                        if (value.equals("print")) {

                            matchedTokenType = "PRINT";   matchedTokenType = "WHILE";

                        }

                    }

                    Token token = new Token(matchedTokenType, value, currentLine, currentColumn); // Crea el objeto Token

                    tokens.add(token); // Agrega el token a la lista   }



                // Actualiza la línea y la columna para el próximo token

                for (char c : value.toCharArray()) {

                    if (c == '\n') {

                        currentLine++;  }

                }



                remainingCode = remainingCode.substring(value.length()); // Elimina el token encontrado del código restante   } else {

                // Si no se encuentra ningún token, lanza una excepción

                char errorChar = remainingCode.charAt(0);

                throw new IllegalArgumentException("Carácter no reconocido: '" + errorChar + "' en la línea " + currentLine + ", columna " + currentColumn);

            }

        }

        return tokens; // Devuelve la lista de tokens generada  }



    public List<Token> getTokens() {

        return tokens;

    }

}
