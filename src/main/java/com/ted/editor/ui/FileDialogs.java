package com.ted.editor.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.FileDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public final class FileDialogs {
    private FileDialogs() {}

    /**
     * Navigable file browser for opening an existing file.
     * Arrow keys / Enter to navigate directories; Open to select a file.
     */
    public static Optional<Path> chooseOpenFile(WindowBasedTextGUI gui, Path startDir) {
        File selected = new FileDialogBuilder()
                .setTitle("Open File")
                .setDescription("Select a file to open")
                .setActionLabel("Open")
                .setSuggestedSize(new TerminalSize(70, 20))
                .setSelectedFile(directoryOrCwd(startDir))
                .build()
                .showDialog(gui);
        if (selected == null || !selected.isFile()) {
            return Optional.empty();
        }
        return Optional.of(selected.toPath());
    }

    /**
     * Navigable file browser for Save As. Pick a directory, type/select a name, then Save.
     */
    public static Optional<Path> chooseSaveFile(WindowBasedTextGUI gui, Path startDir, String suggestedName) {
        File initial = directoryOrCwd(startDir);
        if (suggestedName != null && !suggestedName.isBlank()) {
            initial = new File(initial, suggestedName);
        }
        File selected = new FileDialogBuilder()
                .setTitle("Save As")
                .setDescription("Choose location and file name")
                .setActionLabel("Save")
                .setSuggestedSize(new TerminalSize(70, 20))
                .setSelectedFile(initial)
                .build()
                .showDialog(gui);
        if (selected == null) {
            return Optional.empty();
        }
        if (selected.isDirectory()) {
            return Optional.empty();
        }
        return Optional.of(selected.toPath());
    }

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

    private static File directoryOrCwd(Path startDir) {
        if (startDir != null) {
            File dir = startDir.toFile();
            if (dir.isDirectory()) {
                return dir;
            }
            File parent = dir.getParentFile();
            if (parent != null && parent.isDirectory()) {
                return parent;
            }
        }
        return new File(System.getProperty("user.dir"));
    }
}
