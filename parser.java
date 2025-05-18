import java.util.ArrayList;
import java.util.List;

// Definición de la clase base para los nodos del AST (Árbol de Sintaxis Abstracta)
// Todas las clases que representan construcciones del lenguaje heredarán de esta clase.
abstract class ASTNode {
}

// Clase para representar un programa completo, que contiene una lista de sentencias.
class Program extends ASTNode {
    List<ASTNode> statements; // Lista de nodos AST que representan las sentencias del programa.

    public Program(List<ASTNode> statements) {
        this.statements = statements;
    }
}

// Clase para representar una operación binaria, como suma, resta, multiplicación, etc.
class BinaryOperation extends ASTNode {
    ASTNode left;      // Nodo AST para el operando izquierdo.
    String operator;    // El operador de la operación (+, -, *, /).
    ASTNode right;     // Nodo AST para el operando derecho.

    public BinaryOperation(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}

// Clase para representar un número, ya sea entero o decimal.
class Number extends ASTNode {
    double value;    // El valor numérico.

    public Number(String value) {
        this.value = Double.parseDouble(value); // Convierte la cadena a un valor double.
    }
}

// Clase para representar un identificador, que es el nombre de una variable.
class Identifier extends ASTNode {
    String name;     // El nombre de la variable.

    public Identifier(String name) {
        this.name = name;
    }
}

// Clase para representar una asignación de variable (ej., a = 10).
class Assignment extends ASTNode {
    String name;    // El nombre de la variable a la que se asigna el valor.
    ASTNode value;   // El nodo AST que representa el valor que se asigna a la variable.

    public Assignment(String name, ASTNode value) {
        this.name = name;
        this.value = value;
    }
}

// Clase para representar una sentencia de impresión (ej., print a).
class Print extends ASTNode {
    ASTNode expression;  // El nodo AST que representa la expresión que se va a imprimir.

    public Print(ASTNode expression) {
        this.expression = expression;
    }
}

// Clase para representar una sentencia if (ej., if a > 10 { ... } else { ... }).
class IfStatement extends ASTNode {
    ASTNode condition;    // El nodo AST que representa la condición del if.
    List<ASTNode> ifBlock;    // La lista de sentencias dentro del bloque if.
    List<ASTNode> elseBlock;  // La lista de sentencias dentro del bloque else (puede ser nulo).

    public IfStatement(ASTNode condition, List<ASTNode> ifBlock, List<ASTNode> elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }
}

// Clase para representar una sentencia while (ej., while a > 0 { ... }).
class WhileStatement extends ASTNode {
    ASTNode condition;  // El nodo AST que representa la condición del while.
    List<ASTNode> block;    // La lista de sentencias dentro del bloque while.

    public WhileStatement(ASTNode condition, List<ASTNode> block) {
        this.condition = condition;
        this.block = block;
    }
}

// Clase para representar una comparación (ej., a == 10, a > 5, a < b).
class Comparison extends ASTNode {
    ASTNode left;      // Nodo AST para el operando izquierdo.
    String operator;    // El operador de comparación (==, >, <).
    ASTNode right;     // Nodo AST para el operando derecho.

    public Comparison(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}

// Clase para el analizador sintáctico, que construye el AST a partir de una lista de tokens.
class Parser {
    private List<Token> tokens;         // La lista de tokens generada por el analizador léxico.
    private int currentTokenIndex;    // El índice del token actual que se está procesando.
    private Token currentToken;       // El token actual que se está procesando.

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.currentToken = tokens != null && !tokens.isEmpty() ? tokens.get(0) : null; // Obtiene el primer token, si existe.
    }

    // Método privado para avanzar al siguiente token en la lista.
    private void advance() {
        currentTokenIndex++;
        if (currentTokenIndex < tokens.size()) {
            currentToken = tokens.get(currentTokenIndex);
        } else {
            currentToken = null; // Si se llega al final de la lista, currentToken se establece en null.
        }
    }

