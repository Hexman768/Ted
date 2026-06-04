package com.ted.editor;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Panel;

/**
 * Borland Turbo C++ / MS-DOS EDIT color palette.
 */
public final class TurboTheme {
    /** Classic Borland editor navy (#000080). */
    private static final TextColor TURBO_BLUE = new TextColor.RGB(0, 0, 128);
    /** DOS silver menu/status bar (#C0C0C0), as in Turbo C++ and MS-DOS EDIT. */
    private static final TextColor CHROME_SILVER = new TextColor.RGB(192, 192, 192);

    public static final TextColor BG = TURBO_BLUE;
    public static final TextColor FG = TextColor.ANSI.WHITE;

    public static final TextColor CHROME_BG = CHROME_SILVER;
    public static final TextColor CHROME_FG = TextColor.ANSI.BLACK;
    public static final TextColor CHROME_HOTKEY = TextColor.ANSI.RED;

    public static final TextColor MENU_BG = CHROME_BG;
    public static final TextColor MENU_FG = CHROME_FG;
    public static final TextColor MENU_HOTKEY = CHROME_HOTKEY;

    public static final TextColor TAB_ACTIVE_BG = TURBO_BLUE;
    public static final TextColor TAB_ACTIVE_FG = TextColor.ANSI.WHITE_BRIGHT;
    public static final TextColor TAB_INACTIVE_BG = TURBO_BLUE;
    public static final TextColor TAB_INACTIVE_FG = TextColor.ANSI.CYAN;

    public static final TextColor STATUS_BG = CHROME_BG;
    public static final TextColor STATUS_FG = CHROME_FG;

    public static final TextColor GUTTER_BG = TURBO_BLUE;
    public static final TextColor GUTTER_FG = TextColor.ANSI.CYAN;

    // Syntax token colors
    public static final TextColor SYNTAX_DEFAULT = TextColor.ANSI.WHITE;
    public static final TextColor SYNTAX_KEYWORD = TextColor.ANSI.WHITE_BRIGHT;
    public static final TextColor SYNTAX_STRING = TextColor.ANSI.YELLOW;
    public static final TextColor SYNTAX_CHAR = TextColor.ANSI.YELLOW;
    public static final TextColor SYNTAX_COMMENT = TextColor.ANSI.GREEN;
    public static final TextColor SYNTAX_NUMBER = TextColor.ANSI.CYAN;
    public static final TextColor SYNTAX_PREPROCESSOR = TextColor.ANSI.MAGENTA;
    public static final TextColor SYNTAX_TYPE = TextColor.ANSI.CYAN;
    public static final TextColor SYNTAX_OPERATOR = TextColor.ANSI.WHITE;
    public static final TextColor SYNTAX_TAG = TextColor.ANSI.YELLOW;
    public static final TextColor SYNTAX_ATTRIBUTE = TextColor.ANSI.CYAN;
    public static final TextColor SYNTAX_BUILTIN = TextColor.ANSI.GREEN;
    public static final TextColor CURSOR_FG = TextColor.ANSI.BLACK;
    public static final TextColor CURSOR_BG = TextColor.ANSI.WHITE_BRIGHT;

    public static final String[] MENU_ITEMS = {
            "File", "Edit", "Search", "Run", "Compile", "Window", "Help"
    };

    public static final String STATUS_HINTS = " F1=Help  F2=Save  F3=Open  F4=New  F6=Tab  F10=Exit ";

    public static final int MENU_HEIGHT = 1;
    public static final int TAB_HEIGHT = 1;
    public static final int STATUS_HEIGHT = 1;
    public static final int GUTTER_WIDTH = 5;

    private TurboTheme() {}

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
