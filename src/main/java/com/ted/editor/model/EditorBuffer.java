package com.ted.editor.model;

import com.ted.editor.Language;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EditorBuffer {
    private final List<String> lines = new ArrayList<>();
    private int cursorLine;
    private int cursorColumn;
    private int preferredColumn;
    private boolean modified;
    private Path path;
    private Language language = Language.PLAIN;
    private boolean inBlockComment;

    public EditorBuffer() {
        lines.add("");
    }

    public static EditorBuffer fromPath(Path path) throws Exception {
        EditorBuffer buffer = new EditorBuffer();
        buffer.path = path;
        buffer.language = Language.fromPath(path);
        if (Files.exists(path)) {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            if (content.isEmpty()) {
                buffer.lines.clear();
                buffer.lines.add("");
            } else {
                buffer.lines.clear();
                String normalized = content.replace("\r\n", "\n").replace('\r', '\n');
                for (String line : normalized.split("\n", -1)) {
                    buffer.lines.add(line);
                }
                if (buffer.lines.isEmpty()) {
                    buffer.lines.add("");
                }
            }
        } else {
            buffer.modified = true;
        }
        buffer.cursorLine = 0;
        buffer.cursorColumn = 0;
        return buffer;
    }

    public static EditorBuffer untitled(String title, Language language) {
        EditorBuffer buffer = new EditorBuffer();
        buffer.path = null;
        buffer.language = language;
        buffer.lines.clear();
        buffer.lines.add("");
        return buffer;
    }

    public List<String> getLines() {
        return lines;
    }

    public int lineCount() {
        return lines.size();
    }

    public String getLine(int index) {
        return lines.get(index);
    }

    public int cursorLine() {
        return cursorLine;
    }

    public int cursorColumn() {
        return cursorColumn;
    }

    public void setCursor(int line, int column) {
        cursorLine = Math.max(0, Math.min(line, lines.size() - 1));
        int maxCol = lines.get(cursorLine).length();
        cursorColumn = Math.max(0, Math.min(column, maxCol));
        preferredColumn = cursorColumn;
    }

    public void moveCursor(int deltaLine, int deltaColumn) {
        if (deltaLine != 0) {
            cursorLine = Math.max(0, Math.min(cursorLine + deltaLine, lines.size() - 1));
            int maxCol = lines.get(cursorLine).length();
            cursorColumn = Math.min(preferredColumn, maxCol);
        } else {
            int maxCol = lines.get(cursorLine).length();
            cursorColumn = Math.max(0, Math.min(cursorColumn + deltaColumn, maxCol));
            preferredColumn = cursorColumn;
        }
    }

    public void moveToLineStart() {
        cursorColumn = 0;
        preferredColumn = 0;
    }

    public void moveToLineEnd() {
        cursorColumn = lines.get(cursorLine).length();
        preferredColumn = cursorColumn;
    }

    public void moveToDocumentStart() {
        cursorLine = 0;
        moveToLineStart();
    }

    public void moveToDocumentEnd() {
        cursorLine = lines.size() - 1;
        moveToLineEnd();
    }

    public void insertChar(char c) {
        String line = lines.get(cursorLine);
        if (c == '\n') {
            String before = line.substring(0, cursorColumn);
            String after = line.substring(cursorColumn);
            lines.set(cursorLine, before);
            lines.add(cursorLine + 1, after);
            cursorLine++;
            cursorColumn = 0;
            preferredColumn = 0;
        } else if (c == '\t') {
            insertText("    ");
        } else {
            String updated = line.substring(0, cursorColumn) + c + line.substring(cursorColumn);
            lines.set(cursorLine, updated);
            cursorColumn++;
            preferredColumn = cursorColumn;
        }
        modified = true;
    }

    public void insertText(String text) {
        for (int i = 0; i < text.length(); i++) {
            insertChar(text.charAt(i));
        }
    }

    public void backspace() {
        if (cursorColumn > 0) {
            String line = lines.get(cursorLine);
            String updated = line.substring(0, cursorColumn - 1) + line.substring(cursorColumn);
            lines.set(cursorLine, updated);
            cursorColumn--;
            preferredColumn = cursorColumn;
            modified = true;
        } else if (cursorLine > 0) {
            String current = lines.remove(cursorLine);
            cursorLine--;
            String prev = lines.get(cursorLine);
            cursorColumn = prev.length();
            preferredColumn = cursorColumn;
            lines.set(cursorLine, prev + current);
            modified = true;
        }
    }

    public void delete() {
        String line = lines.get(cursorLine);
        if (cursorColumn < line.length()) {
            String updated = line.substring(0, cursorColumn) + line.substring(cursorColumn + 1);
            lines.set(cursorLine, updated);
            modified = true;
        } else if (cursorLine < lines.size() - 1) {
            String next = lines.remove(cursorLine + 1);
            lines.set(cursorLine, line + next);
            modified = true;
        }
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
        this.language = Language.fromPath(path);
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String displayName() {
        if (path != null) {
            return path.getFileName().toString();
        }
        return "UNTITLED";
    }

    public void save() throws Exception {
        if (path == null) {
            throw new IllegalStateException("No file path set");
        }
        String content = String.join("\n", lines);
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(path, content, StandardCharsets.UTF_8);
        modified = false;
    }

    public boolean isInBlockComment() {
        return inBlockComment;
    }

    public void setInBlockComment(boolean inBlockComment) {
        this.inBlockComment = inBlockComment;
    }

    public void updateBlockCommentState() {
        boolean inBlock = false;
        String blockStart = "/*";
        String blockEnd = "*/";
        for (int i = 0; i <= cursorLine && i < lines.size(); i++) {
            String line = lines.get(i);
            if (inBlock) {
                int end = line.indexOf(blockEnd);
                if (end >= 0) {
                    inBlock = false;
                }
            } else {
                int start = 0;
                while (true) {
                    int block = line.indexOf(blockStart, start);
                    if (block < 0) break;
                    int end = line.indexOf(blockEnd, block + blockStart.length());
                    if (end < 0) {
                        inBlock = true;
                        break;
                    }
                    start = end + blockEnd.length();
                }
            }
        }
        inBlockComment = inBlock;
    }

    public void moveWordForward(boolean bigWord) {
        if (lines.isEmpty()) return;
        int line = cursorLine;
        int col = cursorColumn;
        while (line < lines.size()) {
            String text = lines.get(line);
            col = advanceWordForward(text, col, bigWord);
            if (col <= text.length()) {
                setCursor(line, Math.min(col, text.length()));
                return;
            }
            line++;
            col = 0;
        }
        moveToDocumentEnd();
    }

    public void moveWordBackward(boolean bigWord) {
        if (lines.isEmpty()) return;
        int line = cursorLine;
        int col = cursorColumn;
        while (line >= 0) {
            String text = lines.get(line);
            int nextCol = retreatWordBackward(text, col, bigWord);
            if (nextCol >= 0) {
                setCursor(line, nextCol);
                return;
            }
            line--;
            col = line >= 0 ? lines.get(line).length() : 0;
        }
        moveToDocumentStart();
    }

    public void moveToLine(int line) {
        setCursor(line, cursorColumn);
    }

    public int findCharForward(char ch, int count) {
        if (count < 1) count = 1;
        int line = cursorLine;
        int col = cursorColumn + 1;
        while (line < lines.size()) {
            String text = lines.get(line);
            while (col < text.length()) {
                if (text.charAt(col) == ch && --count == 0) {
                    setCursor(line, col);
                    return col;
                }
                col++;
            }
            line++;
            col = 0;
        }
        return -1;
    }

    private static int advanceWordForward(String text, int col, boolean bigWord) {
        int i = col;
        if (i < text.length() && isWordChar(text.charAt(i), bigWord)) {
            while (i < text.length() && isWordChar(text.charAt(i), bigWord)) i++;
        } else if (i < text.length() && !Character.isWhitespace(text.charAt(i))) {
            while (i < text.length() && !Character.isWhitespace(text.charAt(i))) i++;
        }
        while (i < text.length() && Character.isWhitespace(text.charAt(i))) i++;
        return i;
    }

    private static int retreatWordBackward(String text, int col, boolean bigWord) {
        int i = col;
        if (i > 0 && Character.isWhitespace(text.charAt(i - 1))) {
            while (i > 0 && Character.isWhitespace(text.charAt(i - 1))) i--;
        }
        if (i > 0 && isWordChar(text.charAt(i - 1), bigWord)) {
            while (i > 0 && isWordChar(text.charAt(i - 1), bigWord)) i--;
            return i;
        }
        if (i > 0 && !Character.isWhitespace(text.charAt(i - 1))) {
            while (i > 0 && !Character.isWhitespace(text.charAt(i - 1))) i--;
            return i;
        }
        return col > 0 ? 0 : -1;
    }

    private static boolean isWordChar(char ch, boolean bigWord) {
        return bigWord ? !Character.isWhitespace(ch) : Character.isLetterOrDigit(ch) || ch == '_';
    }
}
