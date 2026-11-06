package com.tmci;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class ScannerTest {

    @Test
    void scansEveryKeyword() {
        List<Token> tokens = new Scanner("HALT IF GOTO LEFT RIGHT WRITE").scanTokens();

        assertToken(tokens.get(0), TokenType.HALT, "HALT", "HALT", 1);
        assertToken(tokens.get(1), TokenType.IF, "IF", "IF", 1);
        assertToken(tokens.get(2), TokenType.GOTO, "GOTO", "GOTO", 1);
        assertToken(tokens.get(3), TokenType.LEFT, "LEFT", "LEFT", 1);
        assertToken(tokens.get(4), TokenType.RIGHT, "RIGHT", "RIGHT", 1);
        assertToken(tokens.get(5), TokenType.WRITE, "WRITE", "WRITE", 1);
        assertToken(tokens.get(6), TokenType.EOF, "", null, 1);
    }

    @Test
    void scansIdentifiersAndSingleCharacterSymbols() {
        List<Token> tokens = new Scanner("start: A 7 z").scanTokens();

        assertToken(tokens.get(0), TokenType.IDENTIFIER, "start", "start", 1);
        assertToken(tokens.get(1), TokenType.SYMBOL, "A", "A", 1);
        assertToken(tokens.get(2), TokenType.SYMBOL, "7", "7", 1);
        assertToken(tokens.get(3), TokenType.SYMBOL, "z", "z", 1);
        assertToken(tokens.get(4), TokenType.EOF, "", null, 1);
    }

    @Test
    void distinguishesIdentifierFromLabel() {
        List<Token> tokens = new Scanner("state: next_state").scanTokens();

        assertToken(tokens.get(0), TokenType.IDENTIFIER, "state", "state", 1);
        assertToken(tokens.get(1), TokenType.LABEL, "next_state", "next_state", 1);
        assertToken(tokens.get(2), TokenType.EOF, "", null, 1);
    }

    @Test
    void scansSingleCharacterIdentifier() {
        List<Token> tokens = new Scanner("a:").scanTokens();

        assertToken(tokens.get(0), TokenType.IDENTIFIER, "a", "a", 1);
        assertToken(tokens.get(1), TokenType.EOF, "", null, 1);
    }

    @Test
    void scansNonWhitespaceSymbols() {
        List<Token> tokens = new Scanner("! ? .").scanTokens();

        assertToken(tokens.get(0), TokenType.SYMBOL, "!", "!", 1);
        assertToken(tokens.get(1), TokenType.SYMBOL, "?", "?", 1);
        assertToken(tokens.get(2), TokenType.SYMBOL, ".", ".", 1);
        assertToken(tokens.get(3), TokenType.EOF, "", null, 1);
    }

    @Test
    void scansCompleteTuringMachineProgram() {
        String source = """
                start_state: IF 0 WRITE 1
                IF _ LEFT
                IF 1 RIGHT
                GOTO next_state
                next_state: HALT
                """;

        List<Token> tokens = new Scanner(source).scanTokens();

        assertToken(tokens.get(0), TokenType.IDENTIFIER, "start_state", "start_state", 1);
        assertToken(tokens.get(1), TokenType.IF, "IF", "IF", 1);
        assertToken(tokens.get(2), TokenType.SYMBOL, "0", "0", 1);
        assertToken(tokens.get(3), TokenType.WRITE, "WRITE", "WRITE", 1);
        assertToken(tokens.get(4), TokenType.SYMBOL, "1", "1", 1);
        assertToken(tokens.get(5), TokenType.NEWLINE, "\n", null, 2);
        assertToken(tokens.get(6), TokenType.IF, "IF", "IF", 2);
        assertToken(tokens.get(7), TokenType.SYMBOL, "_", "_", 2);
        assertToken(tokens.get(8), TokenType.LEFT, "LEFT", "LEFT", 2);
        assertToken(tokens.get(9), TokenType.NEWLINE, "\n", null, 3);
        assertToken(tokens.get(10), TokenType.IF, "IF", "IF", 3);
        assertToken(tokens.get(11), TokenType.SYMBOL, "1", "1", 3);
        assertToken(tokens.get(12), TokenType.RIGHT, "RIGHT", "RIGHT", 3);
        assertToken(tokens.get(13), TokenType.NEWLINE, "\n", null, 4);
        assertToken(tokens.get(14), TokenType.GOTO, "GOTO", "GOTO", 4);
        assertToken(tokens.get(15), TokenType.LABEL, "next_state", "next_state", 4);
        assertToken(tokens.get(16), TokenType.NEWLINE, "\n", null, 5);
        assertToken(tokens.get(17), TokenType.IDENTIFIER, "next_state", "next_state", 5);
        assertToken(tokens.get(18), TokenType.HALT, "HALT", "HALT", 5);
        assertToken(tokens.get(19), TokenType.NEWLINE, "\n", null, 6);
        assertToken(tokens.get(20), TokenType.EOF, "", null, 6);
    }

    @Test
    void rejectsIdentifiersStartingWithUnderscore() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> new Scanner("_state:").scanTokens());

        assertEquals(
                "_state is an invalid label. Must start with an alphabet with no special characters (at line: 1 )",
                exception.getMessage());
    }

    @Test
    void displaysSymbolsOnly() {
        List<Token> tokens = new Scanner("x 7 _").scanTokens();

        assertToken(tokens.get(0), TokenType.SYMBOL, "x", "x", 1);
        assertToken(tokens.get(1), TokenType.SYMBOL, "7", "7", 1);
        assertToken(tokens.get(2), TokenType.SYMBOL, "_", "_", 1);
        assertToken(tokens.get(3), TokenType.EOF, "", null, 1);
    }

    @Test
    void scansIdentifiersOnly() {
        List<Token> tokens = new Scanner("label: state_name:").scanTokens();

        assertToken(tokens.get(0), TokenType.IDENTIFIER, "label", "label", 1);
        assertToken(tokens.get(1), TokenType.IDENTIFIER, "state_name", "state_name", 1);
        assertToken(tokens.get(2), TokenType.EOF, "", null, 1);
    }

    @Test
    void displaysAllTokenTypesInInstruction() {
        List<Token> tokens = new Scanner("IF x label: RIGHT LEFT HALT WRITE x").scanTokens();

        assertToken(tokens.get(0), TokenType.IF, "IF", "IF", 1);
        assertToken(tokens.get(1), TokenType.SYMBOL, "x", "x", 1);
        assertToken(tokens.get(2), TokenType.IDENTIFIER, "label", "label", 1);
        assertToken(tokens.get(3), TokenType.RIGHT, "RIGHT", "RIGHT", 1);
        assertToken(tokens.get(4), TokenType.LEFT, "LEFT", "LEFT", 1);
        assertToken(tokens.get(5), TokenType.HALT, "HALT", "HALT", 1);
        assertToken(tokens.get(6), TokenType.WRITE, "WRITE", "WRITE", 1);
        assertToken(tokens.get(7), TokenType.SYMBOL, "x", "x", 1);
        assertToken(tokens.get(8), TokenType.EOF, "", null, 1);
    }

    @Test
    void ignoresWhitespaceAndTracksSourceLines() {
        List<Token> tokens = new Scanner("start:\r\nWRITE\n7").scanTokens();

        assertToken(tokens.get(0), TokenType.IDENTIFIER, "start", "start", 1);
        assertToken(tokens.get(1), TokenType.NEWLINE, "\n", null, 2);
        assertToken(tokens.get(2), TokenType.WRITE, "WRITE", "WRITE", 2);
        assertToken(tokens.get(3), TokenType.NEWLINE, "\n", null, 3);
        assertToken(tokens.get(4), TokenType.SYMBOL, "7", "7", 3);
        assertToken(tokens.get(5), TokenType.EOF, "", null, 3);
    }

    @Test
    void scansEmptySourceAsEof() {
        List<Token> tokens = new Scanner("").scanTokens();

        assertEquals(1, tokens.size());
        assertToken(tokens.get(0), TokenType.EOF, "", null, 1);
    }

    @Test
    void rejectsUnsupportedCommentMarker() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> new Scanner("#").scanTokens());

        assertEquals("Unexpected character '#' at line 1", exception.getMessage());
    }

    @Test
    void acceptsPunctuationAsSingleCharacterSymbols() {
        List<Token> tokens = new Scanner("@ $").scanTokens();

        assertToken(tokens.get(0), TokenType.SYMBOL, "@", "@", 1);
        assertToken(tokens.get(1), TokenType.SYMBOL, "$", "$", 1);
        assertToken(tokens.get(2), TokenType.EOF, "", null, 1);
    }

    @Test
    void rejectsUnclosedQuotedText() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> new Scanner("\"hello").scanTokens());

        assertEquals("Unexpected character '\"' at line 1", exception.getMessage());
    }

    @Test
    void rejectsMalformedNumericToken() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> new Scanner("12").scanTokens());

        assertEquals(
                "12 is an invalid label. Must start with an alphabet with no special characters (at line: 1 )",
                exception.getMessage());
    }

    @Test
    void scansLabelsWithoutTrailingColon() {
        List<Token> tokens = new Scanner("label state_name").scanTokens();

        assertToken(tokens.get(0), TokenType.LABEL, "label", "label", 1);
        assertToken(tokens.get(1), TokenType.LABEL, "state_name", "state_name", 1);
        assertToken(tokens.get(2), TokenType.EOF, "", null, 1);
    }

    @Test
    void rejectsReservedKeywordsAsLabels() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> new Scanner("HALT:").scanTokens());

        assertEquals("HALT is a reserved keyword (at line: 1)", exception.getMessage());
    }

    @Test
    void tracksLinesForTokensAfterNewlines() {
        List<Token> tokens = new Scanner("first:\nWRITE\n7").scanTokens();

        assertToken(tokens.get(0), TokenType.IDENTIFIER, "first", "first", 1);
        assertToken(tokens.get(1), TokenType.NEWLINE, "\n", null, 2);
        assertToken(tokens.get(2), TokenType.WRITE, "WRITE", "WRITE", 2);
        assertToken(tokens.get(3), TokenType.NEWLINE, "\n", null, 3);
        assertToken(tokens.get(4), TokenType.SYMBOL, "7", "7", 3);
        assertToken(tokens.get(5), TokenType.EOF, "", null, 3);
    }

    private static void assertToken(
            Token token,
            TokenType expectedType,
            String expectedLexeme,
            Object expectedLiteral,
            int expectedLine) {
        assertEquals(expectedType, token.getType());
        assertEquals(expectedLexeme, token.getLexeme());
        assertEquals(expectedLiteral, token.getLiteral());
        assertEquals(expectedLine, token.getLine());
    }
}
