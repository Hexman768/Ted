package com.ted.editor.plugin;

import com.ted.editor.model.TabManager;
import com.ted.editor.ui.EditorPanel;

public interface PluginContext {
    TabManager tabs();
    EditorPanel editor();
    void setStatus(String text);
    void registerKeyHandler(KeyInputHandler handler);
    void registerCommand(String id, Runnable action);
}
