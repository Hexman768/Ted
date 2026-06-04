package com.ted.editor.syntax;

public record Token(TokenType type, int start, int end) {
    public int length() {
        return end - start;
    }
}
