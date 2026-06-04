package com.ted.editor.syntax;

import com.ted.editor.Language;
import com.ted.editor.TurboTheme;
import com.googlecode.lanterna.TextColor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SyntaxHighlighter {
    private static final Map<Language, LanguageRules> RULES = new EnumMap<>(Language.class);

    static {
        RULES.put(Language.C, cFamilyRules(false));
        RULES.put(Language.CPP, cFamilyRules(true));
        RULES.put(Language.JAVA, javaRules());
        RULES.put(Language.JAVASCRIPT, jsRules(false));
        RULES.put(Language.TYPESCRIPT, jsRules(true));
        RULES.put(Language.PYTHON, pythonRules());
        RULES.put(Language.RUST, rustRules());
        RULES.put(Language.GO, goRules());
        RULES.put(Language.HTML, htmlRules());
        RULES.put(Language.CSS, cssRules());
        RULES.put(Language.JSON, jsonRules());
        RULES.put(Language.XML, xmlRules());
        RULES.put(Language.SHELL, shellRules());
        RULES.put(Language.MARKDOWN, markdownRules());
        RULES.put(Language.SQL, sqlRules());
        RULES.put(Language.YAML, yamlRules());
        RULES.put(Language.TOML, tomlRules());
        RULES.put(Language.PROPERTIES, propertiesRules());
        RULES.put(Language.MAKEFILE, makefileRules());
        RULES.put(Language.PLAIN, LanguageRules.plain());
    }

    private SyntaxHighlighter() {}

    public static List<Token> highlight(Language language, String line, boolean inBlockComment) {
        LanguageRules rules = RULES.getOrDefault(language, LanguageRules.plain());
        return rules.tokenize(line, inBlockComment);
    }

    public static TextColor colorFor(TokenType type) {
        return switch (type) {
            case KEYWORD -> TurboTheme.SYNTAX_KEYWORD;
            case TYPE -> TurboTheme.SYNTAX_TYPE;
            case STRING, CHAR -> TurboTheme.SYNTAX_STRING;
            case COMMENT -> TurboTheme.SYNTAX_COMMENT;
            case NUMBER -> TurboTheme.SYNTAX_NUMBER;
            case PREPROCESSOR -> TurboTheme.SYNTAX_PREPROCESSOR;
            case OPERATOR -> TurboTheme.SYNTAX_OPERATOR;
            case TAG -> TurboTheme.SYNTAX_TAG;
            case ATTRIBUTE -> TurboTheme.SYNTAX_ATTRIBUTE;
            case BUILTIN -> TurboTheme.SYNTAX_BUILTIN;
            default -> TurboTheme.SYNTAX_DEFAULT;
        };
    }

    private static LanguageRules cFamilyRules(boolean cpp) {
        Set<String> keywords = Set.of(
                "if", "else", "for", "while", "do", "switch", "case", "default", "break", "continue",
                "return", "goto", "sizeof", "typedef", "struct", "union", "enum", "const", "static",
                "extern", "volatile", "register", "inline", "void", "int", "char", "float", "double",
                "long", "short", "unsigned", "signed", "auto", "true", "false", "nullptr"
        );
        if (cpp) {
            keywords = union(keywords, "class", "namespace", "template", "typename", "new", "delete",
                    "public", "private", "protected", "virtual", "override", "this", "try", "catch",
                    "throw", "using", "constexpr", "noexcept", "decltype", "operator");
        }
        return LanguageRules.builder()
                .keywords(keywords)
                .types(Set.of("bool", "size_t", "uint8_t", "uint16_t", "uint32_t", "uint64_t",
                        "int8_t", "int16_t", "int32_t", "int64_t", "string", "vector", "map"))
                .lineComment("//")
                .blockComment("/*", "*/")
                .preprocessor("#")
                .build();
    }

    private static LanguageRules javaRules() {
        return LanguageRules.builder()
                .keywords(Set.of(
                        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
                        "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
                        "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
                        "interface", "long", "native", "new", "package", "private", "protected", "public",
                        "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
                        "throw", "throws", "transient", "try", "void", "volatile", "while", "var", "record",
                        "sealed", "permits", "true", "false", "null"
                ))
                .types(Set.of("String", "Integer", "Long", "Double", "Boolean", "Object", "List", "Map", "Set"))
                .lineComment("//")
                .blockComment("/*", "*/")
                .build();
    }

    private static LanguageRules jsRules(boolean typescript) {
        Set<String> kw = Set.of(
                "break", "case", "catch", "class", "const", "continue", "debugger", "default", "delete",
                "do", "else", "export", "extends", "finally", "for", "function", "if", "import", "in",
                "instanceof", "let", "new", "return", "super", "switch", "this", "throw", "try", "typeof",
                "var", "void", "while", "with", "yield", "async", "await", "true", "false", "null", "undefined"
        );
        if (typescript) {
            kw = union(kw, "interface", "type", "enum", "implements", "namespace", "readonly", "declare",
                    "as", "is", "keyof", "infer", "satisfies");
        }
        return LanguageRules.builder()
                .keywords(kw)
                .types(Set.of("string", "number", "boolean", "any", "never", "unknown", "void"))
                .lineComment("//")
                .blockComment("/*", "*/")
                .build();
    }

    private static LanguageRules pythonRules() {
        return LanguageRules.builder()
                .keywords(Set.of(
                        "and", "as", "assert", "async", "await", "break", "class", "continue", "def", "del",
                        "elif", "else", "except", "False", "finally", "for", "from", "global", "if", "import",
                        "in", "is", "lambda", "None", "nonlocal", "not", "or", "pass", "raise", "return",
                        "True", "try", "while", "with", "yield"
                ))
                .types(Set.of("int", "float", "str", "bool", "list", "dict", "tuple", "set"))
                .lineComment("#")
                .tripleQuote(true)
                .build();
    }

    private static LanguageRules rustRules() {
        return LanguageRules.builder()
                .keywords(Set.of(
                        "as", "async", "await", "break", "const", "continue", "crate", "dyn", "else", "enum",
                        "extern", "false", "fn", "for", "if", "impl", "in", "let", "loop", "match", "mod",
                        "move", "mut", "pub", "ref", "return", "self", "Self", "static", "struct", "super",
                        "trait", "true", "type", "unsafe", "use", "where", "while"
                ))
                .types(Set.of("i8", "i16", "i32", "i64", "u8", "u16", "u32", "u64", "f32", "f64", "bool", "String", "Vec"))
                .lineComment("//")
                .blockComment("/*", "*/")
                .build();
    }

    private static LanguageRules goRules() {
        return LanguageRules.builder()
                .keywords(Set.of(
                        "break", "case", "chan", "const", "continue", "default", "defer", "else", "fallthrough",
                        "for", "func", "go", "goto", "if", "import", "interface", "map", "package", "range",
                        "return", "select", "struct", "switch", "type", "var", "true", "false", "nil", "iota"
                ))
                .types(Set.of("int", "int8", "int16", "int32", "int64", "uint", "string", "bool", "error", "byte", "rune"))
                .lineComment("//")
                .blockComment("/*", "*/")
                .build();
    }

    private static LanguageRules htmlRules() {
        return LanguageRules.builder().markup(true).build();
    }

    private static LanguageRules cssRules() {
        return LanguageRules.builder()
                .keywords(Set.of("important", "and", "or", "not", "from", "to"))
                .types(Set.of("color", "url", "rgb", "rgba", "hsl", "hsla"))
                .blockComment("/*", "*/")
                .build();
    }

    private static LanguageRules jsonRules() {
        return LanguageRules.builder().json(true).build();
    }

    private static LanguageRules xmlRules() {
        return LanguageRules.builder().markup(true).build();
    }

    private static LanguageRules shellRules() {
        return LanguageRules.builder()
                .keywords(Set.of("if", "then", "else", "elif", "fi", "for", "do", "done", "while", "case",
                        "esac", "function", "return", "export", "local", "source", "echo", "exit", "in"))
                .builtins(Set.of("cd", "ls", "pwd", "cat", "grep", "sed", "awk", "chmod", "chown", "mkdir", "rm"))
                .lineComment("#")
                .build();
    }

    private static LanguageRules markdownRules() {
        return LanguageRules.builder().markdown(true).build();
    }

    private static LanguageRules sqlRules() {
        return LanguageRules.builder()
                .keywords(Set.of(
                        "SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE", "CREATE", "DROP", "ALTER",
                        "TABLE", "INDEX", "JOIN", "LEFT", "RIGHT", "INNER", "OUTER", "ON", "AND", "OR", "NOT",
                        "NULL", "AS", "ORDER", "BY", "GROUP", "HAVING", "LIMIT", "VALUES", "INTO", "SET", "PRIMARY",
                        "KEY", "FOREIGN", "REFERENCES", "UNIQUE", "DEFAULT", "TRUE", "FALSE", "CASE", "WHEN", "THEN",
                        "ELSE", "END", "DISTINCT", "UNION", "ALL", "EXISTS", "BETWEEN", "LIKE", "IN", "IS"
                ))
                .lineComment("--")
                .blockComment("/*", "*/")
                .caseInsensitiveKeywords(true)
                .build();
    }

    private static LanguageRules yamlRules() {
        return LanguageRules.builder().yaml(true).lineComment("#").build();
    }

    private static LanguageRules tomlRules() {
        return LanguageRules.builder()
                .keywords(Set.of("true", "false"))
                .lineComment("#")
                .build();
    }

    private static LanguageRules propertiesRules() {
        return LanguageRules.builder().properties(true).lineComment("#").build();
    }

    private static LanguageRules makefileRules() {
        return LanguageRules.builder()
                .keywords(Set.of("ifeq", "ifneq", "else", "endif", "include", "define", "endef", "export", "vpath"))
                .lineComment("#")
                .build();
    }

    @SafeVarargs
    private static Set<String> union(Set<String> base, String... extra) {
        var set = new java.util.HashSet<>(base);
        for (String e : extra) set.add(e);
        return Set.copyOf(set);
    }

    private static final class LanguageRules {
        private final Set<String> keywords;
        private final Set<String> types;
        private final Set<String> builtins;
        private final String lineComment;
        private final String blockStart;
        private final String blockEnd;
        private final boolean preprocessor;
        private final boolean markup;
        private final boolean json;
        private final boolean markdown;
        private final boolean yaml;
        private final boolean properties;
        private final boolean tripleQuote;
        private final boolean caseInsensitiveKeywords;

        private LanguageRules(Builder b) {
            this.keywords = b.keywords;
            this.types = b.types;
            this.builtins = b.builtins;
            this.lineComment = b.lineComment;
            this.blockStart = b.blockStart;
            this.blockEnd = b.blockEnd;
            this.preprocessor = b.preprocessor;
            this.markup = b.markup;
            this.json = b.json;
            this.markdown = b.markdown;
            this.yaml = b.yaml;
            this.properties = b.properties;
            this.tripleQuote = b.tripleQuote;
            this.caseInsensitiveKeywords = b.caseInsensitiveKeywords;
        }

        static LanguageRules plain() {
            return builder().build();
        }

        static Builder builder() {
            return new Builder();
        }

        List<Token> tokenize(String line, boolean inBlockComment) {
            List<Token> tokens = new ArrayList<>();
            int len = line.length();
            if (len == 0) {
                return tokens;
            }

            if (inBlockComment && blockEnd != null) {
                int end = line.indexOf(blockEnd);
                if (end >= 0) {
                    tokens.add(new Token(TokenType.COMMENT, 0, end + blockEnd.length()));
                    tokenizeRest(line, end + blockEnd.length(), tokens);
                } else {
                    tokens.add(new Token(TokenType.COMMENT, 0, len));
                }
                return tokens;
            }

            if (markup) return tokenizeMarkup(line);
            if (json) return tokenizeJson(line);
            if (markdown) return tokenizeMarkdown(line);
            if (yaml) return tokenizeYaml(line);
            if (properties) return tokenizeProperties(line);

            int i = 0;
            if (preprocessor && line.startsWith("#")) {
                tokens.add(new Token(TokenType.PREPROCESSOR, 0, len));
                return tokens;
            }

            while (i < len) {
                if (blockStart != null && line.startsWith(blockStart, i)) {
                    int end = line.indexOf(blockEnd, i + blockStart.length());
                    int endPos = end >= 0 ? end + blockEnd.length() : len;
                    tokens.add(new Token(TokenType.COMMENT, i, endPos));
                    i = endPos;
                    continue;
                }
                if (lineComment != null && line.startsWith(lineComment, i)) {
                    tokens.add(new Token(TokenType.COMMENT, i, len));
                    break;
                }
                char c = line.charAt(i);
                if (c == '"' || c == '\'') {
                    char quote = c;
                    int j = i + 1;
                    while (j < len) {
                        if (line.charAt(j) == '\\' && j + 1 < len) {
                            j += 2;
                            continue;
                        }
                        if (line.charAt(j) == quote) {
                            j++;
                            break;
                        }
                        j++;
                    }
                    tokens.add(new Token(c == '"' ? TokenType.STRING : TokenType.CHAR, i, j));
                    i = j;
                    continue;
                }
                if (tripleQuote && i + 2 < len && line.startsWith("\"\"\"", i)) {
                    int j = line.indexOf("\"\"\"", i + 3);
                    int end = j >= 0 ? j + 3 : len;
                    tokens.add(new Token(TokenType.STRING, i, end));
                    i = end;
                    continue;
                }
                if (Character.isDigit(c) || (c == '.' && i + 1 < len && Character.isDigit(line.charAt(i + 1)))) {
                    int j = i + 1;
                    while (j < len && (Character.isDigit(line.charAt(j)) || line.charAt(j) == '.' ||
                            line.charAt(j) == 'x' || line.charAt(j) == 'X' ||
                            line.charAt(j) == 'e' || line.charAt(j) == 'E' ||
                            line.charAt(j) == '_' || line.charAt(j) == 'f' || line.charAt(j) == 'L')) {
                        j++;
                    }
                    tokens.add(new Token(TokenType.NUMBER, i, j));
                    i = j;
                    continue;
                }
                if (Character.isLetter(c) || c == '_') {
                    int j = i + 1;
                    while (j < len && (Character.isLetterOrDigit(line.charAt(j)) || line.charAt(j) == '_')) {
                        j++;
                    }
                    String word = line.substring(i, j);
                    tokens.add(new Token(classifyWord(word), i, j));
                    i = j;
                    continue;
                }
                if ("+-*/%=<>!&|^~?:.,;[]{}()".indexOf(c) >= 0) {
                    tokens.add(new Token(TokenType.OPERATOR, i, i + 1));
                    i++;
                    continue;
                }
                i++;
            }
            return mergeDefaults(line, tokens);
        }

        private void tokenizeRest(String line, int start, List<Token> tokens) {
            if (start < line.length()) {
                for (Token t : tokenize(line.substring(start), false)) {
                    tokens.add(new Token(t.type(), t.start() + start, t.end() + start));
                }
            }
        }

        private TokenType classifyWord(String word) {
            String check = caseInsensitiveKeywords ? word.toUpperCase() : word;
            if (keywords.contains(caseInsensitiveKeywords ? check : word)) return TokenType.KEYWORD;
            if (types.contains(word)) return TokenType.TYPE;
            if (builtins.contains(word)) return TokenType.BUILTIN;
            return TokenType.DEFAULT;
        }

        private List<Token> tokenizeMarkup(String line) {
            List<Token> tokens = new ArrayList<>();
            int i = 0;
            while (i < line.length()) {
                int tagStart = line.indexOf('<', i);
                if (tagStart < 0) {
                    addStringTokens(line, i, line.length(), tokens);
                    break;
                }
                if (tagStart > i) addStringTokens(line, i, tagStart, tokens);
                int tagEnd = line.indexOf('>', tagStart);
                if (tagEnd < 0) tagEnd = line.length() - 1;
                else tagEnd++;
                tokens.add(new Token(TokenType.TAG, tagStart, tagEnd));
                i = tagEnd;
            }
            return mergeDefaults(line, tokens);
        }

        private List<Token> tokenizeJson(String line) {
            List<Token> tokens = new ArrayList<>();
            Pattern p = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"|true|false|null|-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?");
            Matcher m = p.matcher(line);
            int last = 0;
            while (m.find()) {
                if (m.start() > last) tokens.add(new Token(TokenType.OPERATOR, last, m.start()));
                String match = m.group();
                if (match.startsWith("\"")) tokens.add(new Token(TokenType.STRING, m.start(), m.end()));
                else if (match.equals("true") || match.equals("false") || match.equals("null"))
                    tokens.add(new Token(TokenType.KEYWORD, m.start(), m.end()));
                else tokens.add(new Token(TokenType.NUMBER, m.start(), m.end()));
                last = m.end();
            }
            return mergeDefaults(line, tokens);
        }

        private List<Token> tokenizeMarkdown(String line) {
            List<Token> tokens = new ArrayList<>();
            if (line.startsWith("#")) {
                tokens.add(new Token(TokenType.KEYWORD, 0, line.length()));
                return tokens;
            }
            if (line.trim().startsWith("```")) {
                tokens.add(new Token(TokenType.PREPROCESSOR, 0, line.length()));
                return tokens;
            }
            Pattern bold = Pattern.compile("\\*\\*[^*]+\\*\\*|__[^_]+__|`[^`]+`");
            Matcher m = bold.matcher(line);
            int last = 0;
            while (m.find()) {
                if (m.start() > last) tokens.add(new Token(TokenType.DEFAULT, last, m.start()));
                tokens.add(new Token(TokenType.STRING, m.start(), m.end()));
                last = m.end();
            }
            return mergeDefaults(line, tokens);
        }

        private List<Token> tokenizeYaml(String line) {
            List<Token> tokens = new ArrayList<>();
            if (lineComment != null) {
                int hash = line.indexOf('#');
                if (hash >= 0) {
                    tokenizeKeyValue(line.substring(0, hash), tokens);
                    tokens.add(new Token(TokenType.COMMENT, hash, line.length()));
                    return mergeDefaults(line, tokens);
                }
            }
            tokenizeKeyValue(line, tokens);
            return mergeDefaults(line, tokens);
        }

        private void tokenizeKeyValue(String line, List<Token> tokens) {
            int colon = line.indexOf(':');
            if (colon > 0) {
                tokens.add(new Token(TokenType.ATTRIBUTE, 0, colon));
                addStringTokens(line, colon, line.length(), tokens);
            } else {
                addStringTokens(line, 0, line.length(), tokens);
            }
        }

        private List<Token> tokenizeProperties(String line) {
            List<Token> tokens = new ArrayList<>();
            int comment = lineComment != null ? line.indexOf(lineComment) : -1;
            String content = comment >= 0 ? line.substring(0, comment) : line;
            int eq = content.indexOf('=');
            if (eq > 0) {
                tokens.add(new Token(TokenType.ATTRIBUTE, 0, eq));
                addStringTokens(content, eq, content.length(), tokens);
            } else if (!content.isBlank()) {
                tokens.add(new Token(TokenType.ATTRIBUTE, 0, content.length()));
            }
            if (comment >= 0) tokens.add(new Token(TokenType.COMMENT, comment, line.length()));
            return mergeDefaults(line, tokens);
        }

        private void addStringTokens(String line, int start, int end, List<Token> tokens) {
            int i = start;
            while (i < end) {
                char c = line.charAt(i);
                if (c == '"' || c == '\'') {
                    char q = c;
                    int j = i + 1;
                    while (j < end && line.charAt(j) != q) j++;
                    if (j < end) j++;
                    tokens.add(new Token(TokenType.STRING, i, j));
                    i = j;
                } else {
                    i++;
                }
            }
        }

        private List<Token> mergeDefaults(String line, List<Token> tokens) {
            if (tokens.isEmpty()) return tokens;
            tokens.sort((a, b) -> Integer.compare(a.start(), b.start()));
            List<Token> merged = new ArrayList<>();
            int pos = 0;
            for (Token t : tokens) {
                if (t.start() > pos) merged.add(new Token(TokenType.DEFAULT, pos, t.start()));
                merged.add(t);
                pos = Math.max(pos, t.end());
            }
            if (pos < line.length()) merged.add(new Token(TokenType.DEFAULT, pos, line.length()));
            return merged;
        }

        static final class Builder {
            private Set<String> keywords = Set.of();
            private Set<String> types = Set.of();
            private Set<String> builtins = Set.of();
            private String lineComment;
            private String blockStart;
            private String blockEnd;
            private boolean preprocessor;
            private boolean markup;
            private boolean json;
            private boolean markdown;
            private boolean yaml;
            private boolean properties;
            private boolean tripleQuote;
            private boolean caseInsensitiveKeywords;

            Builder keywords(Set<String> k) { keywords = k; return this; }
            Builder types(Set<String> t) { types = t; return this; }
            Builder builtins(Set<String> b) { builtins = b; return this; }
            Builder lineComment(String c) { lineComment = c; return this; }
            Builder blockComment(String start, String end) { blockStart = start; blockEnd = end; return this; }
            Builder preprocessor(String p) { preprocessor = p != null; return this; }
            Builder markup(boolean m) { markup = m; return this; }
            Builder json(boolean j) { json = j; return this; }
            Builder markdown(boolean m) { markdown = m; return this; }
            Builder yaml(boolean y) { yaml = y; return this; }
            Builder properties(boolean p) { properties = p; return this; }
            Builder tripleQuote(boolean t) { tripleQuote = t; return this; }
            Builder caseInsensitiveKeywords(boolean c) { caseInsensitiveKeywords = c; return this; }
            LanguageRules build() { return new LanguageRules(this); }
        }
    }
}
