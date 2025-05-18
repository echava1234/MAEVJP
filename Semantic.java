// =============================================================================
// FASE 3: Análisis Semántico (Semantic Analysis)
// =============================================================================


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SemanticAnalyzer {
    private Map<String, String> symbol_table;
    private List<String> errors;

    public SemanticAnalyzer() {
        this.symbol_table = new HashMap<>();
        this.errors = new ArrayList<>();
    }

    public boolean analyze(ASTNode ast) {
        visit_node(ast);
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

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
        } else if (node instanceof StringLiteral) {
            return visit_StringLiteral((StringLiteral) node);
        } else {
            return generic_visit(node);
        }
    }

    private String generic_visit(ASTNode node) {
        return null;
    }

    private String visit_Program(Program node) {
        for (ASTNode statement : node.statements) {
            visit_node(statement);
        }
        return null;
    }

    private String visit_BinaryOperation(BinaryOperation node) {
        String left_type = visit_node(node.left);
        String right_type = visit_node(node.right);

        if (!left_type.equals("number") || !right_type.equals("number")) {
            errors.add("Error semántico: Operación aritmética con valores no numéricos");
        }
        return "number";
    }

    private String visit_Number(Number node) {
        return "number";
    }

    private String visit_StringLiteral(StringLiteral node) {
        return "string";
    }

    private String visit_Identifier(Identifier node) {
        if (!symbol_table.containsKey(node.name)) {
            errors.add("Error semántico: Variable '" + node.name + "' no definida");
            return "unknown";
        }
        return symbol_table.get(node.name);
    }

    private String visit_Assignment(Assignment node) {
        String value_type = visit_node(node.value);
        symbol_table.put(node.name, value_type);
        return null;
    }

    private String visit_Print(Print node) {
        visit_node(node.expression);
        return null;
    }

    private String visit_IfStatement(IfStatement node) {
        String condition_type = visit_node(node.condition);

        if (!condition_type.equals("boolean")) {
            errors.add("Error semántico: La condición del if debe ser una expresión booleana");
        }

        for (ASTNode statement : node.ifBlock) {
            visit_node(statement);
        }

        if (node.elseBlock != null) {
            for (ASTNode statement : node.elseBlock) {
                visit_node(statement);
            }
        }
        return null;
    }

    private String visit_WhileStatement(WhileStatement node) {
        String condition_type = visit_node(node.condition);

        if (!condition_type.equals("boolean")) {
            errors.add("Error semántico: La condición del while debe ser una expresión booleana");
        }

        for (ASTNode statement : node.block) {
            visit_node(statement);
        }
        return null;
    }

    private String visit_Comparison(Comparison node) {
        String left_type = visit_node(node.left);
        String right_type = visit_node(node.right);

        if (!left_type.equals(right_type)) {
            errors.add("Error semántico: Comparación entre tipos diferentes");
        }
        return "boolean";
    }
}

