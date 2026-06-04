package com.ted.editor.model;

import com.ted.editor.Language;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TabManager {
    private final List<EditorTab> tabs = new ArrayList<>();
    private int activeIndex;
    private int nextId = 1;

    public TabManager() {
        tabs.add(new EditorTab(nextId++, EditorBuffer.untitled("UNTITLED", Language.PLAIN)));
        activeIndex = 0;
    }

    public List<EditorTab> tabs() {
        return tabs;
    }

    public EditorTab activeTab() {
        return tabs.get(activeIndex);
    }

    public EditorBuffer activeBuffer() {
        return activeTab().buffer();
    }

    public int activeIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int index) {
        if (index >= 0 && index < tabs.size()) {
            activeIndex = index;
        }
    }

    public void nextTab() {
        if (tabs.size() > 1) {
            activeIndex = (activeIndex + 1) % tabs.size();
        }
    }

    public void previousTab() {
        if (tabs.size() > 1) {
            activeIndex = (activeIndex - 1 + tabs.size()) % tabs.size();
        }
    }

    public EditorTab openBuffer(EditorBuffer buffer) {
        EditorTab tab = new EditorTab(nextId++, buffer);
        tabs.add(tab);
        activeIndex = tabs.size() - 1;
        return tab;
    }

    public boolean replaceDefaultIfEmpty(EditorBuffer buffer) {
        if (tabs.size() != 1) {
            return false;
        }
        EditorBuffer current = tabs.get(0).buffer();
        if (current.getPath() != null || current.isModified() || current.lineCount() != 1
                || !current.getLine(0).isEmpty()) {
            return false;
        }
        tabs.set(0, new EditorTab(nextId++, buffer));
        activeIndex = 0;
        return true;
    }

    public EditorTab newUntitled() {
        String name = "UNTITLED";
        int n = 1;
        while (nameTaken(name)) {
            n++;
            name = "UNTITLED" + n;
        }
        return openBuffer(EditorBuffer.untitled(name, Language.PLAIN));
    }

    private boolean nameTaken(String name) {
        return tabs.stream().anyMatch(t -> t.buffer().displayName().equals(name));
    }

    public boolean closeActive() {
        if (tabs.size() <= 1) {
            return false;
        }
        tabs.remove(activeIndex);
        if (activeIndex >= tabs.size()) {
            activeIndex = tabs.size() - 1;
        }
        return true;
    }

    public Optional<EditorTab> findByIndex(int index) {
        if (index >= 0 && index < tabs.size()) {
            return Optional.of(tabs.get(index));
        }
        return Optional.empty();
    }
}