    // Método principal para analizar los tokens y construir el AST.
    public Program parse() {
        List<ASTNode> statements = new ArrayList<>(); // Lista para almacenar las sentencias analizadas.

        while (currentToken != null) {
            ASTNode statement = parseStatement(); // Analiza una sola sentencia.
            statements.add(statement);             // Agrega la sentencia analizada a la lista.
        }
        return new Program(statements); // Devuelve el nodo raíz del AST, que representa el programa completo.
    }

    // Método privado para analizar una sola sentencia.
    private ASTNode parseStatement() {
        if (currentToken.getType().equals("IDENTIFIER")) {
            return parseAssignment(); // Si el token actual es un identificador, es una asignación.
        } else if (currentToken.getType().equals("PRINT")) {
            return parsePrint();       // Si es "print", es una sentencia de impresión.
        } else if (currentToken.getType().equals("IF")) {
            return parseIf();          // Si es "if", es una sentencia condicional.
        } else if (currentToken.getType().equals("WHILE")) {
            return parseWhile();        // Si es "while", es un bucle.
        } else {
            // Si no coincide con ningún tipo de sentencia conocido, hay un error de sintaxis.
            throw new SyntaxError("Sentencia no válida en la línea " + currentToken.getLine() + ", columna " + currentToken.getColumn());
        }
    }

