package com.ted.vim;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.ted.editor.model.EditorBuffer;
import com.ted.editor.plugin.KeyInputContext;
import com.ted.editor.plugin.KeyInputHandler;
import com.ted.editor.plugin.PluginContext;
import com.ted.editor.ui.EditorPanel;

final class VimInputController implements KeyInputHandler {

    private enum Mode {
        NORMAL,
        INSERT
    }

    private final EditorPanel editorPanel;
    private final PluginContext pluginContext;
    private Mode mode = Mode.INSERT;

    VimInputController(PluginContext pluginContext) {
        this.pluginContext = pluginContext;
        this.editorPanel = pluginContext.editorPanel();
    }

    @Override
    public Result handleKey(KeyStroke key, KeyInputContext ctx) {
        if (key.getKeyType() == KeyType.Escape) {
            mode = Mode.NORMAL;
            pluginContext.setStatusExtra("VIM -- NORMAL --");
            return Result.HANDLED;
        }

        if (mode == Mode.INSERT) {
            return editorPanel.handleDefaultKey(key) ? Result.HANDLED : Result.UNHANDLED;
        }

        if (key.getKeyType() == KeyType.Character && !key.isCtrlDown() && !key.isAltDown()) {
            EditorBuffer buf = ctx.activeBuffer();
            switch (key.getCharacter()) {
                case 'h' -> buf.moveCursor(0, -1);
                case 'j' -> buf.moveCursor(1, 0);
                case 'k' -> buf.moveCursor(-1, 0);
                case 'l' -> buf.moveCursor(0, 1);
                case 'w' -> buf.moveWordForward(false);
                case 'b' -> buf.moveWordBackward(false);
                case '0' -> buf.moveToLineStart();
                case '$' -> buf.moveToLineEnd();
                case 'g' -> buf.moveToDocumentStart();
                case 'G' -> buf.moveToDocumentEnd();
                case 'i', 'a' -> {
                    mode = Mode.INSERT;
                    pluginContext.setStatusExtra("VIM -- INSERT --");
                    return Result.HANDLED;
                }
                default -> {
                    return Result.UNHANDLED;
                }
            }
            return Result.HANDLED;
        }

        return Result.UNHANDLED;
    }
}
