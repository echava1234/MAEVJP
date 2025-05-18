// =============================================================================
// FASE 2: Análisis Sintáctico (Parser)
// =============================================================================

// Clase base para los nodos del AST (Árbol de Sintaxis Abstracta)
abstract class ASTNode {
}

// Clase para representar un programa, que contiene una lista de sentencias
class Program extends ASTNode {
    List<ASTNode> statements; // Lista de nodos AST que representan las sentencias del programa

    public Program(List<ASTNode> statements) {
        this.statements = statements;
    }
}

// Clase para representar una operación binaria (e.g., suma, resta)
class BinaryOperation extends ASTNode {
    ASTNode left; // Nodo AST del operando izquierdo
    String operator; // Operador de la operación
    ASTNode right; // Nodo AST del operando derecho

    public BinaryOperation(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}

// Clase para representar un número
class Number extends ASTNode {
    double value; // Valor del número

    public Number(String value) {
        this.value = Double.parseDouble(value);
    }
}

// Clase para representar una cadena de texto
class StringLiteral extends ASTNode {
    String value;

    public StringLiteral(String value) {
        this.value = value;
    }
}

// Clase para representar un identificador (variable)
class Identifier extends ASTNode {
    String name; // Nombre del identificador

    public Identifier(String name) {
        this.name = name;
    }
}

// Clase para representar una asignación (e.g., x = 10)
class Assignment extends ASTNode {
    String name; // Nombre de la variable a la que se asigna
    ASTNode value; // Nodo AST de la expresión que se asigna a la variable

    public Assignment(String name, ASTNode value) {
        this.name = name;
        this.value = value;
    }
}

// Clase para representar una sentencia de impresión (e.g., print x)
class Print extends ASTNode {
    ASTNode expression; // Nodo AST de la expresión que se va a imprimir

    public Print(ASTNode expression) {
        this.expression = expression;
    }
}

// Clase para representar una sentencia if
class IfStatement extends ASTNode {
    ASTNode condition; // Nodo AST de la condición del if
    List<ASTNode> ifBlock; // Lista de nodos AST de las sentencias dentro del bloque if
    List<ASTNode> elseBlock; // Lista de nodos AST de las sentencias dentro del bloque else (opcional)

    public IfStatement(ASTNode condition, List<ASTNode> ifBlock, List<ASTNode> elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }
}

// Clase para representar una sentencia while
class WhileStatement extends ASTNode {
    ASTNode condition; // Nodo AST de la condición del while
    List<ASTNode> block; // Lista de nodos AST de las sentencias dentro del bloque while

    public WhileStatement(ASTNode condition, List<ASTNode> block) {
        this.condition = condition;
        this.block = block;
    }
}

// Clase para representar una comparación (e.g., x == y, x > y)
class Comparison extends ASTNode {
    ASTNode left; // Nodo AST del operando izquierdo
    String operator; // Operador de comparación
    ASTNode right; // Nodo AST del operando derecho

    public Comparison(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}

// Clase para el analizador sintáctico
class Parser {
    private List<Token> tokens; // Lista de tokens a analizar
    private int currentTokenIndex; // Índice del token actual
    private Token currentToken; // Token actual

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.currentToken = tokens != null && !tokens.isEmpty() ? tokens.get(0) : null;
    }

    // Avanza al siguiente token en la lista
    private void advance() {
        currentTokenIndex++;
        if (currentTokenIndex < tokens.size()) {
            currentToken = tokens.get(currentTokenIndex);
        } else {
            currentToken = null; // Si se llega al final de la lista, currentToken se establece en null
        }
    }

    // Método principal para parsear el código fuente y generar el AST
    public Program parse() {
        List<ASTNode> statements = new ArrayList<>(); // Lista para almacenar las sentencias del programa

        while (currentToken != null) { // Mientras haya tokens por procesar
            ASTNode statement = parseStatement(); // Parsea una sentencia
            statements.add(statement); // Agrega la sentencia a la lista
        }
        return new Program(statements); // Devuelve el nodo AST del programa
    }

