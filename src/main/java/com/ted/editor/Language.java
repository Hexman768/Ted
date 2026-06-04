package com.ted.editor;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

public enum Language {
    PLAIN("Plain Text", ""),
    C("C", ".c,.h"),
    CPP("C++", ".cpp,.cc,.cxx,.hpp,.hh,.hxx"),
    JAVA("Java", ".java"),
    JAVASCRIPT("JavaScript", ".js,.mjs,.cjs"),
    TYPESCRIPT("TypeScript", ".ts,.tsx"),
    PYTHON("Python", ".py,.pyw"),
    RUST("Rust", ".rs"),
    GO("Go", ".go"),
    HTML("HTML", ".html,.htm"),
    CSS("CSS", ".css,.scss"),
    JSON("JSON", ".json"),
    XML("XML", ".xml,.svg"),
    SHELL("Shell", ".sh,.bash,.zsh"),
    MARKDOWN("Markdown", ".md,.markdown"),
    SQL("SQL", ".sql"),
    YAML("YAML", ".yml,.yaml"),
    TOML("TOML", ".toml"),
    PROPERTIES("Properties", ".properties,.ini,.cfg"),
    MAKEFILE("Makefile", "makefile");

    private final String displayName;
    private final String extensions;

    Language(String displayName, String extensions) {
        this.displayName = displayName;
        this.extensions = extensions;
    }

    public String displayName() {
        return displayName;
    }

    public static Language fromPath(Path path) {
        if (path == null) {
            return PLAIN;
        }
        String fileName = path.getFileName() != null
                ? path.getFileName().toString().toLowerCase(Locale.ROOT)
                : "";
        if (fileName.equals("makefile") || fileName.startsWith("makefile.")) {
            return MAKEFILE;
        }
        int dot = fileName.lastIndexOf('.');
        String ext = dot >= 0 ? fileName.substring(dot) : "";
        for (Language lang : values()) {
            if (lang == PLAIN || lang.extensions.isEmpty()) {
                continue;
            }
            for (String candidate : lang.extensions.split(",")) {
                if (candidate.startsWith(".") && ext.equals(candidate)) {
                    return lang;
                }
            }
        }
        return PLAIN;
    }

    public static Optional<Language> fromName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        for (Language lang : values()) {
            if (lang.displayName.equalsIgnoreCase(name) || lang.name().equalsIgnoreCase(name)) {
                return Optional.of(lang);
            }
        }
        return Optional.empty();
    }
}
