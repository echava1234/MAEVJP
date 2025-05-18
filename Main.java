import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

// Clase principal del compilador
public class Main {

    public static void main(String[] args) {
        // Verifica que se proporcione el nombre del archivo fuente como argumento
        if (args.length != 1) {
            System.out.println("Uso: java Main archivo.minilang");
            return;
        }

        String fileName = args[0]; // Obtiene el nombre del archivo
        String sourceCode = ""; // Almacena el código fuente del archivo

        // Lee el código fuente del archivo
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                sourceCode += line + "\n"; // Agrega cada línea al código fuente
            }
            reader.close(); // Cierra el lector del archivo
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage()); // Imprime el error si ocurre una excepción
            return; // Termina la ejecución si no se puede leer el archivo
        }

        // FASE 1: Análisis Léxico
        Lexer lexer = new Lexer(sourceCode); // Crea un objeto Lexer para el análisis léxico
        List<Token> tokens = lexer.tokenize(); // Obtiene la lista de tokens del código fuente

        // FASE 2: Análisis Sintáctico
        Parser parser = new Parser(tokens); // Crea un objeto Parser para el análisis sintáctico
        Program program = parser.parse(); // Obtiene el AST (Árbol de Sintaxis Abstracta) del código fuente

        // FASE 3: Análisis Semántico
        SemanticAnalyzer analyzer = new SemanticAnalyzer(); // Crea un objeto SemanticAnalyzer para el análisis semántico
        boolean isValid = analyzer.analyze(program); // Realiza el análisis semántico del AST

        // Imprime el resultado del análisis semántico
        if (isValid) {
            System.out.println("El programa es semánticamente correcto.");
        } else {
            System.out.println("Se han encontrado errores semánticos:");
            for (String error : analyzer.getErrors()) {
                System.out.println(error); // Imprime cada error semántico encontrado
            }
        }
    }
}
