package com.ted.editor.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.AbstractInteractableComponent;
import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.InteractableRenderer;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.ted.editor.TurboTheme;
import com.ted.editor.model.EditorBuffer;
import com.ted.editor.model.TabManager;
import com.ted.editor.plugin.KeyInputContext;
import com.ted.editor.plugin.KeyInputHandler;
import com.ted.editor.syntax.SyntaxHighlighter;
import com.ted.editor.syntax.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EditorPanel extends AbstractInteractableComponent<EditorPanel> {
    private final TabManager tabManager;
    private int scrollRow;
    private Consumer<String> statusUpdater;
    private Runnable repaintRequest;
    private Consumer<KeyStroke> globalKeyHandler;
    private final List<KeyInputHandler> keyInputHandlers = new ArrayList<>();
    private String statusExtra = "";

    public EditorPanel(TabManager tabManager) {
        this.tabManager = tabManager;
    }

    public void setStatusExtra(String extra) {
        this.statusExtra = extra != null ? extra : "";
    }

    public String statusExtra() {
        return statusExtra;
    }

    public void addKeyInputHandler(KeyInputHandler handler) {
        keyInputHandlers.add(handler);
    }

    public void setStatusUpdater(Consumer<String> statusUpdater) {
        this.statusUpdater = statusUpdater;
    }

    public void setRepaintRequest(Runnable repaintRequest) {
        this.repaintRequest = repaintRequest;
    }

    public void setGlobalKeyHandler(Consumer<KeyStroke> globalKeyHandler) {
        this.globalKeyHandler = globalKeyHandler;
    }

    public void refreshScroll() {
        EditorBuffer buf = tabManager.activeBuffer();
        buf.updateBlockCommentState();
        int cursorLine = buf.cursorLine();
        TerminalSize size = getSize();
        int visible = Math.max(1, size.getRows());
        if (cursorLine < scrollRow) {
            scrollRow = cursorLine;
        } else if (cursorLine >= scrollRow + visible) {
            scrollRow = cursorLine - visible + 1;
        }
        updateStatus();
    }

    private void updateStatus() {
        if (statusUpdater != null) {
            EditorBuffer buf = tabManager.activeBuffer();
            String modified = buf.isModified() ? "MODIFIED" : "      ";
            String extra = statusExtra.isEmpty() ? "" : "  |  " + statusExtra;
            statusUpdater.accept(String.format(" Ln %d, Col %d  |  %s  |  %s%s ",
                    buf.cursorLine() + 1,
                    buf.cursorColumn() + 1,
                    buf.getLanguage().displayName(),
                    modified,
                    extra));
        }
    }

    @Override
    protected InteractableRenderer<EditorPanel> createDefaultRenderer() {
        return new InteractableRenderer<>() {
            @Override
            public TerminalSize getPreferredSize(EditorPanel component) {
                return component.getSize() != null ? component.getSize() : new TerminalSize(80, 20);
            }

            @Override
            public void drawComponent(TextGUIGraphics g, EditorPanel component) {
                component.renderEditor(g);
            }

            @Override
            public TerminalPosition getCursorLocation(EditorPanel component) {
                EditorBuffer buf = component.tabManager.activeBuffer();
                int row = buf.cursorLine() - component.scrollRow;
                if (row < 0 || row >= component.getSize().getRows()) {
                    return null;
                }
                return new TerminalPosition(
                        TurboTheme.GUTTER_WIDTH + buf.cursorColumn(),
                        row);
            }
        };
    }

    private void renderEditor(TextGUIGraphics g) {
        TerminalSize size = getSize();
        TurboTheme.fillBackground(g, size);
        EditorBuffer buf = tabManager.activeBuffer();
        buf.updateBlockCommentState();

        int rows = size.getRows();
        int cols = size.getColumns();
        int gutter = TurboTheme.GUTTER_WIDTH;
        int textWidth = Math.max(1, cols - gutter);

        for (int row = 0; row < rows; row++) {
            int lineIndex = scrollRow + row;
            drawGutter(g, row, lineIndex + 1, gutter);
            if (lineIndex < buf.lineCount()) {
                drawLine(g, row, gutter, textWidth, buf, lineIndex);
            }
        }
        drawCursor(g, buf, gutter, textWidth);
    }

    private void drawGutter(TextGUIGraphics g, int row, int lineNum, int width) {
        g.setBackgroundColor(TurboTheme.GUTTER_BG);
        g.setForegroundColor(TurboTheme.GUTTER_FG);
        String num = String.format("%4d ", lineNum);
        for (int c = 0; c < width && c < num.length(); c++) {
            g.setCharacter(c, row, num.charAt(c));
        }
        for (int c = num.length(); c < width; c++) {
            g.setCharacter(c, row, ' ');
        }
    }

    private void drawLine(TextGUIGraphics g, int row, int xOffset, int width, EditorBuffer buf, int lineIndex) {
        String line = buf.getLine(lineIndex);
        List<Token> tokens = SyntaxHighlighter.highlight(buf.getLanguage(), line, buf.isInBlockComment());
        int col = 0;
        for (Token token : tokens) {
            g.setForegroundColor(SyntaxHighlighter.colorFor(token.type()));
            g.setBackgroundColor(TurboTheme.BG);
            for (int i = token.start(); i < token.end() && col < width; i++) {
                g.setCharacter(xOffset + col, row, line.charAt(i));
                col++;
            }
        }
        g.setBackgroundColor(TurboTheme.BG);
        while (col < width) {
            g.setCharacter(xOffset + col, row, ' ');
            col++;
        }
    }

    private void drawCursor(TextGUIGraphics g, EditorBuffer buf, int gutter, int textWidth) {
        int cursorScreenRow = buf.cursorLine() - scrollRow;
        int cursorCol = buf.cursorColumn();
        if (cursorScreenRow < 0 || cursorScreenRow >= getSize().getRows() || cursorCol >= textWidth) {
            return;
        }
        char ch = ' ';
        if (buf.cursorLine() < buf.lineCount()) {
            String line = buf.getLine(buf.cursorLine());
            if (cursorCol < line.length()) {
                ch = line.charAt(cursorCol);
            }
        }
        g.setForegroundColor(TurboTheme.CURSOR_FG);
        g.setBackgroundColor(TurboTheme.CURSOR_BG);
        g.setCharacter(gutter + cursorCol, cursorScreenRow, ch == ' ' ? '\u2588' : ch);
    }

    @Override
    public Interactable.Result handleKeyStroke(KeyStroke key) {
        if (globalKeyHandler != null && isGlobalKey(key)) {
            globalKeyHandler.accept(key);
            return Interactable.Result.HANDLED;
        }

        KeyInputContext ctx = createKeyInputContext();
        for (KeyInputHandler handler : keyInputHandlers) {
            if (handler.handleKey(key, ctx) == KeyInputHandler.Result.HANDLED) {
                afterKeyHandled();
                return Interactable.Result.HANDLED;
            }
        }

        if (handleDefaultKey(key)) {
            afterKeyHandled();
            return Interactable.Result.HANDLED;
        }
        return Interactable.Result.UNHANDLED;
    }

    private KeyInputContext createKeyInputContext() {
        return new KeyInputContext() {
            @Override
            public EditorBuffer activeBuffer() {
                return tabManager.activeBuffer();
            }

            @Override
            public TabManager tabManager() {
                return tabManager;
            }

            @Override
            public TerminalSize visibleEditorSize() {
                TerminalSize size = getSize();
                return size != null ? size : new TerminalSize(80, 20);
            }

            @Override
            public void refreshEditor() {
                afterKeyHandled();
            }
        };
    }

    private void afterKeyHandled() {
        refreshScroll();
        invalidate();
        if (repaintRequest != null) {
            repaintRequest.run();
        }
    }

    /** TED's built-in insert-mode key bindings; plugins can delegate here in insert mode. */
    public boolean handleDefaultKey(KeyStroke key) {
        EditorBuffer buf = tabManager.activeBuffer();

        switch (key.getKeyType()) {
            case Escape -> {
                return false;
            }
            case ArrowUp -> buf.moveCursor(-1, 0);
            case ArrowDown -> buf.moveCursor(1, 0);
            case ArrowLeft -> buf.moveCursor(0, -1);
            case ArrowRight -> buf.moveCursor(0, 1);
            case Home -> {
                if (key.isCtrlDown()) buf.moveToDocumentStart();
                else buf.moveToLineStart();
            }
            case End -> {
                if (key.isCtrlDown()) buf.moveToDocumentEnd();
                else buf.moveToLineEnd();
            }
            case PageUp -> buf.moveCursor(-Math.max(1, getSize().getRows() - 1), 0);
            case PageDown -> buf.moveCursor(Math.max(1, getSize().getRows() - 1), 0);
            case Backspace -> buf.backspace();
            case Delete -> buf.delete();
            case Enter -> buf.insertChar('\n');
            case Tab -> {
                if (key.isShiftDown()) {
                    String line = buf.getLine(buf.cursorLine());
                    int col = buf.cursorColumn();
                    int remove = 0;
                    while (remove < 4 && col - remove > 0 && line.charAt(col - remove - 1) == ' ') {
                        remove++;
                    }
                    for (int i = 0; i < remove; i++) buf.backspace();
                } else {
                    buf.insertChar('\t');
                }
            }
            case Character -> {
                if (!key.isCtrlDown() && !key.isAltDown()) {
                    buf.insertChar(key.getCharacter());
                } else {
                    return false;
                }
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    private boolean isGlobalKey(KeyStroke key) {
        if (key.getKeyType() == KeyType.F1 || key.getKeyType() == KeyType.F2 || key.getKeyType() == KeyType.F3
                || key.getKeyType() == KeyType.F4 || key.getKeyType() == KeyType.F6
                || key.getKeyType() == KeyType.F10) {
            return true;
        }
        if (key.isCtrlDown() && key.getKeyType() == KeyType.Character) {
            char c = Character.toLowerCase(key.getCharacter());
            return c == 'q' || c == 's' || c == 'o' || c == 'n' || c == 'w' || c == '\t';
        }
        if (key.isCtrlDown() && key.isShiftDown() && key.getKeyType() == KeyType.Tab) {
            return true;
        }
        if (key.isAltDown() && key.getKeyType() == KeyType.Character) {
            return key.getCharacter() >= '1' && key.getCharacter() <= '9';
        }
        return key.getKeyType() == KeyType.Insert;
    }

    public void resetScroll() {
        scrollRow = 0;
    }
}
