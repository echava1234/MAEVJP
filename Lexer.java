class Lexer {
    private String sourceCode;
    private List<Token> tokens;
    private int currentLine;
    private int currentColumn;
    private static final Map<String, String> TOKEN_TYPES = new HashMap<>();

    static {
        TOKEN_TYPES.put("NUMBER", "\\d+(\\.\\d+)?");
        TOKEN_TYPES.put("PLUS", "\\+");
        TOKEN_TYPES.put("MINUS", "-");
        TOKEN_TYPES.put("MULTIPLY", "\\*");
        TOKEN_TYPES.put("DIVIDE", "/");
        TOKEN_TYPES.put("LPAREN", "\\(");
        TOKEN_TYPES.put("RPAREN", "\\)");
        TOKEN_TYPES.put("ASSIGN", "=");
        TOKEN_TYPES.put("SEMICOLON", ";");
        TOKEN_TYPES.put("IDENTIFIER", "[a-zA-Z_][a-zA-Z0-9_]*");
        TOKEN_TYPES.put("PRINT", "print");
        TOKEN_TYPES.put("IF", "if");
        TOKEN_TYPES.put("ELSE", "else");
        TOKEN_TYPES.put("WHILE", "while");
        TOKEN_TYPES.put("EQUALS", "==");
        TOKEN_TYPES.put("LBRACE", "\\{");
        TOKEN_TYPES.put("RBRACE", "\\}");
        TOKEN_TYPES.put("GREATER", ">");
        TOKEN_TYPES.put("LESS", "<");
        TOKEN_TYPES.put("WHITESPACE", "\\s+");
        TOKEN_TYPES.put("COMMENT", "//.*");
        TOKEN_TYPES.put("STRING", "\"[^\"]*\"");
    }

    public Lexer(String sourceCode) {
        this.sourceCode = sourceCode;
        this.tokens = new ArrayList<>();
        this.currentLine = 1;
        this.currentColumn = 1;
    }

    public List<Token> tokenize() {
        String remainingCode = sourceCode;

        while (!remainingCode.isEmpty()) {
            Matcher match = null;
            String matchedTokenType = null;

            for (Map.Entry<String, String> entry : TOKEN_TYPES.entrySet()) {
                String tokenType = entry.getKey();
                String pattern = entry.getValue();
                Pattern regex = Pattern.compile("^" + pattern);
                match = regex.matcher(remainingCode);

                if (match.find()) {
                    matchedTokenType = tokenType;
                    break;
                }
            }

            if (match != null) {
                String value = match.group(0);

                if (!matchedTokenType.equals("WHITESPACE") && !matchedTokenType.equals("COMMENT")) {
                    if (matchedTokenType.equals("IDENTIFIER")) {
                        if (value.equals("print")) {
                            matchedTokenType = "PRINT";
                        } else if (value.equals("if")) {
                            matchedTokenType = "IF";
                        } else if (value.equals("else")) {
                            matchedTokenType = "ELSE";
                        } else if (value.equals("while")) {
                            matchedTokenType = "WHILE";
                        }
                    }
                    Token token = new Token(matchedTokenType, value, currentLine, currentColumn);
                    tokens.add(token);
                }

                for (char c : value.toCharArray()) {
                    if (c == '\n') {
                        currentLine++;
                        currentColumn = 1;
                    } else {
                        currentColumn++;
                    }
                }

                remainingCode = remainingCode.substring(value.length());
            } else {
                char errorChar = remainingCode.charAt(0);
                throw new IllegalArgumentException("Carácter no reconocido: '" + errorChar + "' en la línea " + currentLine + ", columna " + currentColumn);
            }
        }
        return tokens;
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
