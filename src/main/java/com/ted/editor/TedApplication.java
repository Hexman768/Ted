package com.ted.editor;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.ted.editor.model.EditorBuffer;
import com.ted.editor.model.TabManager;
import com.ted.editor.theme.Theme;
import com.ted.editor.theme.ThemeManager;
import com.ted.editor.theme.Themes;
import com.ted.editor.ui.ChromePanel;
import com.ted.editor.ui.EditorPanel;
import com.ted.editor.ui.FileDialogs;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class TedApplication {
    private final TabManager tabManager = new TabManager();
    private final AtomicReference<String> statusText = new AtomicReference<>("  TED - Turbo Editor  ");
    private WindowBasedTextGUI gui;
    private EmptySpace guiBackground;
    private EditorPanel editorPanel;
    private Panel rootPanel;
    private Panel editorContainer;
    private Panel menuPanel;
    private Panel tabPanel;
    private Panel statusPanel;
    private BasicWindow mainWindow;

    public void run(String[] args) throws Exception {
        Theme startupTheme = resolveStartupTheme();
        ThemeManager.setTheme(startupTheme);

        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setTerminalEmulatorTitle("TED - Turbo Editor");
        factory.setInitialTerminalSize(new TerminalSize(100, 30));

        Screen screen = factory.createScreen();
        screen.startScreen();
        screen.setCursorPosition(null);

        guiBackground = new EmptySpace(TurboTheme.BG);
        gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), guiBackground);

        if (args.length > 0) {
            boolean first = true;
            for (String arg : args) {
                Path path = Paths.get(arg);
                EditorBuffer buffer = EditorBuffer.fromPath(path);
                if (first && tabManager.replaceDefaultIfEmpty(buffer)) {
                    first = false;
                } else {
                    tabManager.openBuffer(buffer);
                }
            }
        }

        buildUi();
        ThemeManager.addListener(this::applyThemeToUi);
        editorPanel.takeFocus();
        mainWindow.waitUntilClosed();
        screen.stopScreen();
    }

    private static Theme resolveStartupTheme() {
        String fromProp = System.getProperty("ted.theme");
        if (fromProp != null && !fromProp.isBlank()) {
            return Themes.byId(fromProp.trim());
        }
        String fromEnv = System.getenv("TED_THEME");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return Themes.byId(fromEnv.trim());
        }
        return Themes.TURBO;
    }

    private void buildUi() {
        rootPanel = new Panel();
        rootPanel.setFillColorOverride(TurboTheme.BG);
        rootPanel.setLayoutManager(new BorderLayout());

        menuPanel = ChromePanel.menuBar();
        tabPanel = ChromePanel.tabBar(tabManager);
        statusPanel = ChromePanel.statusBar(statusText);

        editorPanel = new EditorPanel(tabManager);
        editorPanel.setGlobalKeyHandler(this::handleGlobalKey);
        editorPanel.setStatusUpdater(s -> {
            statusText.set(s);
            refreshChrome();
        });
        editorPanel.setRepaintRequest(this::refreshChrome);

        editorContainer = TurboTheme.createBorderedPanel();
        editorContainer.setLayoutManager(new BorderLayout());
        editorContainer.addComponent(editorPanel.setLayoutData(BorderLayout.Location.CENTER));

        Panel chromeTop = new Panel();
        chromeTop.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        chromeTop.addComponent(menuPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)));
        chromeTop.addComponent(tabPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)));
        rootPanel.addComponent(chromeTop.setLayoutData(BorderLayout.Location.TOP));
        rootPanel.addComponent(editorContainer.setLayoutData(BorderLayout.Location.CENTER));
        rootPanel.addComponent(statusPanel.setLayoutData(BorderLayout.Location.BOTTOM));

        mainWindow = new BasicWindow();
        mainWindow.setComponent(rootPanel);
        mainWindow.setHints(List.of(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS, Window.Hint.FIT_TERMINAL_WINDOW));
        mainWindow.setCloseWindowWithEscape(false);
        gui.addWindow(mainWindow);
        mainWindow.setFocusedInteractable(editorPanel);
    }

    private void applyThemeToUi(Theme theme) {
        if (guiBackground != null) {
            guiBackground.setColor(TurboTheme.BG);
        }
        if (rootPanel != null) {
            rootPanel.setFillColorOverride(TurboTheme.BG);
        }
        if (editorContainer != null) {
            editorContainer.setFillColorOverride(TurboTheme.BG);
        }
        if (menuPanel != null) {
            menuPanel.setFillColorOverride(TurboTheme.CHROME_BG);
        }
        if (tabPanel != null) {
            tabPanel.setFillColorOverride(TurboTheme.BG);
        }
        if (statusPanel != null) {
            statusPanel.setFillColorOverride(TurboTheme.CHROME_BG);
        }
        statusText.set(" Theme: " + theme.displayName() + " ");
        refreshChrome();
        if (editorPanel != null) {
            editorPanel.takeFocus();
        }
    }

    private void refreshChrome() {
        if (menuPanel != null) menuPanel.invalidate();
        if (tabPanel != null) tabPanel.invalidate();
        if (statusPanel != null) statusPanel.invalidate();
        if (editorPanel != null) editorPanel.invalidate();
        if (editorContainer != null) editorContainer.invalidate();
        if (rootPanel != null) rootPanel.invalidate();
    }

    private void handleGlobalKey(KeyStroke key) {
        if (key.getKeyType() == KeyType.F1) showHelp();
        else if (key.getKeyType() == KeyType.F2) saveFile();
        else if (key.getKeyType() == KeyType.F3) openFile();
        else if (key.getKeyType() == KeyType.F4) {
            tabManager.newUntitled();
            refreshChrome();
            editorPanel.takeFocus();
        } else if (key.getKeyType() == KeyType.F6) {
            tabManager.nextTab();
            editorPanel.resetScroll();
            refreshChrome();
            editorPanel.takeFocus();
        } else if (key.getKeyType() == KeyType.F9) showFileMenu();
        else if (key.getKeyType() == KeyType.F10) attemptExit();
        else if (key.isCtrlDown() && key.getKeyType() == KeyType.Character) {
            switch (Character.toLowerCase(key.getCharacter())) {
                case 'q' -> attemptExit();
                case 's' -> saveFile();
                case 'o' -> openFile();
                case 't' -> showThemeMenu();
                case 'n' -> {
                    tabManager.newUntitled();
                    refreshChrome();
                    editorPanel.takeFocus();
                }
                case 'w' -> closeTab();
                case '\t' -> {
                    if (key.isShiftDown()) tabManager.previousTab();
                    else tabManager.nextTab();
                    editorPanel.resetScroll();
                    refreshChrome();
                    editorPanel.takeFocus();
                }
                default -> {}
            }
        } else if (key.isAltDown() && key.getKeyType() == KeyType.Character) {
            char c = Character.toLowerCase(key.getCharacter());
            if (c >= '1' && c <= '9') switchToTab(c - '1');
            else if (c == 'f') showFileMenu();
        } else if (key.getKeyType() == KeyType.Insert) {
            if (key.isCtrlDown()) copyLine();
            else if (key.isShiftDown()) pasteClipboard();
        }
    }

    private void showFileMenu() {
        new ActionListDialogBuilder()
                .setTitle("File")
                .setDescription("File menu")
                .setCanCancel(true)
                .addAction("Open...", this::openFile)
                .addAction("Save", this::saveFile)
                .addAction("Theme...", this::showThemeMenu)
                .addAction("Exit", this::attemptExit)
                .build()
                .showDialog(gui);
        editorPanel.takeFocus();
    }

    private void showThemeMenu() {
        ActionListDialogBuilder builder = new ActionListDialogBuilder()
                .setTitle("Theme")
                .setDescription("Choose a color theme")
                .setCanCancel(true);
        Theme active = ThemeManager.current();
        for (Theme theme : ThemeManager.available()) {
            String label = theme.displayName() + (theme.equals(active) ? "  *" : "");
            builder.addAction(label, () -> ThemeManager.setTheme(theme));
        }
        builder.build().showDialog(gui);
        editorPanel.takeFocus();
    }

    private void switchToTab(int index) {
        tabManager.setActiveIndex(index);
        editorPanel.resetScroll();
        refreshChrome();
        editorPanel.takeFocus();
    }

    private void showHelp() {
        FileDialogs.message(gui, "TED Help",
                "F1 Help  F2 Save  F3 Open  F4 New  F6 Next Tab  F9 File Menu  F10 Exit\n" +
                "Ctrl+S Save  Ctrl+O Open  Ctrl+N New  Ctrl+W Close Tab  Ctrl+T Theme\n" +
                "Ctrl+Tab / Shift+Ctrl+Tab switch tabs  Alt+1..9 jump to tab\n" +
                "Ctrl+Ins copy line  Shift+Ins paste\n" +
                "Startup theme: -Dted.theme=light or TED_THEME=light");
    }

    private void openFile() {
        Path start = Optional.ofNullable(tabManager.activeBuffer().getPath())
                .map(Path::getParent)
                .orElse(Paths.get(System.getProperty("user.dir")));
        FileDialogs.chooseOpenFile(gui, start)
                .ifPresent(path -> {
                    try {
                        openFileQuiet(path);
                        refreshChrome();
                        editorPanel.takeFocus();
                    } catch (Exception e) {
                        FileDialogs.message(gui, "Error", "Cannot open: " + e.getMessage());
                    }
                });
        editorPanel.takeFocus();
    }

    private void openFileQuiet(Path path) throws Exception {
        EditorBuffer buffer = EditorBuffer.fromPath(path);
        if (!tabManager.replaceDefaultIfEmpty(buffer)) {
            tabManager.openBuffer(buffer);
        }
    }

    private void saveFile() {
        EditorBuffer buf = tabManager.activeBuffer();
        try {
            if (buf.getPath() == null) {
                Path start = Paths.get(System.getProperty("user.dir"));
                FileDialogs.chooseSaveFile(gui, start, "untitled.txt")
                        .ifPresent(p -> {
                            try {
                                if (java.nio.file.Files.exists(p)
                                        && !FileDialogs.confirm(gui, "Overwrite " + p.getFileName() + "?")) {
                                    return;
                                }
                                buf.setPath(p);
                                buf.save();
                                refreshChrome();
                            } catch (Exception e) {
                                FileDialogs.message(gui, "Error", e.getMessage());
                            }
                        });
                editorPanel.takeFocus();
            } else {
                buf.save();
                refreshChrome();
            }
        } catch (Exception e) {
            FileDialogs.message(gui, "Error", e.getMessage());
        }
    }

    private void closeTab() {
        EditorBuffer buf = tabManager.activeBuffer();
        if (buf.isModified() && !FileDialogs.confirm(gui, "Close without saving " + buf.displayName() + "?")) {
            return;
        }
        if (!tabManager.closeActive()) {
            attemptExit();
            return;
        }
        editorPanel.resetScroll();
        refreshChrome();
        editorPanel.takeFocus();
    }

    private void attemptExit() {
        boolean anyModified = tabManager.tabs().stream().anyMatch(t -> t.buffer().isModified());
        if (anyModified && !FileDialogs.confirm(gui, "Exit without saving changes?")) {
            return;
        }
        mainWindow.close();
    }

    private void copyLine() {
        EditorBuffer buf = tabManager.activeBuffer();
        String line = buf.getLine(buf.cursorLine());
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(line), null);
            statusText.set(" Line copied to clipboard ");
            refreshChrome();
        } catch (Exception ignored) {
            statusText.set(" Clipboard unavailable ");
            refreshChrome();
        }
    }

    private void pasteClipboard() {
        try {
            String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);
            if (data != null) {
                tabManager.activeBuffer().insertText(data.replace("\r\n", "\n"));
                editorPanel.refreshScroll();
                refreshChrome();
            }
        } catch (Exception ignored) {
            statusText.set(" Clipboard unavailable ");
            refreshChrome();
        }
    }
}
