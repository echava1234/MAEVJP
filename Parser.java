// =============================================================================
// FASE 2: Análisis Sintáctico (Parser)
// =============================================================================

import java.util.ArrayList;
import java.util.List;

class Parser {
    private List<Token> tokens;
    private int currentTokenIndex;
    private Token currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.currentToken = tokens != null && !tokens.isEmpty() ? tokens.get(0) : null;
    }

    private void advance() {
        currentTokenIndex++;
        if (currentTokenIndex < tokens.size()) {
            currentToken = tokens.get(currentTokenIndex);
        } else {
            currentToken = null;
        }
    }

    public Program parse() {
        List<ASTNode> statements = new ArrayList<>();

        while (currentToken != null) {
            ASTNode statement = parseStatement();
            statements.add(statement);
        }
        return new Program(statements);
    }

    private ASTNode parseStatement() {
        if (currentToken.getType().equals("IDENTIFIER")) {
            return parseAssignment();
        } else if (currentToken.getType().equals("PRINT")) {
            return parsePrint();
        } else if (currentToken.getType().equals("IF")) {
            return parseIf();
        } else if (currentToken.getType().equals("WHILE")) {
            return parseWhile();
        } else {
            throw new SyntaxError("Sentencia no válida en la línea " + currentToken.getLine() + ", columna " + currentToken.getColumn());
        }
    }

    private ASTNode parseAssignment() {
        String varName = currentToken.getValue();
        advance();

        if (currentToken == null || !currentToken.getType().equals("ASSIGN")) {
            throw new SyntaxError("Se esperaba '=' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance();

        ASTNode expression = parseExpression();

        if (currentToken == null || !currentToken.getType().equals("SEMICOLON")) {
            throw new SyntaxError("Se esperaba ';' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance();
        return new Assignment(varName, expression);
    }

    private ASTNode parsePrint() {
        advance();

        ASTNode expression = parseExpression();

        if (currentToken == null || !currentToken.getType().equals("SEMICOLON")) {
            throw new SyntaxError("Se esperaba ';' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance();
        return new Print(expression);
    }

    private ASTNode parseIf() {
        advance();

        if (currentToken == null || !currentToken.getType().equals("LPAREN")) {
            throw new SyntaxError("Se esperaba '(' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance();

        ASTNode condition = parseComparison();

        if (currentToken == null || !currentToken.getType().equals("RPAREN")) {
            throw new SyntaxError("Se esperaba ')' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance();

        if (currentToken == null || !currentToken.getType().equals("LBRACE")) {
            throw new SyntaxError("Se esperaba '{' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance();

        List<ASTNode> ifBlock = new ArrayList<>();
        while (currentToken != null && !currentToken.getType().equals("RBRACE")) {
            ASTNode statement = parseStatement();
            ifBlock.add(statement);
        }

        if (currentToken == null || !currentToken.getType().equals("RBRACE")) {
            throw new SyntaxError("Se esperaba '}' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance();

        List<ASTNode> elseBlock = null;
        if (currentToken != null && currentToken.getType().equals("ELSE")) {
            advance();
            if (currentToken == null || !currentToken.getType().equals("LBRACE")) {
                throw new SyntaxError("Se esperaba '{' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
            }
            advance();
            elseBlock = new ArrayList<>();
            while (currentToken != null && !currentToken.getType().equals("RBRACE")) {
                ASTNode statement = parseStatement();
                elseBlock.add(statement);
            }
            if (currentToken == null || !currentToken.getType().equals("RBRACE")) {
                throw new SyntaxError("Se esperaba '}' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
            }
            advance();
        }
        return new IfStatement(condition, ifBlock, elseBlock);
    }

    private ASTNode parseWhile() {
        advance();

        if (currentToken == null || !currentToken.getType().equals("LPAREN")) {
            throw new SyntaxError("Se esperaba '(' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance();

        ASTNode condition = parseComparison();

        if (currentToken == null || !currentToken.getType().equals("RPAREN")) {
            throw new SyntaxError("Se esperaba ')' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance();

        if (currentToken == null || !currentToken.getType().equals("LBRACE")) {
            throw new SyntaxError("Se esperaba '{' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance();

        List<ASTNode> block = new ArrayList<>();
        while (currentToken != null && !currentToken.getType().equals("RBRACE")) {
            ASTNode statement = parseStatement();
            block.add(statement);
        }

        if (currentToken == null || !currentToken.getType().equals("RBRACE")) {
            throw new SyntaxError("Se esperaba '}' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
        }
        advance();
        return new WhileStatement(condition, block);
    }

    private ASTNode parseComparison() {
        ASTNode left = parseExpression();

        if (currentToken != null && (currentToken.getType().equals("EQUALS") || currentToken.getType().equals("GREATER") || currentToken.getType().equals("LESS"))) {
            String operator = currentToken.getValue();
            advance();
            ASTNode right = parseExpression();
            return new Comparison(left, operator, right);
        } else {
            return left;
        }
    }

    private ASTNode parseExpression() {
        return parseAddSub();
    }

    private ASTNode parseAddSub() {
        ASTNode left = parseMulDiv();

        while (currentToken != null && (currentToken.getType().equals("PLUS") || currentToken.getType().equals("MINUS"))) {
            String operator = currentToken.getValue();
            advance();
            ASTNode right = parseMulDiv();
            left = new BinaryOperation(left, operator, right);
        }
        return left;
    }

    private ASTNode parseMulDiv() {
        ASTNode left = parseFactor();

        while (currentToken != null && (currentToken.getType().equals("MULTIPLY") || currentToken.getType().equals("DIVIDE"))) {
            String operator = currentToken.getValue();
            advance();
            ASTNode right = parseFactor();
            left = new BinaryOperation(left, operator, right);
        }
        return left;
    }

    private ASTNode parseFactor() {
        Token token = currentToken;

        if (token.getType().equals("NUMBER")) {
            advance();
            return new Number(token.getValue());
        } else if (token.getType().equals("IDENTIFIER")) {
            advance();
            return new Identifier(token.getValue());
        } else if (token.getType().equals("LPAREN")) {
            advance();
            ASTNode expression = parseExpression();
            if (currentToken == null || !currentToken.getType().equals("RPAREN")) {
                throw new SyntaxError("Se esperaba ')' en la línea " + (currentToken != null ? currentToken.getLine() : "final") + ", columna " + (currentToken != null ? currentToken.getColumn() : "final"));
            }
            advance();
            return expression;
        } else if (token.getType().equals("STRING")) {
            advance();
            return new StringLiteral(token.getValue());
        } else {
            throw new SyntaxError("Token inesperado: " + token.getType() + " en la línea " + token.getLine() + ", columna " + token.getColumn());
        }
    }
}

// ASTNode.java
// Clase base para los nodos del AST
abstract class ASTNode {
}

// Program.java
// Clase para representar un programa, que contiene una lista de sentencias
class Program extends ASTNode {
    List<ASTNode> statements;

    public Program(List<ASTNode> statements) {
        this.statements = statements;
    }
}

// BinaryOperation.java
// Clase para representar una operación binaria
class BinaryOperation extends ASTNode {
    ASTNode left;
    String operator;
    ASTNode right;

    public BinaryOperation(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}

// Number.java
// Clase para representar un número
class Number extends ASTNode {
    double value;

    public Number(String value) {
        this.value = Double.parseDouble(value);
    }
}

// StringLiteral.java
class StringLiteral extends ASTNode {
    String value;

    public StringLiteral(String value) {
        this.value = value;
    }
}

// Identifier.java
// Clase para representar un identificador
class Identifier extends ASTNode {
    String name;

    public Identifier(String name) {
        this.name = name;
    }
}

// Assignment.java
// Clase para representar una asignación
class Assignment extends ASTNode {
    String name;
    ASTNode value;

    public Assignment(String name, ASTNode value) {
        this.name = name;
        this.value = value;
    }
}

// Print.java
// Clase para representar una sentencia de impresión
class Print extends ASTNode {
    ASTNode expression;

    public Print(ASTNode expression) {
        this.expression = expression;
    }
}

// IfStatement.java
class IfStatement extends ASTNode {
    ASTNode condition;
    List<ASTNode> ifBlock;
    List<ASTNode> elseBlock;

    public IfStatement(ASTNode condition, List<ASTNode> ifBlock, List<ASTNode> elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }
}

// WhileStatement.java
class WhileStatement extends ASTNode {
    ASTNode condition;
    List<ASTNode> block;

    public WhileStatement(ASTNode condition, List<ASTNode> block) {
        this.condition = condition;
        this.block = block;
    }
}

// Comparison.java
class Comparison extends ASTNode {
    ASTNode left;
    String operator;
    ASTNode right;

    public Comparison(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}

