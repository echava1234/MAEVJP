// =============================================================================
// FASE 2: Análisis Sintáctico (Parser)
// =============================================================================

import java.util.List;

// -----------------------------------------------------------------------------
// Clase SyntaxError
// Define una excepción específica para errores de sintaxis
// -----------------------------------------------------------------------------
class SyntaxError extends RuntimeException {
    public SyntaxError(String message) {
        super(message);
    }
}

// -----------------------------------------------------------------------------
// Clase Parser
// Verifica que la secuencia de tokens sigue las reglas gramaticales del lenguaje
// Usa técnicas de parsing recursivo descendente
// -----------------------------------------------------------------------------
public class Parser {
    // Lista de tokens de entrada (ya generados por el Lexer)
    private List<Token> tokens;
    // Índice del token actual en la lista
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    // -------------------------------------------------------------------------
    // parse(): método principal, recorre todas las sentencias
    // -------------------------------------------------------------------------
    public void parse() {
        while (!isAtEnd()) {
            parseStatement(); // Llama a parseStatement repetidamente (programa = lista de sentencias)
        }
        System.out.println("El programa es sintácticamente correcto.");
    }

    // =========================
    // STATEMENT LEVEL
    // =========================

    // Determina el tipo de sentencia según el token actual y llama al método apropiado
    private void parseStatement() {
        Token t = currentToken();
        if (t == null) return; // Fin de archivo/token

        switch (t.getType()) {
            case "LET":
                parseLetStatement();        // Declaración de variable
                break;
            case "PRINT":
                parsePrintStatement();      // Sentencia de impresión
                break;
            case "WHILE":
                parseWhileStatement();      // Bucle while
                break;
            case "IF":
                parseIfStatement();         // Condicional if/else
                break;
            case "IDENTIFIER":
                parseAssignmentStatement(); // Reasignación a variable ya declarada
                break;
            default:
                error("Sentencia no válida", t); // Si no es ninguna conocida, error de sintaxis
        }
    }

    // let x = expr;
    private void parseLetStatement() {
        consume("LET");
        consume("IDENTIFIER");
        consume("ASSIGN");
        parseExpression();      // Puede ser un número, string, variable, expresión aritmética, etc.
        consume("SEMICOLON");
    }

    // x = expr;
    private void parseAssignmentStatement() {
        consume("IDENTIFIER");
        consume("ASSIGN");
        parseExpression();
        consume("SEMICOLON");
    }

    // print(expr1, expr2, ...);
    private void parsePrintStatement() {
        consume("PRINT");
        consume("LPAREN");
        parseExpression();
        while (match("COMMA")) { // Permite múltiples expresiones separadas por coma
            parseExpression();
        }
        consume("RPAREN");
        consume("SEMICOLON");
    }

    // while (expr) { sentencias }
    private void parseWhileStatement() {
        consume("WHILE");
        consume("LPAREN");
        parseExpression(); // Condición
        consume("RPAREN");
        consume("LBRACE");
        while (!check("RBRACE") && !isAtEnd()) {
            parseStatement();  // Puede haber cero o más sentencias dentro del while
        }
        consume("RBRACE");
    }

    // if (expr) { sentencias } else { sentencias }
    private void parseIfStatement() {
        consume("IF");
        consume("LPAREN");
        parseExpression(); // Condición
        consume("RPAREN");
        consume("LBRACE");
        while (!check("RBRACE") && !isAtEnd()) {
            parseStatement();
        }
        consume("RBRACE");
        if (match("ELSE")) { // else es opcional
            consume("LBRACE");
            while (!check("RBRACE") && !isAtEnd()) {
                parseStatement();
            }
            consume("RBRACE");
        }
    }

    // =========================
    // EXPRESSION LEVEL
    // =========================

    // Expresión principal: delega a parseEquality (lógica extensible)
    private void parseExpression() {
        parseEquality();
    }

    // expr == expr
    private void parseEquality() {
        parseComparison();
        while (match("EQ")) {
            parseComparison();
        }
    }

    // expr < expr, expr > expr, expr <= expr, expr >= expr
    private void parseComparison() {
        parseTerm();
        while (match("LE") || match("GE") || match("LT") || match("GT")) {
            parseTerm();
        }
    }

    // expr + expr, expr - expr
    private void parseTerm() {
        parseFactor();
        while (match("PLUS") || match("MINUS")) {
            parseFactor();
        }
    }

    // expr * expr, expr / expr
    private void parseFactor() {
        parseUnary();
        while (match("MULTIPLY") || match("DIVIDE")) {
            parseUnary();
        }
    }

    // Maneja el operador unario menos: -expr
    private void parseUnary() {
        if (match("MINUS")) {
            parseUnary();
        } else {
            parsePrimary();
        }
    }

    // Literales, variables y expresiones entre paréntesis
    private void parsePrimary() {
        if (match("NUMBER") || match("STRING")) {
            // Ok, literal válido
        } else if (match("IDENTIFIER")) {
            // Ok, variable (debe existir semánticamente)
        } else if (match("LPAREN")) {
            parseExpression();
            consume("RPAREN");
        } else {
            Token t = currentToken();
            error("Expresión no válida", t);
        }
    }

    // =========================
    // MÉTODOS UTILITARIOS
    // =========================

    // Consume el token esperado y avanza. Si no coincide, lanza error detallando la ubicación
    private Token consume(String type) {
        Token t = currentToken();
        if (t != null && t.getType().equals(type)) {
            pos++;
            return t;
        }
        error("Se esperaba '" + type + "'", t);
        return null; // Nunca se llega aquí, error lanza excepción
    }

    // Si el token esperado está presente, avanza y retorna true. Si no, no avanza.
    private boolean match(String type) {
        Token t = currentToken();
        if (t != null && t.getType().equals(type)) {
            pos++;
            return true;
        }
        return false;
    }

    // Verifica si el token actual es del tipo dado, sin consumirlo.
    private boolean check(String type) {
        Token t = currentToken();
        return t != null && t.getType().equals(type);
    }

    // Devuelve el token actual o null si se terminó la lista
    private Token currentToken() {
        if (pos >= tokens.size()) return null;
        return tokens.get(pos);
    }

    // ¿Llegamos al final de la lista de tokens?
    private boolean isAtEnd() {
        return pos >= tokens.size();
    }

    // Lanza un error de sintaxis con mensaje y ubicación (línea y columna)
    private void error(String msg, Token t) {
        String location = (t != null) ? " en la línea " + t.getLine() + ", columna " + t.getColumn() : "";
        throw new SyntaxError(msg + location);
    }
}

