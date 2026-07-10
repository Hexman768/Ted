package com.ted.editor;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.ted.editor.model.EditorBuffer;
import com.ted.editor.model.TabManager;
import com.ted.editor.plugin.DefaultPluginContext;
import com.ted.editor.plugin.PluginContext;
import com.ted.editor.plugin.TedPlugin;
import com.ted.editor.ui.ChromePanel;
import com.ted.editor.ui.EditorPanel;
import com.ted.editor.ui.FileDialogs;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

public class TedApplication {
    private final TabManager tabManager = new TabManager();
    private final AtomicReference<String> statusText = new AtomicReference<>("  TED - Turbo Editor  ");
    private WindowBasedTextGUI gui;
    private EditorPanel editorPanel;
    private Panel menuPanel;
    private Panel tabPanel;
    private Panel statusPanel;
    private BasicWindow mainWindow;

    public void run(String[] args) throws Exception {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setTerminalEmulatorTitle("TED - Turbo Editor");
        factory.setInitialTerminalSize(new TerminalSize(100, 30));

        Screen screen = factory.createScreen();
        screen.startScreen();
        screen.setCursorPosition(null);

        gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TurboTheme.BG));

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
        loadPlugins();
        editorPanel.takeFocus();
        mainWindow.waitUntilClosed();
        screen.stopScreen();
    }

    private void buildUi() {
        Panel root = new Panel();
        root.setFillColorOverride(TurboTheme.BG);
        root.setLayoutManager(new BorderLayout());

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

        Panel editorContainer = TurboTheme.createBorderedPanel();
        editorContainer.setLayoutManager(new BorderLayout());
        editorContainer.addComponent(editorPanel.setLayoutData(BorderLayout.Location.CENTER));

        Panel chromeTop = new Panel();
        chromeTop.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        chromeTop.addComponent(menuPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)));
        chromeTop.addComponent(tabPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)));
        root.addComponent(chromeTop.setLayoutData(BorderLayout.Location.TOP));
        root.addComponent(editorContainer.setLayoutData(BorderLayout.Location.CENTER));
        root.addComponent(statusPanel.setLayoutData(BorderLayout.Location.BOTTOM));

        mainWindow = new BasicWindow();
        mainWindow.setComponent(root);
        mainWindow.setHints(List.of(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS, Window.Hint.FIT_TERMINAL_WINDOW));
        mainWindow.setCloseWindowWithEscape(false);
        gui.addWindow(mainWindow);
        mainWindow.setFocusedInteractable(editorPanel);
    }

    private void loadPlugins() {
        PluginContext pluginContext = new DefaultPluginContext(tabManager, editorPanel, statusText);
        ServiceLoader<TedPlugin> loader = ServiceLoader.load(TedPlugin.class);
        int count = 0;
        for (TedPlugin plugin : loader) {
            plugin.init(pluginContext);
            System.err.println("TED: loaded plugin " + plugin.name());
            count++;
        }
        if (count == 0) {
            System.err.println("TED: no plugins found (check META-INF/services/com.ted.editor.plugin.TedPlugin)");
        }
        editorPanel.refreshScroll();
        refreshChrome();
    }

    private void refreshChrome() {
        if (menuPanel != null) menuPanel.invalidate();
        if (tabPanel != null) tabPanel.invalidate();
        if (statusPanel != null) statusPanel.invalidate();
        editorPanel.invalidate();
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
        } else if (key.getKeyType() == KeyType.F10) attemptExit();
        else if (key.isCtrlDown() && key.getKeyType() == KeyType.Character) {
            switch (Character.toLowerCase(key.getCharacter())) {
                case 'q' -> attemptExit();
                case 's' -> saveFile();
                case 'o' -> openFile();
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
            char c = key.getCharacter();
            if (c >= '1' && c <= '9') switchToTab(c - '1');
        } else if (key.getKeyType() == KeyType.Insert) {
            if (key.isCtrlDown()) copyLine();
            else if (key.isShiftDown()) pasteClipboard();
        }
    }

    private void switchToTab(int index) {
        tabManager.setActiveIndex(index);
        editorPanel.resetScroll();
        refreshChrome();
        editorPanel.takeFocus();
    }

    private void showHelp() {
        FileDialogs.message(gui, "TED Help",
                "F1 Help  F2 Save  F3 Open  F4 New  F6 Next Tab  F10 Exit\n" +
                "Ctrl+S Save  Ctrl+O Open  Ctrl+N New  Ctrl+W Close Tab\n" +
                "Ctrl+Tab / Shift+Ctrl+Tab switch tabs  Alt+1..9 jump to tab\n" +
                "Ctrl+Ins copy line  Shift+Ins paste");
    }

    private void openFile() {
        FileDialogs.prompt(gui, "Open File", "Path:", System.getProperty("user.dir"))
                .ifPresent(path -> {
                    try {
                        openFileQuiet(Paths.get(path));
                        refreshChrome();
                        editorPanel.takeFocus();
                    } catch (Exception e) {
                        FileDialogs.message(gui, "Error", "Cannot open: " + e.getMessage());
                    }
                });
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
                FileDialogs.prompt(gui, "Save As", "Path:", "untitled.txt")
                        .ifPresent(p -> {
                            try {
                                buf.setPath(Paths.get(p));
                                buf.save();
                                refreshChrome();
                            } catch (Exception e) {
                                FileDialogs.message(gui, "Error", e.getMessage());
                            }
                        });
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
