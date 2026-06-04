# TED — Turbo Editor

A terminal text editor written in Java, styled after **Borland Turbo C++** and **MS-DOS EDIT**. Navy-blue editing surface, silver menu bars, line numbers, syntax highlighting, and multi-tab file editing — all in your terminal.

```
┌──────────────────────────────────────────────────────────────┐
│ File  Edit  Search  Run  Compile  Window  Help               │  ← silver menu bar
├──────────────────────────────────────────────────────────────┤
│ ▶ main.java *   utils.rs                                     │  ← tab bar
│┌────────────────────────────────────────────────────────────┐│
││   1  public class Main {                                   ││
││   2      public static void main(String[] args) {          ││  ← navy editor
││   3          System.out.println("Hello, world!");          ││
││   4      }                                                 ││
││   5  }                                                     ││
│└────────────────────────────────────────────────────────────┘│
│ Ln 3, Col 12  |  Java  |  MODIFIED   F1=Help  F2=Save  ...  │  ← status bar
└──────────────────────────────────────────────────────────────┘
```

## Features

- **Retro UI** — DOS silver chrome (`#C0C0C0`) for the top menu and bottom status bar; deep navy editor background (`#000080`); Borland-style red hotkey letters
- **Syntax highlighting** — keywords, strings, comments, numbers, types, preprocessor directives, and markup tags
- **Multi-tab editing** — open several files at once; modified tabs show a `*` suffix
- **20+ languages** — detected automatically from file extension
- **Line numbers** — fixed gutter on the left
- **Full-screen terminal UI** — built with [Lanterna](https://github.com/mabe02/lanterna)

## Requirements

- **Java 17** or later
- **Maven 3.6+**
- A real terminal emulator (iTerm2, Terminal.app, Windows Terminal, Kitty, etc.)

## Quick start

```bash
git clone <repo-url>
cd ted

# Build a standalone JAR (includes all dependencies)
mvn package

# Run the editor
java -jar target/ted-1.0.0.jar

# Open files on launch
java -jar target/ted-1.0.0.jar src/main/java/com/ted/editor/Ted.java README.md
```

You can also run without packaging:

```bash
mvn compile exec:java -Dexec.mainClass="com.ted.editor.Ted"
```

## Keybindings

### Function keys

| Key | Action |
|-----|--------|
| `F1` | Help |
| `F2` | Save |
| `F3` | Open file |
| `F4` | New tab |
| `F6` | Next tab |
| `F10` | Exit |

### Keyboard shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+S` | Save |
| `Ctrl+O` | Open file |
| `Ctrl+N` | New tab |
| `Ctrl+W` | Close tab |
| `Ctrl+Q` | Exit |
| `Ctrl+Tab` | Next tab |
| `Shift+Ctrl+Tab` | Previous tab |
| `Alt+1` … `Alt+9` | Jump to tab 1–9 |
| `Ctrl+Insert` | Copy current line to clipboard |
| `Shift+Insert` | Paste from clipboard |

### Editing

| Key | Action |
|-----|--------|
| Arrow keys | Move cursor |
| `Home` / `End` | Start / end of line |
| `Ctrl+Home` / `Ctrl+End` | Start / end of file |
| `Page Up` / `Page Down` | Scroll one page |
| `Tab` | Insert 4 spaces |
| `Shift+Tab` | Unindent (remove up to 4 spaces) |
| `Backspace` / `Delete` | Delete character or join lines |

## Supported languages

Language mode is chosen from the file extension:

| Language | Extensions |
|----------|------------|
| C | `.c`, `.h` |
| C++ | `.cpp`, `.cc`, `.cxx`, `.hpp`, `.hh`, `.hxx` |
| Java | `.java` |
| JavaScript | `.js`, `.mjs`, `.cjs` |
| TypeScript | `.ts`, `.tsx` |
| Python | `.py`, `.pyw` |
| Rust | `.rs` |
| Go | `.go` |
| HTML | `.html`, `.htm` |
| CSS | `.css`, `.scss` |
| JSON | `.json` |
| XML | `.xml`, `.svg` |
| Shell | `.sh`, `.bash`, `.zsh` |
| Markdown | `.md`, `.markdown` |
| SQL | `.sql` |
| YAML | `.yml`, `.yaml` |
| TOML | `.toml` |
| Properties | `.properties`, `.ini`, `.cfg` |
| Makefile | `Makefile`, `makefile.*` |

Files without a recognized extension open in plain-text mode.

## Project structure

```
src/main/java/com/ted/editor/
├── Ted.java                 Entry point
├── TedApplication.java      Application shell, keybindings, file I/O
├── TurboTheme.java          Color palette and chrome constants
├── Language.java            Language detection by extension
├── model/
│   ├── EditorBuffer.java    Text buffer, cursor, save/load
│   ├── EditorTab.java       Tab wrapper
│   └── TabManager.java      Tab collection and switching
├── syntax/
│   ├── SyntaxHighlighter.java
│   ├── Token.java
│   └── TokenType.java
└── ui/
    ├── ChromePanel.java     Menu bar, tab bar, status bar
    ├── EditorPanel.java     Editor rendering and input
    └── FileDialogs.java     Open / save / confirm dialogs
```

## How it works

TED runs as a full-screen Lanterna TUI. The layout is:

1. **Top menu bar** — gray chrome with `File`, `Edit`, `Search`, etc.; hotkey letters in red
2. **Tab bar** — open files on the navy background; active tab marked with `▶`
3. **Editor pane** — bordered text area with line-number gutter and syntax-colored tokens
4. **Status bar** — gray chrome showing cursor position, language, and modified state on the left; function-key hints on the right

Syntax highlighting uses a lightweight regex/lexer per language — no external parser dependencies. Each visible line is tokenized and drawn with colors from `TurboTheme`.

## Development

```bash
# Compile
mvn compile

# Run tests (none yet)
mvn test

# Build fat JAR
mvn package
```

To tweak the look, start with `TurboTheme.java`. Menu labels and status hints are defined there as constants.

## Acknowledgments

Inspired by the editors that shipped with **Borland Turbo C++** and **MS-DOS EDIT**. Built with [Lanterna](https://github.com/mabe02/lanterna) for terminal rendering.
