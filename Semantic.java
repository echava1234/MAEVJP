// =============================================================================
// FASE 3: Análisis Semántico (Semantic Analysis)
// =============================================================================


// Clase para representar un error semántico
class SemanticError extends RuntimeException {
    public SemanticError(String message) {
        super(message);
    }
}

// Clase para el analizador semántico
class SemanticAnalyzer {
    private Map<String, String> environment; // Tabla de símbolos para almacenar variables y sus tipos

    public SemanticAnalyzer() {
        this.environment = new HashMap<>();
    }

    // Realiza el análisis semántico del AST
    public void analyze(Program program) {
        for (ASTNode statement : program.statements) {
            analyzeStatement(statement);
        }
    }

    // Analiza una sentencia del AST
    private void analyzeStatement(ASTNode node) {
        if (node instanceof Assignment) {
            analyzeAssignment((Assignment) node);
        } else if (node instanceof Print) {
            analyzePrint((Print) node);
        } else if (node instanceof IfStatement) {
            analyzeIfStatement((IfStatement) node);
        } else if (node instanceof WhileStatement) {
            analyzeWhileStatement((WhileStatement) node);
        } else if (node instanceof BinaryOperation) {
            analyzeBinaryOperation((BinaryOperation) node);
        } else if (node instanceof Comparison) {
            analyzeComparison((Comparison) node);
        } else if (node instanceof Identifier) {
            analyzeIdentifier((Identifier) node);
        } else if (node instanceof Number || node instanceof StringLiteral) {
            // Los literales no requieren análisis semántico adicional en este nivel
        } else {
            throw new SemanticError("Nodo AST desconocido: " + node.getClass().getName());
        }
    }

    // Analiza una asignación
    private void analyzeAssignment(Assignment assignment) {
        String varName = assignment.name;
        ASTNode valueNode = assignment.value;
        String valueType = getType(valueNode);
        environment.put(varName, valueType); // Almacena el tipo de la variable en el entorno
    }

    // Analiza una sentencia de impresión
    private void analyzePrint(Print printStatement) {
        analyzeStatement(printStatement.expression); // Analiza la expresión que se va a imprimir
    }

    // Analiza una sentencia if
    private void analyzeIfStatement(IfStatement ifStatement) {
        analyzeStatement(ifStatement.condition); // Analiza la condición
        if (!(getType(ifStatement.condition).equals("BOOLEAN"))) {
            throw new SemanticError("La condición del 'if' debe ser de tipo booleano.");
        }
        for (ASTNode statement : ifStatement.ifBlock) {
            analyzeStatement(statement); // Analiza las sentencias del bloque if
        }
        if (ifStatement.elseBlock != null) {
            for (ASTNode statement : ifStatement.elseBlock) {
                analyzeStatement(statement); // Analiza las sentencias del bloque else
            }
        }
    }

    // Analiza una sentencia while
    private void analyzeWhileStatement(WhileStatement whileStatement) {
        analyzeStatement(whileStatement.condition); // Analiza la condición
        if (!(getType(whileStatement.condition).equals("BOOLEAN"))) {
            throw new SemanticError("La condición del 'while' debe ser de tipo booleano.");
        }
        for (ASTNode statement : whileStatement.block) {
            analyzeStatement(statement); // Analiza las sentencias del bloque while
        }
    }

    // Analiza una operación binaria
    private void analyzeBinaryOperation(BinaryOperation binaryOperation) {
        String leftType = getType(binaryOperation.left);
        String rightType = getType(binaryOperation.right);

        if (leftType == null || rightType == null) {
            throw new SemanticError("Uno o ambos operandos de la operación binaria no tienen tipo definido.");
        }

        if (!leftType.equals(rightType)) {
            throw new SemanticError("Los operandos de la operación '" + binaryOperation.operator + "' deben ser del mismo tipo. Se encontraron: " + leftType + " y " + rightType + ".");
        }

        if (!(leftType.equals("NUMBER"))) {
            throw new SemanticError("La operación '" + binaryOperation.operator + "' solo se puede aplicar a números. Se encontraron operandos de tipo: " + leftType + ".");
        }
    }

    // Analiza una comparación
    private void analyzeComparison(Comparison comparison) {
        String leftType = getType(comparison.left);
        String rightType = getType(comparison.right);

        if (leftType == null || rightType == null) {
            throw new SemanticError("Uno o ambos operandos de la comparación no tienen tipo definido.");
        }

        if (!leftType.equals(rightType)) {
            throw new SemanticError("Los operandos de la comparación '" + comparison.operator + "' deben ser del mismo tipo. Se encontraron: " + leftType + " y " + rightType + ".");
        }

        // Las comparaciones siempre resultan en un tipo booleano, pero no necesitamos almacenarlo aquí
    }

    // Analiza un identificador (variable)
    private void analyzeIdentifier(Identifier identifier) {
        if (!environment.containsKey(identifier.name)) {
            throw new SemanticError("Variable '" + identifier.name + "' no declarada.");
        }
    }

    // Obtiene el tipo de un nodo AST
    private String getType(ASTNode node) {
        if (node instanceof Number) {
            return "NUMBER";
        } else if (node instanceof StringLiteral) {
            return "STRING";
        } else if (node instanceof Identifier) {
            return environment.get(((Identifier) node).name);
        } else if (node instanceof BinaryOperation) {
            // Suponemos que las operaciones binarias entre números dan como resultado un número
            return getType(((BinaryOperation) node).left); // El tipo será el mismo que el de los operandos (ya verificado)
        } else if (node instanceof Comparison) {
            return "BOOLEAN";
        }
        return null; // Tipo desconocido
    }
}
