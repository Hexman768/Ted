package com.ted.vim;

import com.ted.editor.plugin.PluginContext;
import com.ted.editor.plugin.TedPlugin;

public final class VimPlugin implements TedPlugin {

    @Override
    public String name() {
        return "vim";
    }

    @Override
    public void init(PluginContext context) {
        context.registerKeyHandler(new VimInputController(context));
        context.setStatusExtra("VIM");
    }
}