    // Método para parsear una sentencia
    private ASTNode parseStatement() {
        if (currentToken.getType().equals("IDENTIFIER")) { // Si la sentencia comienza con un identificador, es una asignación
            return parseAssignment();
        } else if (currentToken.getType().equals("PRINT")) { // Si la sentencia comienza con "print", es una sentencia de impresión
            return parsePrint();
        } else if (currentToken.getType().equals("IF")) { // Si la sentencia comienza con "if", es una sentencia condicional
            return parseIf();
        } else if (currentToken.getType().equals("WHILE")) { // Si la sentencia comienza con "while", es un bucle while
            return parseWhile();
        } else {
            // Si la sentencia no coincide con ninguna de las anteriores, es un error de sintaxis
            throw new SyntaxError("Sentencia no válida en la línea " + currentToken.getLine() + ", columna " + currentToken.getColumn());
        }
    }

    // Método para parsear una asignación
    private ASTNode parseAssignment() {
        String varName = currentToken.getValue(); // Obtiene el nombre de la variable
        advance(); // Avanza al siguiente token

        if (currentToken == null || !currentToken.getType().equals("ASSIGN")) {
            // Si no se encuentra un "=", es un error de sintaxis
            throw new SyntaxError("Se esperaba '=' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Avanza al siguiente token

        ASTNode expression = parseExpression(); // Parsea la expresión que se asigna a la variable

        if (currentToken == null || !currentToken.getType().equals("SEMICOLON")) {
            // Si no se encuentra un ";", es un error de sintaxis
            throw new SyntaxError("Se esperaba ';' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Avanza al siguiente token
        return new Assignment(varName, expression); // Devuelve el nodo AST de la asignación
    }

    // Método para parsear una sentencia de impresión
    private ASTNode parsePrint() {
        advance(); // Avanza al siguiente token

        ASTNode expression = parseExpression(); // Parsea la expresión que se va a imprimir

        if (currentToken == null || !currentToken.getType().equals("SEMICOLON")) {
            // Si no se encuentra un ";", es un error de sintaxis
            throw new SyntaxError("Se esperaba ';' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Avanza al siguiente token
        return new Print(expression); // Devuelve el nodo AST de la impresión
    }

    // Método para parsear una sentencia if
    private ASTNode parseIf() {
        advance(); // Avanza al siguiente token

        if (currentToken == null || !currentToken.getType().equals("LPAREN")) {
            // Si no se encuentra un "(", es un error de sintaxis
            throw new SyntaxError("Se esperaba '(' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Avanza al siguiente token

        ASTNode condition = parseComparison(); // Parsea la condición del if

        if (currentToken == null || !currentToken.getType().equals("RPAREN")) {
            // Si no se encuentra un ")", es un error de sintaxis
            throw new SyntaxError("Se esperaba ')' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Avanza al siguiente token

        if (currentToken == null || !currentToken.getType().equals("LBRACE")) {
            // Si no se encuentra un "{", es un error de sintaxis
            throw new SyntaxError("Se esperaba '{' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Avanza al siguiente token

        List<ASTNode> ifBlock = new ArrayList<>(); // Lista para almacenar las sentencias del bloque if
        while (currentToken != null && !currentToken.getType().equals("RBRACE")) {
            // Mientras no se llegue al final del bloque if
            ASTNode statement = parseStatement(); // Parsea una sentencia dentro del bloque
            ifBlock.add(statement); // Agrega la sentencia a la lista
        }

        if (currentToken == null || !currentToken.getType().equals("RBRACE")) {
            // Si no se encuentra un "}", es un error de sintaxis
            throw new SyntaxError("Se esperaba '}' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Avanza al siguiente token

        List<ASTNode> elseBlock = null; // Inicializa la lista de sentencias del bloque else
        if (currentToken != null && currentToken.getType().equals("ELSE")) { // Si hay un bloque else
            advance(); // Avanza al token "else"
            if (currentToken == null || !currentToken.getType().equals("LBRACE")) {
                // Si no se encuentra un "{", es un error de sintaxis
                throw new SyntaxError("Se esperaba '{' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
            }
            advance(); // Avanza al siguiente token
            elseBlock = new ArrayList<>(); // Crea la lista para el bloque else
            while (currentToken != null && !currentToken.getType().equals("RBRACE")) {
                // Mientras no se llegue al final del bloque else
                ASTNode statement = parseStatement(); // Parsea una sentencia del bloque else
                elseBlock.add(statement); // Agrega la sentencia a la lista
            }
            if (currentToken == null || !currentToken.getType().equals("RBRACE")) {
                // Si no se encuentra un "}", es un error de sintaxis
                throw new SyntaxError("Se esperaba '}' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
            }
            advance(); // Avanza al siguiente token
        }
        return new IfStatement(condition, ifBlock, elseBlock); // Devuelve el nodo AST del if
    }

    // Método para parsear una sentencia while
    private ASTNode parseWhile() {
        advance(); // Avanza al token "while"

        if (currentToken == null || !currentToken.getType().equals("LPAREN")) {
            // Si no se encuentra un "(", es un error de sintaxis
            throw new SyntaxError("Se esperaba '(' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Avanza al siguiente token

        ASTNode condition = parseComparison(); // Parsea la condición del while

        if (currentToken == null || !currentToken.getType().equals("RPAREN")) {
            // Si no se encuentra un ")", es un error de sintaxis
            throw new SyntaxError("Se esperaba ')' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Avanza al siguiente token

        if (currentToken == null || !currentToken.getType().equals("LBRACE")) {
            // Si no se encuentra un "{", es un error de sintaxis
            throw new SyntaxError("Se esperaba '{' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Avanza al siguiente token

        List<ASTNode> block = new ArrayList<>(); // Lista para almacenar las sentencias del bloque while
        while (currentToken != null && !currentToken.getType().equals("RBRACE")) {
            // Mientras no se llegue al final del bloque while
            ASTNode statement = parseStatement(); // Parsea una sentencia del bloque
            block.add(statement); // Agrega la sentencia a la lista
        }

        if (currentToken == null || !currentToken.getType().equals("RBRACE")) {
            // Si no se encuentra un "}", es un error de sintaxis
            throw new SyntaxError("Se esperaba '}' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Avanza al siguiente token
        return new WhileStatement(condition, block); // Devuelve el nodo AST del while
    }

    // Método para parsear una comparación
    private ASTNode parseComparison() {
        ASTNode left = parseExpression(); // Parsea la expresión izquierda de la comparación

        // Verifica si hay un operador de comparación
        if (currentToken != null && (currentToken.getType().equals("EQUALS") || currentToken.getType().equals("GREATER") || currentToken.getType().equals("LESS"))) {
            String operator = currentToken.getValue(); // Obtiene el operador
            advance(); // Avanza al siguiente token
            ASTNode right = parseExpression(); // Parsea la expresión derecha
            return new Comparison(left, operator, right); // Devuelve el nodo AST de la comparación
        } else {
            return left; // Si no hay operador de comparación, devuelve la expresión izquierda
        }
    }

    // Método para parsear una expresión
    private ASTNode parseExpression() {
        return parseAddSub(); // Llama al método para parsear sumas y restas, que tiene menor precedencia
    }

    // Método para parsear sumas y restas
    private ASTNode parseAddSub() {
        ASTNode left = parseMulDiv(); // Parsea primero las multiplicaciones y divisiones

        // Mientras haya operadores de suma o resta
        while (currentToken != null && (currentToken.getType().equals("PLUS") || currentToken.getType().equals("MINUS"))) {
            String operator = currentToken.getValue(); // Obtiene el operador
            advance(); // Avanza al siguiente token
            ASTNode right = parseMulDiv(); // Parsea el siguiente término
            left = new BinaryOperation(left, operator, right); // Crea un nodo AST de operación binaria
        }
        return left; // Devuelve el nodo AST de la expresión de suma/resta
    }

    // Método para parsear multiplicaciones y divisiones
    private ASTNode parseMulDiv() {
        ASTNode left = parseFactor(); // Parsea primero los factores

        // Mientras haya operadores de multiplicación o división
        while (currentToken != null && (currentToken.getType().equals("MULTIPLY") || currentToken.getType().equals("DIVIDE"))) {
            String operator = currentToken.getValue(); // Obtiene el operador
            advance(); // Avanza al siguiente token
            ASTNode right = parseFactor(); // Parsea el siguiente factor
            left = new BinaryOperation(left, operator, right); // Crea un nodo AST de operación binaria
        }
        return left; // Devuelve el nodo AST de la expres

