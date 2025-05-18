// =============================================================================
// FASE 1: Análisis Léxico (Lexer)
// =============================================================================

import java.util.*;
import java.util.regex.*;

public class Lexer {
    public static class Token {
        public String type;
        public String value;
        public int line;

        public Token(String type, String value, int line) {
            this.type = type;
            this.value = value;
            this.line = line;
        }

        @Override
        public String toString() {
            return String.format("Token(%s, '%s', line %d)", type, value, line);
        }
    }

    private static class TokenPattern {
        public final Pattern pattern;
        public final String type;

        public TokenPattern(String regex, String type) {
            this.pattern = Pattern.compile("^" + regex);
            this.type = type;
        }
    }

    private static final List<TokenPattern> tokenPatterns = Arrays.asList(
        new TokenPattern("[ \\t]+",                  null),        // Espacios/tabs, se ignoran
        new TokenPattern("//.*",                    null),        // Comentarios de línea, se ignoran
        new TokenPattern("let\\b",                  "LET"),
        new TokenPattern("print\\b",                "PRINT"),
        new TokenPattern("if\\b",                   "IF"),
        new TokenPattern("else\\b",                 "ELSE"),
        new TokenPattern("while\\b",                "WHILE"),
        new TokenPattern("[a-zA-Z_][a-zA-Z0-9_]*",  "IDENTIFIER"),
        new TokenPattern("\"(\\\\.|[^\"])*\"",      "STRING"),
        new TokenPattern("[0-9]+",                  "NUMBER"),
        new TokenPattern("\\+",                     "PLUS"),
        new TokenPattern("-",                       "MINUS"),
        new TokenPattern("\\*",                     "STAR"),
        new TokenPattern("/",                       "SLASH"),
        new TokenPattern("=",                       "EQUALS"),
        new TokenPattern(">=",                      "GE"),
        new TokenPattern("<=",                      "LE"),
        new TokenPattern(">",                       "GT"),
        new TokenPattern("<",                       "LT"),
        new TokenPattern("\\(",                     "LPAREN"),
        new TokenPattern("\\)",                     "RPAREN"),
        new TokenPattern("\\{",                     "LBRACE"),
        new TokenPattern("\\}",                     "RBRACE"),
        new TokenPattern(";",                       "SEMICOLON"),
        new TokenPattern(",",                       "COMMA")
    );

    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        String[] lines = input.split("\\r?\\n");
        int lineNum = 1;
        for (String line : lines) {
            String s = line;
            while (!s.isEmpty()) {
                boolean matched = false;
                for (TokenPattern tp : tokenPatterns) {
                    Matcher matcher = tp.pattern.matcher(s);
                    if (matcher.find()) {
                        String value = matcher.group();
                        if (tp.type != null) {
                            tokens.add(new Token(tp.type, value, lineNum));
                        }
                        s = s.substring(value.length());
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    throw new IllegalStateException("Token desconocido en la línea " + lineNum + ": " + s);
                }
            }
            lineNum++;
        }
        return tokens;
    }
}

