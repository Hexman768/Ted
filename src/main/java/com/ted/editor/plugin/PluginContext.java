package com.ted.editor.plugin;

import com.ted.editor.model.TabManager;
import com.ted.editor.ui.EditorPanel;

/** Services available to plugins during {@link TedPlugin#init(PluginContext)}. */
public interface PluginContext {

    TabManager tabManager();

    EditorPanel editorPanel();

    void registerKeyHandler(KeyInputHandler handler);

    void setStatusText(String text);

    /** Appended to the status bar after cursor info (e.g. {@code VIM -- NORMAL --}). */
    void setStatusExtra(String extra);
}
