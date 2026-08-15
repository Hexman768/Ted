package com.ted.editor.theme;

import com.googlecode.lanterna.TextColor;

import java.util.List;

public final class Themes {
    private Themes() {}

    private static final TextColor TURBO_BLUE = new TextColor.RGB(0, 0, 128);
    private static final TextColor CHROME_SILVER = new TextColor.RGB(192, 192, 192);
    private static final TextColor LIGHT_BLUE = new TextColor.RGB(0, 0, 170);
    /** VGA bright yellow — classic Turbo keywords / accents. */
    private static final TextColor DOS_YELLOW = new TextColor.RGB(255, 255, 85);
    private static final TextColor DOS_GREEN = new TextColor.RGB(85, 255, 85);
    private static final TextColor DOS_RED = new TextColor.RGB(255, 85, 85);
    private static final TextColor DOS_CYAN = new TextColor.RGB(85, 255, 255);
    private static final TextColor DOS_MAGENTA = new TextColor.RGB(255, 85, 255);
    private static final TextColor DOS_WHITE = new TextColor.RGB(255, 255, 255);

    /** Classic Borland Turbo C++ / MS-DOS EDIT look. */
    public static final Theme TURBO = new Theme(
            "turbo",
            "Turbo C++",
            TURBO_BLUE,
            DOS_WHITE,
            CHROME_SILVER,
            TextColor.ANSI.BLACK,
            TextColor.ANSI.RED,
            TURBO_BLUE,
            DOS_YELLOW,
            TURBO_BLUE,
            DOS_CYAN,
            TURBO_BLUE,
            DOS_CYAN,
            DOS_WHITE,       // default / identifiers
            DOS_YELLOW,      // keywords
            DOS_RED,         // strings
            DOS_RED,         // chars
            DOS_GREEN,       // comments
            DOS_CYAN,        // numbers
            DOS_MAGENTA,     // preprocessor
            DOS_CYAN,        // types
            DOS_WHITE,       // operators
            DOS_YELLOW,      // tags
            DOS_CYAN,        // attributes
            DOS_GREEN,       // builtins
            TextColor.ANSI.BLACK,
            DOS_WHITE
    );

    /**
     * Light editor theme: white page, black body text, blue accents for
     * keywords / types / chrome — sparse two-tone highlighting.
     */
    public static final Theme LIGHT = new Theme(
            "light",
            "Light",
            TextColor.ANSI.WHITE_BRIGHT,
            TextColor.ANSI.BLACK,
            LIGHT_BLUE,
            TextColor.ANSI.WHITE_BRIGHT,
            TextColor.ANSI.YELLOW,
            TextColor.ANSI.WHITE_BRIGHT,
            LIGHT_BLUE,
            TextColor.ANSI.WHITE_BRIGHT,
            TextColor.ANSI.BLACK,
            TextColor.ANSI.WHITE_BRIGHT,
            LIGHT_BLUE,
            TextColor.ANSI.BLACK,
            LIGHT_BLUE,
            LIGHT_BLUE,
            LIGHT_BLUE,
            TextColor.ANSI.BLACK,
            LIGHT_BLUE,
            LIGHT_BLUE,
            LIGHT_BLUE,
            TextColor.ANSI.BLACK,
            LIGHT_BLUE,
            LIGHT_BLUE,
            LIGHT_BLUE,
            TextColor.ANSI.BLACK,
            TextColor.ANSI.YELLOW
    );

    public static List<Theme> all() {
        return List.of(TURBO, LIGHT);
    }

    public static Theme byId(String id) {
        for (Theme theme : all()) {
            if (theme.id().equalsIgnoreCase(id)) {
                return theme;
            }
        }
        return TURBO;
    }
}
