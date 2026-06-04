package com.ted.editor.ui;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import java.util.Optional;

public final class FileDialogs {
    private FileDialogs() {}

    public static Optional<String> prompt(WindowBasedTextGUI gui, String title, String label, String initial) {
        String result = TextInputDialog.showDialog(gui, title, label, initial != null ? initial : "");
        if (result == null || result.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(result.trim());
    }

    public static boolean confirm(WindowBasedTextGUI gui, String message) {
        MessageDialogButton result = MessageDialog.showMessageDialog(
                gui, "Confirm", message, MessageDialogButton.Yes, MessageDialogButton.No);
        return result == MessageDialogButton.Yes;
    }

    public static void message(WindowBasedTextGUI gui, String title, String message) {
        MessageDialog.showMessageDialog(gui, title, message, MessageDialogButton.OK);
    }
}
