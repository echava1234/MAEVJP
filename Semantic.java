// =============================================================================
// FASE 3: Análisis Semántico (Semantic Analysis)
// =============================================================================

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Esta clase hace un simple chequeo semántico:
// - Detecta variables usadas sin declarar
// - Detecta variables declaradas dos veces ("let" repetido)

public class Semantic {
    // Analiza la lista de tokens para verificar declaraciones de variables
    public static void check(List<Token> tokens) {
        Set<String> declared = new HashSet<>();
        boolean inLet = false;
        String pendingVar = null;

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);

            // Detectar declaración de variable
            if (t.getType().equals("LET")) {
                inLet = true;
                continue;
            }

            if (inLet && t.getType().equals("IDENTIFIER")) {
                String var = t.getValue();
                if (declared.contains(var)) {
                    throw new RuntimeException("Error semántico: variable '" + var + "' ya declarada en línea " + t.getLine());
                }
                declared.add(var);
                inLet = false;
                continue;
            }

            // Detectar uso de variable sin declarar (fuera de let)
            if (t.getType().equals("IDENTIFIER") && !declared.contains(t.getValue())) {
                // Busca si es parte de una declaración let
                boolean isLet = false;
                if (i > 0 && tokens.get(i-1).getType().equals("LET")) {
                    isLet = true;
                }
                if (!isLet) {
                    throw new RuntimeException("Error semántico: variable '" + t.getValue() + "' usada sin declarar en línea " + t.getLine());
                }
            }
        }
    }
}