    // Método privado para analizar una asignación de variable.
    private ASTNode parseAssignment() {
        String varName = currentToken.getValue(); // Obtiene el nombre de la variable.
        advance();                           // Consume el identificador (nombre de la variable).

        if (currentToken == null || !currentToken.getType().equals("ASSIGN")) {
            // Después de un identificador en una asignación, se espera un '='.
            throw new SyntaxError("Se esperaba '=' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Consume el '='.

        ASTNode expression = parseExpression(); // Analiza la expresión que se asigna a la variable.

        if (currentToken == null || !currentToken.getType().equals("SEMICOLON")) {
            // Después de la expresión en una asignación, se espera un ';'.
            throw new SyntaxError("Se esperaba ';' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Consume el ';'.
        return new Assignment(varName, expression); // Devuelve el nodo AST de asignación.
    }

    // Método privado para analizar una sentencia print.
    private ASTNode parsePrint() {
        advance(); // Consume el 'print'.

        ASTNode expression = parseExpression(); // Analiza la expresión que se va a imprimir.

        if (currentToken == null || !currentToken.getType().equals("SEMICOLON")) {
            // Después de la expresión en un print, se espera un ';'.
            throw new SyntaxError("Se esperaba ';' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Consume el ';'.
        return new Print(expression); // Devuelve el nodo AST de print.
    }

    // Método privado para analizar una sentencia if.
    private ASTNode parseIf() {
        advance(); // Consume el 'if'.

        if (currentToken == null || !currentToken.getType().equals("LPAREN")) {
            // Después de 'if', se espera un '('.
            throw new SyntaxError("Se esperaba '(' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Consume el '('.

        ASTNode condition = parseComparison(); // Analiza la condición del if.

        if (currentToken == null || !currentToken.getType().equals("RPAREN")) {
            // Después de la condición del if, se espera un ')'.
            throw new SyntaxError("Se esperaba ')' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Consume el ')'.

        if (currentToken == null || !currentToken.getType().equals("LBRACE")) {
            // Después de ')', se espera un '{'.
            throw new SyntaxError("Se esperaba '{' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Consume el '{'.

        List<ASTNode> ifBlock = new ArrayList<>(); // Lista para las sentencias del bloque if.
        while (currentToken != null && !currentToken.getType().equals("RBRACE")) {
            ASTNode statement = parseStatement(); // Analiza cada sentencia dentro del bloque if.
            ifBlock.add(statement);
        }

        if (currentToken == null || !currentToken.getType().equals("RBRACE")) {
            // Se espera un '}' al final del bloque if.
            throw new SyntaxError("Se esperaba '}' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Consume el '}'.

        // Manejo de la parte else (opcional).
        List<ASTNode> elseBlock = null;
        if (currentToken != null && currentToken.getType().equals("ELSE")) {
            advance(); // Consume el 'else'.
            if (currentToken == null || !currentToken.getType().equals("LBRACE")) {
                throw new SyntaxError("Se esperaba '{' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
            }
            advance(); // Consume el '{'
            elseBlock = new ArrayList<>();
            while (currentToken != null && !currentToken.getType().equals("RBRACE")) {
                ASTNode statement = parseStatement();
                elseBlock.add(statement);
            }
            if (currentToken == null || !currentToken.getType().equals("RBRACE")) {
                throw new SyntaxError("Se esperaba '}' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
            }
            advance(); // Consume el '}'
        }
        return new IfStatement(condition, ifBlock, elseBlock); // Devuelve el nodo AST de if.
    }

    // Método privado para analizar una sentencia while.
    private ASTNode parseWhile() {
        advance(); // Consume el 'while'.

        if (currentToken == null || !currentToken.getType().equals("LPAREN")) {
            // Después de 'while', se espera un '('.
            throw new SyntaxError("Se esperaba '(' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Consume el '('.

        ASTNode condition = parseComparison(); // Analiza la condición del while.

        if (currentToken == null || !currentToken.getType().equals("RPAREN")) {
            // Después de la condición del while, se espera un ')'.
            throw new SyntaxError("Se esperaba ')' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Consume el ')'.

        if (currentToken == null || !currentToken.getType().equals("LBRACE")) {
            // Después de ')', se espera un '{'.
            throw new SyntaxError("Se esperaba '{' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Consume el '{'.

        List<ASTNode> block = new ArrayList<>(); // Lista para las sentencias del bloque while.
        while (currentToken != null && !currentToken.getType().equals("RBRACE")) {
            ASTNode statement = parseStatement(); // Analiza cada sentencia dentro del bloque while.
            block.add(statement);
        }

        if (currentToken == null || !currentToken.getType().equals("RBRACE")) {
            // Se espera un '}' al final del bloque while.
            throw new SyntaxError("Se esperaba '}' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance(); // Consume el '}'.
        return new WhileStatement(condition, block); // Devuelve el nodo AST de while.
    }

    //Método para analizar una comparacion
    private ASTNode parseComparison() {
        ASTNode left = parseExpression(); //analiza el lado izquierdo de la comparacion

        if (currentToken != null && (currentToken.getType().equals("EQUALS") || currentToken.getType().equals("GREATER") || currentToken.getType().equals("LESS"))) {
            //si el token actual es un operador de comparacion
            String operator = currentToken.getValue(); //obtiene el valor del operador
            advance(); // Consume el operador
            ASTNode right = parseExpression(); //analiza el lado derecho de la comparacion
            return new Comparison(left, operator, right); //devuelve el nodo AST de comparacion
        } else {
            return left; //si no es un operador de comparacion, devuelve el nodo del lado izquierdo
        }
    }

    // Método privado para analizar una expresión.  La gramática de las expresiones se maneja
    // con varios métodos para asegurar la precedencia de los operadores.
    private ASTNode parseExpression() {
        return parseAddSub(); // Las expresiones comienzan con sumas y restas (tienen la menor precedencia).
    }

    // Método privado para analizar sumas y restas.
    private ASTNode parseAddSub() {
        ASTNode left = parseMulDiv(); // Primero analiza multiplicaciones y divisiones (mayor precedencia).

        while (currentToken != null && (currentToken.getType().equals("PLUS") || currentToken.getType().equals("MINUS"))) {
            // Mientras haya operadores de suma o resta, continúa analizando.
            String operator = currentToken.getValue(); // Obtiene el operador (+ o -).
            advance();                           // Consume el operador.
            ASTNode right = parseMulDiv();     // Analiza el siguiente término (después del + o -).
            left = new BinaryOperation(left, operator, right); // Crea un nodo de operación binaria para la suma o resta.
        }
        return left; // Devuelve el nodo AST que representa la suma o resta (o el resultado de la multiplicación/división si no hay sumas/restas).
    }

    // Método privado para analizar multiplicaciones y divisiones.
    private ASTNode parseMulDiv() {
        ASTNode left = parseFactor(); // Primero analiza los factores (números, identificadores, paréntesis).

        while (currentToken != null && (currentToken.getType().equals("MULTIPLY") || currentToken.getType().equals("DIVIDE"))) {
            // Mientras haya operadores de multiplicación o división, continúa analizando.
            String operator = currentToken.getValue(); // Obtiene el operador (* o /).
            advance();                              // Consume el operador.
            ASTNode right = parseFactor();         // Analiza el siguiente factor.
            left = new BinaryOperation(left, operator, right); // Crea un nodo de operación binaria para la multiplicación o división.
        }
        return left; // Devuelve el nodo AST que representa la multiplicación o división (o un factor si no hay multiplicaciones/divisiones).
    }

    // Método privado para analizar un factor, que puede ser un número, un identificador o una expresión entre paréntesis.
    private ASTNode parseFactor() {
        Token token = currentToken; // Obtiene el token actual.

        if (token.getType().equals("NUMBER")) {
            advance();           // Consume el número.
            return new Number(token.getValue()); // Crea un nodo AST de número.
        } else if (token.getType().equals("IDENTIFIER")) {
            advance();           // Consume el identificador.
            return new Identifier(token.getValue()); // Crea un nodo AST de identificador.
        } else if (token.getType().equals("LPAREN")) {
            advance();           // Consume el '('.
            ASTNode expression = parseExpression(); // Analiza la expresión dentro de los paréntesis.
            if (currentToken == null || !currentToken.getType().equals("RPAREN")) {
                // Se espera un ')' después de la expresión entre paréntesis.
                throw new SyntaxError("Se esperaba ')' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
            }
            advance();           // Consume el ')'.
            return expression; // Devuelve el nodo AST de la expresión dentro de los paréntesis.
        } else {
            // Si el token no es un número, identificador o '(', hay un error de sintaxis.
            throw new SyntaxError("Token inesperado: " + token.getType() + " en la línea " + token.getLine() + ", columna " + token.getColumn());
        }
    }
}

// Clase principal que contiene el método main para ejecutar el analizador sintáctico.
public class Main {
    public static void main(String[] args) {
        // Lista de tokens de ejemplo (simula la salida del analizador léxico).
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token("PRINT", "print", 1, 1));
        tokens.add(new Token("IDENTIFIER", "a", 1, 7));
        tokens.add(new Token("ASSIGN", "=", 1, 9));
        tokens.add(new Token("NUMBER", "10", 1, 11));
        tokens.add(new Token("SEMICOLON", ";", 1, 13));
        tokens.add(new Token("IF", "if", 2, 1));
        tokens.add(new Token("LPAREN", "(", 2, 4));
        tokens.add(new Token("IDENTIFIER", "a", 2, 5));
        tokens.add(new Token("EQUALS", "==", 2, 7));
        tokens.add(new Token("NUMBER", "10", 2, 10));
        tokens.add(new Token("RPAREN", ")", 2, 12));
        tokens.add(new Token("LBRACE", "{", 2, 13));
        tokens.add(new Token("PRINT", "print", 3, 5));
        tokens.add(new Token("STRING", "\"a es 10\"", 3, 11)); // Added String token type
        tokens.add(new Token("SEMICOLON", ";", 3, 21));
        tokens.add(new Token("RBRACE", "}", 4, 1));
        tokens.add(new Token("ELSE", "else", 4, 2));
        tokens.add(new Token("LBRACE", "{", 4, 7));
        tokens.add(new Token("PRINT", "print", 5, 5));
        tokens.add(new Token("STRING", "\"a no es 10\"", 5, 11)); // Added String token type
        tokens.add(new Token("SEMICOLON", ";", 5, 24));
        tokens.add(new Token("RBRACE", "}", 6, 1));
        tokens.add(new Token("WHILE", "while", 7, 1));
        tokens.add(new Token("LPAREN", "(", 7, 7));
        tokens.add(new Token("IDENTIFIER", "a", 7, 8));
        tokens.add(new Token("GREATER", ">", 7, 10));
        tokens.add(new Token("NUMBER", "0", 7, 12));
        tokens.add(new Token("RPAREN", ")", 7, 13));
        tokens.add(new Token("LBRACE", "{", 7, 14));
        tokens.add(new Token("IDENTIFIER", "a", 8, 4));
        tokens.add(new Token("ASSIGN", "=", 8, 6));
        tokens.add(new Token("IDENTIFIER", "a", 8, 8));
        tokens.add(new Token("MINUS", "-", 8, 10));
        tokens.add(new Token("NUMBER", "1", 8, 12));
        tokens.add(new Token("SEMICOLON", ";", 8, 13));
        tokens.add(new Token("PRINT", "print", 9, 4));
        tokens.add(new Token("IDENTIFIER", "a", 9, 10));
        tokens.add(new Token("SEMICOLON", ";", 9, 11));
        tokens.add(new Token("RBRACE", "}", 10, 1));

        Parser parser = new Parser(tokens);    // Crea una instancia del analizador sintáctico.
        Program program = parser.parse();       // Llama al método parse() para obtener el AST.

        // Imprime el AST (esto es solo para demostración, la representación del AST puede variar).
        printAST(program, 0);
    }

    // Método para imprimir el AST (recorrido recursivo).  Útil para depurar y visualizar la estructura del árbol.
    static void printAST(ASTNode node, int indent) {
        if (node == null) return; // Si el nodo es nulo, no hay nada que imprimir.

        for (int i = 0; i < indent; i++) {
            System.out.print("  "); // Indenta para mostrar la jerarquía del árbol.
        }

        if (node instanceof Program) {
            System.out.println("Program:");
            Program program = (Program) node;
            for (ASTNode statement : program.statements) {
                printAST(statement, indent + 1); // Llama recursivamente para imprimir los hijos del nodo.
            }
        } else if (node instanceof Assignment) {
            Assignment assignment = (Assignment) node;
            System.out.println("Assignment: " + assignment.name + " =");
            printAST(assignment.value, indent + 1);
        } else if (node instanceof Print) {
            Print print = (Print) node;
            System.out.println("Print:");
            printAST(print.expression, indent + 1);
        } else if (node instanceof BinaryOperation) {
            BinaryOperation binOp = (BinaryOperation) node;
            System.out.println("BinaryOperation: " + binOp.operator);
            printAST(binOp.left, indent + 1);
            printAST(binOp.right, indent + 1);
        } else if (node instanceof Number) {
            Number number = (Number) node;
            System.out.println("Number: " + number.value);
        } else if (node instanceof Identifier) {
            Identifier identifier = (Identifier) node;
            System.out.println("Identifier: " + identifier.name);
        } else if (node instanceof IfStatement) {
            IfStatement ifStatement = (IfStatement) node;
            System.out.println("IfStatement:");
            System.out.print("  Condition: ");
            printAST(ifStatement.condition, indent + 1);
            System.out.println("  If Block:");
            for (ASTNode statement : ifStatement.ifBlock) {
                printAST(statement, indent + 2);
            }
            if (ifStatement.elseBlock != null) {
                System.out.println("  Else Block:");
                for (ASTNode statement : ifStatement.elseBlock) {
                    printAST(statement, indent + 2);
                }
            }

        } else if (node instanceof WhileStatement) {
            WhileStatement whileStatement = (WhileStatement) node;
            System.out.print("WhileStatement: ");
            printAST(whileStatement.condition, indent + 1);
            System.out.println("  Block:");
            for (ASTNode statement : whileStatement.block) {
                printAST(statement, indent + 2);
            }
        } else if (node instanceof Comparison) {
            Comparison comparison = (Comparison) node;
            System.out.println("Comparison: " + comparison.operator);
            printAST(comparison.left, indent + 1);
printAST(comparison.right, indent + 1);
        }
    }
}

