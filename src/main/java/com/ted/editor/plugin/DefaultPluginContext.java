package com.ted.editor.plugin;

import com.ted.editor.model.TabManager;
import com.ted.editor.ui.EditorPanel;

import java.util.concurrent.atomic.AtomicReference;

public final class DefaultPluginContext implements PluginContext {

    private final TabManager tabManager;
    private final EditorPanel editorPanel;
    private final AtomicReference<String> statusText;

    public DefaultPluginContext(
            TabManager tabManager,
            EditorPanel editorPanel,
            AtomicReference<String> statusText) {
        this.tabManager = tabManager;
        this.editorPanel = editorPanel;
        this.statusText = statusText;
    }

    @Override
    public TabManager tabManager() {
        return tabManager;
    }

    @Override
    public EditorPanel editorPanel() {
        return editorPanel;
    }

    @Override
    public void registerKeyHandler(KeyInputHandler handler) {
        editorPanel.addKeyInputHandler(handler);
    }

    @Override
    public void setStatusText(String text) {
        statusText.set(text);
        editorPanel.refreshScroll();
    }

    @Override
    public void setStatusExtra(String extra) {
        editorPanel.setStatusExtra(extra);
        editorPanel.refreshScroll();
    }
}
