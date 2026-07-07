package com.ted.editor.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ComponentRenderer;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.ted.editor.TurboTheme;
import com.ted.editor.model.EditorTab;
import com.ted.editor.model.TabManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class ChromePanel {
    private ChromePanel() {}

    public static Panel menuBar() {
        MenuPanel panel = new MenuPanel();
        panel.setPreferredSize(new TerminalSize(1, TurboTheme.MENU_HEIGHT));
        panel.setFillColorOverride(TurboTheme.CHROME_BG);
        return panel;
    }

    public static Panel tabBar(TabManager tabManager) {
        TabPanel panel = new TabPanel(tabManager);
        panel.setPreferredSize(new TerminalSize(1, TurboTheme.TAB_HEIGHT));
        panel.setFillColorOverride(TurboTheme.BG);
        return panel;
    }

    public static Panel statusBar(AtomicReference<String> statusText) {
        StatusPanel panel = new StatusPanel(statusText);
        panel.setPreferredSize(new TerminalSize(1, TurboTheme.STATUS_HEIGHT));
        panel.setFillColorOverride(TurboTheme.CHROME_BG);
        return panel;
    }

    /** Fills a full-width gray chrome bar (menu or status). */
    private static void fillChromeBar(TextGUIGraphics tg, TerminalSize size) {
        tg.setBackgroundColor(TurboTheme.CHROME_BG);
        tg.setForegroundColor(TurboTheme.CHROME_FG);
        tg.fill(' ');
    }

    /**
     * Top menu bar: silver background, black labels, red Borland-style hotkey letters.
     */
    public static void drawMenu(TextGUIGraphics tg, TerminalSize size) {
        fillChromeBar(tg, size);
        int x = 1;
        for (String item : TurboTheme.MENU_ITEMS) {
            if (x >= size.getColumns()) break;
            if (!item.isEmpty()) {
                tg.setBackgroundColor(TurboTheme.CHROME_BG);
                tg.setForegroundColor(TurboTheme.CHROME_HOTKEY);
                tg.setCharacter(x++, 0, item.charAt(0));
                tg.setForegroundColor(TurboTheme.CHROME_FG);
                for (int i = 1; i < item.length() && x < size.getColumns(); i++) {
                    tg.setCharacter(x++, 0, item.charAt(i));
                }
            }
            x += 2;
        }
    }

    /**
     * Bottom status bar: silver background, cursor info left, function-key hints right.
     */
    public static void drawStatus(TextGUIGraphics tg, TerminalSize size, String status) {
        fillChromeBar(tg, size);
        String right = TurboTheme.STATUS_HINTS;
        int hintStart = Math.max(0, size.getColumns() - right.length());
        String left = status != null ? status : "";
        if (left.length() > hintStart) {
            left = left.substring(0, Math.max(0, hintStart - 1));
        }
        tg.setBackgroundColor(TurboTheme.CHROME_BG);
        tg.setForegroundColor(TurboTheme.CHROME_FG);
        for (int x = 0; x < hintStart && x < left.length(); x++) {
            tg.setCharacter(x, 0, left.charAt(x));
        }
        drawHintText(tg, hintStart, right, size.getColumns());
    }

    private static void drawHintText(TextGUIGraphics tg, int startX, String text, int maxCols) {
        tg.setBackgroundColor(TurboTheme.CHROME_BG);
        int x = startX;
        for (int i = 0; i < text.length() && x < maxCols; i++, x++) {
            char ch = text.charAt(i);
            if (ch == 'F' && i + 1 < text.length() && Character.isDigit(text.charAt(i + 1))) {
                tg.setForegroundColor(TurboTheme.CHROME_HOTKEY);
                tg.setCharacter(x, 0, ch);
                tg.setForegroundColor(TurboTheme.CHROME_FG);
            } else {
                tg.setForegroundColor(TurboTheme.CHROME_FG);
                tg.setCharacter(x, 0, ch);
            }
        }
    }

    private static void drawTabs(TextGUIGraphics tg, TerminalSize size, TabManager tabManager) {
        tg.setBackgroundColor(TurboTheme.BG);
        tg.setForegroundColor(TurboTheme.FG);
        tg.fill(' ');
        List<EditorTab> tabs = tabManager.tabs();
        int active = tabManager.activeIndex();
        int x = 1;
        for (int i = 0; i < tabs.size() && x < size.getColumns() - 2; i++) {
            EditorTab tab = tabs.get(i);
            String label = " " + tab.title() + " ";
            boolean isActive = i == active;
            tg.setBackgroundColor(isActive ? TurboTheme.TAB_ACTIVE_BG : TurboTheme.TAB_INACTIVE_BG);
            tg.setForegroundColor(isActive ? TurboTheme.TAB_ACTIVE_FG : TurboTheme.TAB_INACTIVE_FG);
            for (int c = 0; c < label.length() && x < size.getColumns(); c++, x++) {
                tg.setCharacter(x, 0, label.charAt(c));
            }
            if (isActive && x < size.getColumns()) {
                tg.setCharacter(x++, 0, '\u25b6');
            }
            x++;
        }
        if (x < size.getColumns()) {
            String hint = " F6:Next Tab ";
            tg.setForegroundColor(TurboTheme.GUTTER_FG);
            int start = Math.max(x, size.getColumns() - hint.length());
            for (int i = 0; i < hint.length() && start + i < size.getColumns(); i++) {
                tg.setCharacter(start + i, 0, hint.charAt(i));
            }
        }
    }

    private static final class MenuPanel extends Panel {
        @Override
        protected ComponentRenderer<Panel> createDefaultRenderer() {
            return new ComponentRenderer<>() {
                @Override
                public TerminalSize getPreferredSize(Panel c) {
                    return c.getSize() != null ? c.getSize() : new TerminalSize(1, 1);
                }

                @Override
                public void drawComponent(TextGUIGraphics tg, Panel c) {
                    drawMenu(tg, c.getSize());
                }
            };
        }
    }

    private static final class TabPanel extends Panel {
        private final TabManager tabManager;

        TabPanel(TabManager tabManager) {
            this.tabManager = tabManager;
        }

        @Override
        protected ComponentRenderer<Panel> createDefaultRenderer() {
            return new ComponentRenderer<>() {
                @Override
                public TerminalSize getPreferredSize(Panel c) {
                    return c.getSize() != null ? c.getSize() : new TerminalSize(1, 1);
                }

                @Override
                public void drawComponent(TextGUIGraphics tg, Panel c) {
                    drawTabs(tg, c.getSize(), tabManager);
                }
            };
        }
    }

    private static final class StatusPanel extends Panel {
        private final AtomicReference<String> statusText;

        StatusPanel(AtomicReference<String> statusText) {
            this.statusText = statusText;
        }

        @Override
        protected ComponentRenderer<Panel> createDefaultRenderer() {
            return new ComponentRenderer<>() {
                @Override
                public TerminalSize getPreferredSize(Panel c) {
                    return c.getSize() != null ? c.getSize() : new TerminalSize(1, 1);
                }

                @Override
                public void drawComponent(TextGUIGraphics tg, Panel c) {
                    drawStatus(tg, c.getSize(), statusText.get());
                }
            };
        }
    }
}
