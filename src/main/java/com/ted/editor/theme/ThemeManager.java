package com.ted.editor.theme;

import com.ted.editor.TurboTheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Holds the active {@link Theme} and notifies listeners when it changes.
 */
public final class ThemeManager {
    private static Theme current = Themes.TURBO;
    private static final List<Consumer<Theme>> listeners = new CopyOnWriteArrayList<>();

    private ThemeManager() {}

    public static Theme current() {
        return current;
    }

    public static void setTheme(Theme theme) {
        Objects.requireNonNull(theme, "theme");
        boolean changed = !theme.equals(current);
        current = theme;
        TurboTheme.apply(theme);
        if (!changed) {
            return;
        }
        for (Consumer<Theme> listener : listeners) {
            listener.accept(theme);
        }
    }

    public static void addListener(Consumer<Theme> listener) {
        listeners.add(listener);
    }

    public static List<Theme> available() {
        return new ArrayList<>(Themes.all());
    }
}
