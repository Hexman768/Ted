package com.ted.editor;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Panel;
import com.ted.editor.theme.Theme;
import com.ted.editor.theme.Themes;

/**
 * Active theme colors and shared chrome constants.
 * Color fields are updated when {@link ThemeManager#setTheme(Theme)} runs.
 */
public final class TurboTheme {
    public static TextColor BG;
    public static TextColor FG;
    public static TextColor CHROME_BG;
    public static TextColor CHROME_FG;
    public static TextColor CHROME_HOTKEY;
    public static TextColor MENU_BG;
    public static TextColor MENU_FG;
    public static TextColor MENU_HOTKEY;
    public static TextColor TAB_ACTIVE_BG;
    public static TextColor TAB_ACTIVE_FG;
    public static TextColor TAB_INACTIVE_BG;
    public static TextColor TAB_INACTIVE_FG;
    public static TextColor STATUS_BG;
    public static TextColor STATUS_FG;
    public static TextColor GUTTER_BG;
    public static TextColor GUTTER_FG;
    public static TextColor SYNTAX_DEFAULT;
    public static TextColor SYNTAX_KEYWORD;
    public static TextColor SYNTAX_STRING;
    public static TextColor SYNTAX_CHAR;
    public static TextColor SYNTAX_COMMENT;
    public static TextColor SYNTAX_NUMBER;
    public static TextColor SYNTAX_PREPROCESSOR;
    public static TextColor SYNTAX_TYPE;
    public static TextColor SYNTAX_OPERATOR;
    public static TextColor SYNTAX_TAG;
    public static TextColor SYNTAX_ATTRIBUTE;
    public static TextColor SYNTAX_BUILTIN;
    public static TextColor CURSOR_FG;
    public static TextColor CURSOR_BG;

    public static final String[] MENU_ITEMS = {
            "File", "Edit", "Search", "Run", "Compile", "Window", "Help"
    };

    public static final String STATUS_HINTS = " F1=Help  F2=Save  F3=Open  F9=Menu  F10=Exit ";

    public static final int MENU_HEIGHT = 1;
    public static final int TAB_HEIGHT = 1;
    public static final int STATUS_HEIGHT = 1;
    public static final int GUTTER_WIDTH = 5;

    static {
        apply(Themes.TURBO);
    }

    private TurboTheme() {}

    public static void apply(Theme theme) {
        BG = theme.bg();
        FG = theme.fg();
        CHROME_BG = theme.chromeBg();
        CHROME_FG = theme.chromeFg();
        CHROME_HOTKEY = theme.chromeHotkey();
        MENU_BG = CHROME_BG;
        MENU_FG = CHROME_FG;
        MENU_HOTKEY = CHROME_HOTKEY;
        TAB_ACTIVE_BG = theme.tabActiveBg();
        TAB_ACTIVE_FG = theme.tabActiveFg();
        TAB_INACTIVE_BG = theme.tabInactiveBg();
        TAB_INACTIVE_FG = theme.tabInactiveFg();
        STATUS_BG = CHROME_BG;
        STATUS_FG = CHROME_FG;
        GUTTER_BG = theme.gutterBg();
        GUTTER_FG = theme.gutterFg();
        SYNTAX_DEFAULT = theme.syntaxDefault();
        SYNTAX_KEYWORD = theme.syntaxKeyword();
        SYNTAX_STRING = theme.syntaxString();
        SYNTAX_CHAR = theme.syntaxChar();
        SYNTAX_COMMENT = theme.syntaxComment();
        SYNTAX_NUMBER = theme.syntaxNumber();
        SYNTAX_PREPROCESSOR = theme.syntaxPreprocessor();
        SYNTAX_TYPE = theme.syntaxType();
        SYNTAX_OPERATOR = theme.syntaxOperator();
        SYNTAX_TAG = theme.syntaxTag();
        SYNTAX_ATTRIBUTE = theme.syntaxAttribute();
        SYNTAX_BUILTIN = theme.syntaxBuiltin();
        CURSOR_FG = theme.cursorFg();
        CURSOR_BG = theme.cursorBg();
    }

    public static void fillBackground(TextGraphics g, TerminalSize size) {
        g.setBackgroundColor(BG);
        g.setForegroundColor(FG);
        g.fill(' ');
    }

    public static Panel createBorderedPanel() {
        Panel panel = new Panel();
        panel.setFillColorOverride(BG);
        panel.withBorder(Borders.doubleLineBevel());
        return panel;
    }
}
