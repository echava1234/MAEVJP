import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

// -----------------------------------------------------------------------------
// Clase Main: punto de entrada del compilador MAEVJP
// Orquesta la lectura, tokenización y análisis sintáctico del código fuente
// -----------------------------------------------------------------------------
public class Main {
    public static void main(String[] args) {
        // Comprueba que se reciba un archivo fuente como argumento
        if (args.length != 1) {
            System.out.println("Uso: java Main archivo.minilang");
            return;
        }

        String filename = args[0];
        String sourceCode = "";

        // Intenta leer el archivo fuente completo
        try {
            sourceCode = new String(Files.readAllBytes(Paths.get(filename)));
        } catch (Exception e) {
            System.out.println("Error al leer el archivo: " + filename + " (" + e.getMessage() + ")");
            return;
        }

        // Ejecuta el análisis léxico y sintáctico, reportando cualquier error
        try {
            Lexer lexer = new Lexer(sourceCode);                // Genera el lexer para el código fuente
            List<Token> tokens = lexer.tokenize();              // Obtiene la lista de tokens
            // Descomenta la siguiente línea para ver los tokens generados:
            // for (Token t : tokens) System.out.println(t);

            Parser parser = new Parser(tokens);                 // Genera el parser con la lista de tokens
            parser.parse();                                     // Inicia el análisis sintáctico
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());     // Reporta errores de análisis
        }
    }
}
