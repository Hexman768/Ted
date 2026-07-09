package com.ted.editor.plugin;

import javax.swing.*;

public interface KeyInputHandler {
    enum Result { HANDLED, UNHANDLED }
    Result handleKey(KeyStroke key);
}
