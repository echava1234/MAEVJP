# Compilador MAEVJP

Este es un compilador simple para un lenguaje de programación ficticio llamado "MAEVJP", desarrollado como proyecto para la clase de Teoría de la Computación. El compilador está implementado en Java.

## Características del lenguaje MAEVJP

MAEVJPes un lenguaje sencillo que soporta:

* Operaciones aritméticas básicas (+, -, \*, /)
* Declaración y asignación de variables
* Sentencias `print` para mostrar valores
* Estructuras de control `if-else`
* Bucles `while`
* Comentarios de una línea con `//`
* Cadenas (strings)

## Componentes del compilador

El compilador consta de los siguientes componentes principales:

* **Analizador Léxico (Lexer):** Convierte el código fuente en tokens.
* **Analizador Sintáctico (Parser):** Analiza los tokens y construye.
* **Analizador Semántico (Semantic Analyzer):** Verifica la validez semántica.

## Estructura del Proyecto

El proyecto tiene la siguiente estructura:

* `Token.java`: Define la clase `Token` para representar un token.
* `Lexer.java`: Implementa el analizador léxico.

* `Program.java`, `BinaryOperation.java`, `Number.java`, `StringLiteral.java`, `Identifier.java`, `Assignment.java`, `Print.java`, `IfStatement.java`, `WhileStatement.java`, `Comparison.java`: Definen las clases para los diferentes tipos de nodos.
* `Parser.java`: Implementa el analizador sintáctico.
* `SemanticError.java`: Define la clase `SemanticError` para representar errores semánticos.
* `SemanticAnalyzer.java`: Implementa el analizador semántico.
* `Main.java`: El programa principal que coordina el proceso de compilación.

## Cómo ejecutar el compilador

1.  **Requisitos:**
    * Asegúrate de tener el JDK (Java Development Kit) instalado (versión 8 o superior).
2.  **Compilación:**
    * Guarda todos los archivos `.java` en el mismo directorio.
    * Abre una terminal o símbolo del sistema en ese directorio.
    * Compila los archivos Java con el siguiente comando:
        ```bash
        javac *.java
        ```
3.  **Ejecución:**
    * Una vez que la compilación sea exitosa, ejecuta el compilador con el siguiente comando, reemplazando `archivo.MAEVJP` con el nombre de tu archivo de código MAEVJP:
        ```bash
        java Main archivo.MAEVJP
        ```

### Ejemplo de código MAEVJP

```maevjp
// Este es un programa de prueba para MAEVJP
// Declaración de variables
x = 10;
y = 5;
z = x + y * 2;
// Imprimir resultados
print z;
// Estructura condicional
if (x > y) {
    result = x - y;
    print result;
} else {
    result = y - x;
    print result;
}
// Bucle while
count = 1;
while (count < 5) {
    print count;
    count = count + 1;
}

