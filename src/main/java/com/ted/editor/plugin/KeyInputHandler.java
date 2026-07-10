package com.ted.editor.plugin;

import com.googlecode.lanterna.input.KeyStroke;

/**
 * Intercepts editor keystrokes after global shortcuts (F-keys, Ctrl+S, etc.)
 * but before TED's default insert-mode handling.
 */
public interface KeyInputHandler {

    enum Result {
        HANDLED,
        UNHANDLED
    }

    Result handleKey(KeyStroke key, KeyInputContext ctx);
}
