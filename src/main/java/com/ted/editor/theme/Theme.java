package com.ted.editor.theme;

import com.googlecode.lanterna.TextColor;

/**
 * Color palette for TED chrome, editor surface, and syntax highlighting.
 */
public record Theme(
        String id,
        String displayName,
        TextColor bg,
        TextColor fg,
        TextColor chromeBg,
        TextColor chromeFg,
        TextColor chromeHotkey,
        TextColor tabActiveBg,
        TextColor tabActiveFg,
        TextColor tabInactiveBg,
        TextColor tabInactiveFg,
        TextColor gutterBg,
        TextColor gutterFg,
        TextColor syntaxDefault,
        TextColor syntaxKeyword,
        TextColor syntaxString,
        TextColor syntaxChar,
        TextColor syntaxComment,
        TextColor syntaxNumber,
        TextColor syntaxPreprocessor,
        TextColor syntaxType,
        TextColor syntaxOperator,
        TextColor syntaxTag,
        TextColor syntaxAttribute,
        TextColor syntaxBuiltin,
        TextColor cursorFg,
        TextColor cursorBg
) {}
