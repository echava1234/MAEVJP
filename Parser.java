// =============================================================================
// FASE 2: Análisis Sintáctico (Parser)
// =============================================================================

import java.util.List;

class SyntaxError extends RuntimeException {
    public SyntaxError(String message) {
        super(message);
    }
}

public class Parser {
    private List<Token> tokens;
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    public void parse() {
        while (!isAtEnd()) {
            parseStatement();
        }
        System.out.println("El programa es sintácticamente correcto.");
    }

    // =========================
    // STATEMENT LEVEL
    // =========================
    private void parseStatement() {
        Token t = currentToken();
        if (t == null) return;

        switch (t.getType()) {
            case "LET":
                parseLetStatement();
                break;
            case "PRINT":
                parsePrintStatement();
                break;
            case "WHILE":
                parseWhileStatement();
                break;
            case "IF":
                parseIfStatement();
                break;
            case "IDENTIFIER":
                parseAssignmentStatement();
                break;
            default:
                error("Sentencia no válida", t);
        }
    }

    private void parseLetStatement() {
        consume("LET");
        consume("IDENTIFIER");
        consume("ASSIGN");
        parseExpression();
        consume("SEMICOLON");
    }

    private void parseAssignmentStatement() {
        consume("IDENTIFIER");
        consume("ASSIGN");
        parseExpression();
        consume("SEMICOLON");
    }

    private void parsePrintStatement() {
        consume("PRINT");
        consume("LPAREN");
        parseExpression();
        while (match("COMMA")) {
            parseExpression();
        }
        consume("RPAREN");
        consume("SEMICOLON");
    }

    private void parseWhileStatement() {
        consume("WHILE");
        consume("LPAREN");
        parseExpression();
        consume("RPAREN");
        consume("LBRACE");
        while (!check("RBRACE") && !isAtEnd()) {
            parseStatement();
        }
        consume("RBRACE");
    }

    private void parseIfStatement() {
        consume("IF");
        consume("LPAREN");
        parseExpression();
        consume("RPAREN");
        consume("LBRACE");
        while (!check("RBRACE") && !isAtEnd()) {
            parseStatement();
        }
        consume("RBRACE");
        if (match("ELSE")) {
            consume("LBRACE");
            while (!check("RBRACE") && !isAtEnd()) {
                parseStatement();
            }
            consume("RBRACE");
        }
    }

    // =========================
    // EXPRESSION LEVEL (simple, puedes extenderlo)
    // =========================
    private void parseExpression() {
        parseEquality();
    }

    private void parseEquality() {
        parseComparison();
        while (match("EQ")) {
            parseComparison();
        }
    }

    private void parseComparison() {
        parseTerm();
        while (match("LE") || match("GE") || match("LT") || match("GT")) {
            parseTerm();
        }
    }

    private void parseTerm() {
        parseFactor();
        while (match("PLUS") || match("MINUS")) {
            parseFactor();
        }
    }

    private void parseFactor() {
        parseUnary();
        while (match("MULTIPLY") || match("DIVIDE")) {
            parseUnary();
        }
    }

    private void parseUnary() {
        if (match("MINUS")) {
            parseUnary();
        } else {
            parsePrimary();
        }
    }

    private void parsePrimary() {
        if (match("NUMBER") || match("STRING")) {
            // Ok
        } else if (match("IDENTIFIER")) {
            // Ok
        } else if (match("LPAREN")) {
            parseExpression();
            consume("RPAREN");
        } else {
            Token t = currentToken();
            error("Expresión no válida", t);
        }
    }

    // =========================
    // UTILITY METHODS
    // =========================

    private Token consume(String type) {
        Token t = currentToken();
        if (t != null && t.getType().equals(type)) {
            pos++;
            return t;
        }
        error("Se esperaba '" + type + "'", t);
        return null;
    }

    private boolean match(String type) {
        Token t = currentToken();
        if (t != null && t.getType().equals(type)) {
            pos++;
            return true;
        }
        return false;
    }

    private boolean check(String type) {
        Token t = currentToken();
        return t != null && t.getType().equals(type);
    }

    private Token currentToken() {
        if (pos >= tokens.size()) return null;
        return tokens.get(pos);
    }

    private boolean isAtEnd() {
        return pos >= tokens.size();
    }

    private void error(String msg, Token t) {
        String location = (t != null) ? " en la línea " + t.getLine() + ", columna " + t.getColumn() : "";
        throw new SyntaxError(msg + location);
    }
}

