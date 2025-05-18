import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java Main archivo.minilang");
            return;
        }

        String fileName = args[0];
        String sourceCode = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                sourceCode += line + "\n";
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return;
        }

        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.tokenize();

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
