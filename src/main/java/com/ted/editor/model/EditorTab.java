package com.ted.editor.model;

public class EditorTab {
    private final EditorBuffer buffer;
    private final int id;

    public EditorTab(int id, EditorBuffer buffer) {
        this.id = id;
        this.buffer = buffer;
    }

    public int id() {
        return id;
    }

    public EditorBuffer buffer() {
        return buffer;
    }

    public String title() {
        String name = buffer.displayName();
        return buffer.isModified() ? name + " *" : name;
    }
}
