
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java Main archivo.minilang");
            return;
        }

        String filename = args[0];
        String sourceCode = "";

        try {
            sourceCode = new String(Files.readAllBytes(Paths.get(filename)));
        } catch (Exception e) {
            System.out.println("Error al leer el archivo: " + filename + " (" + e.getMessage() + ")");
            return;
        }

        try {
            Lexer lexer = new Lexer(sourceCode);
            List<Token> tokens = lexer.tokenize();
            // Puedes descomentar para depuración:
            // for (Token t : tokens) System.out.println(t);

            Parser parser = new Parser(tokens);
            parser.parse();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
