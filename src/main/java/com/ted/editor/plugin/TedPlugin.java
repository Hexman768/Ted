package com.ted.editor.plugin;

public interface TedPlugin {
    String name();
    void init(PluginContext context);
}
