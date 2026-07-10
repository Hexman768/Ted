package com.ted.editor.plugin;

/** Entry point for TED plugins. Register via {@code META-INF/services/com.ted.editor.plugin.TedPlugin}. */
public interface TedPlugin {

    String name();

    void init(PluginContext context);
}
