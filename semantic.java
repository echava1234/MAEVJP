import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Clase para representar errores semánticos
class SemanticError extends RuntimeException {
    public SemanticError(String message) {
        super(message);
    }
}

// Clase para el analizador semántico
class SemanticAnalyzer {
    private Map<String, String> symbol_table; // Tabla de símbolos para almacenar variables (nombre -> tipo)
    private List<String> errors;             // Lista para almacenar los errores semánticos encontrados

    public SemanticAnalyzer() {
        this.symbol_table = new HashMap<>();
        this.errors = new ArrayList<>();
    }

    // Método principal para analizar el AST
    public boolean analyze(ASTNode ast) {
        visit_node(ast);             // Inicia el recorrido del AST
        return errors.isEmpty(); // Devuelve true si no hay errores, false en caso contrario
    }

    public List<String> getErrors() {
        return errors;
    }

    // Método para visitar un nodo del AST y llamar al método de visita específico
    private String visit_node(ASTNode node) {
        if (node instanceof Program) {
            return visit_Program((Program) node);
        } else if (node instanceof BinaryOperation) {
            return visit_BinaryOperation((BinaryOperation) node);
        } else if (node instanceof Number) {
            return visit_Number((Number) node);
        } else if (node instanceof Identifier) {
            return visit_Identifier((Identifier) node);
        } else if (node instanceof Assignment) {
            return visit_Assignment((Assignment) node);
        } else if (node instanceof Print) {
            return visit_Print((Print) node);
        } else if (node instanceof IfStatement) {
            return visit_IfStatement((IfStatement) node);
        } else if (node instanceof WhileStatement) {
            return visit_WhileStatement((WhileStatement) node);
        } else if (node instanceof Comparison) {
            return visit_Comparison((Comparison) node);
        } else {
            return generic_visit(node);
        }
    }

    // Método para nodos que no tienen un método de visita específico
    private String generic_visit(ASTNode node) {
        return null; // Opcional:  puedes lanzar una excepción si se encuentra un nodo desconocido
    }

    // Método para visitar un nodo Program
    private String visit_Program(Program node) {
        for (ASTNode statement : node.statements) {
            visit_node(statement);
        }
        return null;
    }

    // Método para visitar un nodo BinaryOperation
    private String visit_BinaryOperation(BinaryOperation node) {
        String left_type = visit_node(node.left);
        String right_type = visit_node(node.right);

        // Verifica que los operandos sean numéricos
        if (!left_type.equals("number") || !right_type.equals("number")) {
            errors.add("Error semántico: Operación aritmética con valores no numéricos");
        }
        return "number";
    }

    // Método para visitar un nodo Number
    private String visit_Number(Number node) {
        return "number";
    }

    // Método para visitar un nodo Identifier
    private String visit_Identifier(Identifier node) {
        if (!symbol_table.containsKey(node.name)) {
            errors.add("Error semántico: Variable '" + node.name + "' no definida");
            return "unknown";
        }
        return symbol_table.get(node.name);
    }

    // Método para visitar un nodo Assignment
    private String visit_Assignment(Assignment node) {
        String value_type = visit_node(node.value);
        symbol_table.put(node.name, value_type);
        return null;
    }

    // Método para visitar un nodo Print
    private String visit_Print(Print node) {
        visit_node(node.expression);
        return null;
    }

    // Método para visitar un nodo IfStatement
    private String visit_IfStatement(IfStatement node) {
        String condition_type = visit_node(node.condition);

        // Verifica que la condición sea de tipo boolean
        if (!condition_type.equals("boolean")) {
            errors.add("Error semántico: La condición del if debe ser una expresión booleana");
        }

        // Visita el bloque if
        for (ASTNode statement : node.ifBlock) {
            visit_node(statement);
        }

        // Visita el bloque else si existe
        if (node.elseBlock != null) {
            for (ASTNode statement : node.elseBlock) {
                visit_node(statement);
            }
        }
        return null;
    }

    // Método para visitar un nodo WhileStatement
    private String visit_WhileStatement(WhileStatement node) {
        String condition_type = visit_node(node.condition);

        // Verifica que la condición sea de tipo boolean
        if (!condition_type.equals("boolean")) {
            errors.add("Error semántico: La condición del while debe ser una expresión booleana");
        }

        // Visita el bloque while
        for (ASTNode statement : node.block) {
            visit_node(statement);
        }
        return null;
    }

    // Método para visitar un nodo Comparison
    private String visit_Comparison(Comparison node) {
        String left_type = visit_node(node.left);
        String right_type = visit_node(node.right);

        // Verifica que los operandos sean del mismo tipo
        if (!left_type.equals(right_type)) {
            errors.add("Error semántico: Comparación entre tipos diferentes");
        }
        return "boolean";
    }
}

// Clase principal para ejecutar el analizador semántico (y el parser)
public class Main {
    public static void main(String[] args) {
        // Lista de tokens de ejemplo (simula la salida del analizador léxico)
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
        tokens.add(new Token("STRING", "\"a es 10\"", 3, 11));
        tokens.add(new Token("SEMICOLON", ";", 3, 21));
        tokens.add(new Token("RBRACE", "}", 4, 1));
        tokens.add(new Token("ELSE", "else", 4, 2));
        tokens.add(new Token("LBRACE", "{", 4, 7));
        tokens.add(new Token("PRINT", "print", 5, 5));
        tokens.add(new Token("STRING", "\"a no es 10\"", 5, 11));
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

        Parser parser = new Parser(tokens);
        Program program = parser.parse();

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        boolean isValid = analyzer.analyze(program);

        if (isValid) {
            System.out.println("El programa es semánticamente correcto.");
        } else {
            System.out.println("Se han encontrado errores semánticos:");
            for (String error : analyzer.getErrors()) {
                System.out.println(error);
            }
        }
    }
}
