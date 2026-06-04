package com.ted.editor;

/**
 * TED - Turbo-style Editor for the terminal.
 * Inspired by Borland Turbo C++ and MS-DOS EDIT.
 */
public final class Ted {
    public static void main(String[] args) {
        try {
            new TedApplication().run(args);
        } catch (Exception e) {
            System.err.println("TED failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
