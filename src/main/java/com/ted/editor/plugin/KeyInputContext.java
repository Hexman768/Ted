package com.ted.editor.plugin;

import com.googlecode.lanterna.TerminalSize;
import com.ted.editor.model.EditorBuffer;
import com.ted.editor.model.TabManager;

/** Read-only view of editor state passed to {@link KeyInputHandler}s. */
public interface KeyInputContext {

    EditorBuffer activeBuffer();

    TabManager tabManager();

    /** Visible editor area size (rows/columns), for page motions etc. */
    TerminalSize visibleEditorSize();

    /** Scroll, repaint, and refresh status after a handled key. */
    void refreshEditor();
}
