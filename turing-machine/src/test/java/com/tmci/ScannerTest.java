package com.tmci;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class ScannerTest {

    @Test
    void scansKeywordsAndPunctuation() {
        List<Token> tokens = new Scanner("HALT IF GOTO LEFT RIGHT READ WRITE _").scanTokens();

        assertToken(tokens.get(0), TokenType.HALT, "HALT", "HALT", 1);
        assertToken(tokens.get(1), TokenType.IF, "IF", "IF", 1);
        assertToken(tokens.get(2), TokenType.GOTO, "GOTO", "GOTO", 1);
        assertToken(tokens.get(3), TokenType.LEFT, "LEFT", "LEFT", 1);
        assertToken(tokens.get(4), TokenType.RIGHT, "RIGHT", "RIGHT", 1);
        assertToken(tokens.get(5), TokenType.READ, "READ", "READ", 1);
        assertToken(tokens.get(6), TokenType.WRITE, "WRITE", "WRITE", 1);
        assertToken(tokens.get(7), TokenType.SYMBOL, "_", "_", 1);
        assertToken(tokens.get(8), TokenType.EOF, "", null, 1);
    }

    @Test
    void scansLabelsAndSingleCharacterSymbols() {
        List<Token> tokens = new Scanner("start: A 7 z").scanTokens();

        assertToken(tokens.get(0), TokenType.LABEL, "start:", "start", 1);
        assertToken(tokens.get(1), TokenType.SYMBOL, "A", "A", 1);
        assertToken(tokens.get(2), TokenType.SYMBOL, "7", "7", 1);
        assertToken(tokens.get(3), TokenType.SYMBOL, "z", "z", 1);
        assertToken(tokens.get(4), TokenType.EOF, "", null, 1);
    }

    @Test
    void allowsLabelsToStartWithUnderscore() {
        List<Token> tokens = new Scanner("_state: state_name: _").scanTokens();

        assertToken(tokens.get(0), TokenType.LABEL, "_state:", "_state", 1);
        assertToken(tokens.get(1), TokenType.LABEL, "state_name:", "state_name", 1);
        assertToken(tokens.get(2), TokenType.SYMBOL, "_", "_", 1);
        assertToken(tokens.get(3), TokenType.EOF, "", null, 1);
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
    void displaysLabelsOnly() {
        List<Token> tokens = new Scanner("label: _state:").scanTokens();

        assertToken(tokens.get(0), TokenType.LABEL, "label:", "label", 1);
        assertToken(tokens.get(1), TokenType.LABEL, "_state:", "_state", 1);
        assertToken(tokens.get(2), TokenType.EOF, "", null, 1);
    }

    @Test
    void displaysAllTokenTypesInInstruction() {
        List<Token> tokens = new Scanner("IF x label: IF RIGHT LEFT HALT WRITE READ x").scanTokens();

        assertToken(tokens.get(0), TokenType.IF, "IF", "IF", 1);
        assertToken(tokens.get(1), TokenType.SYMBOL, "x", "x", 1);
        assertToken(tokens.get(2), TokenType.LABEL, "label:", "label", 1);
        assertToken(tokens.get(3), TokenType.IF, "IF", "IF", 1);
        assertToken(tokens.get(4), TokenType.RIGHT, "RIGHT", "RIGHT", 1);
        assertToken(tokens.get(5), TokenType.LEFT, "LEFT", "LEFT", 1);
        assertToken(tokens.get(6), TokenType.HALT, "HALT", "HALT", 1);
        assertToken(tokens.get(7), TokenType.WRITE, "WRITE", "WRITE", 1);
        assertToken(tokens.get(8), TokenType.READ, "READ", "READ", 1);
        assertToken(tokens.get(9), TokenType.SYMBOL, "x", "x", 1);
        assertToken(tokens.get(10), TokenType.EOF, "", null, 1);
    }

    @Test
    void ignoresWhitespaceAndTracksSourceLines() {
        List<Token> tokens = new Scanner("start:\r\nWRITE\n7").scanTokens();

        assertToken(tokens.get(0), TokenType.LABEL, "start:", "start", 1);
        assertToken(tokens.get(1), TokenType.WRITE, "WRITE", "WRITE", 2);
        assertToken(tokens.get(2), TokenType.SYMBOL, "7", "7", 3);
        assertToken(tokens.get(3), TokenType.EOF, "", null, 3);
    }

    @Test
    void scansEmptySourceAsEof() {
        List<Token> tokens = new Scanner("").scanTokens();

        assertEquals(1, tokens.size());
        assertToken(tokens.get(0), TokenType.EOF, "", null, 1);
    }

    @Test
    void rejectsUnexpectedCharacters() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> new Scanner("@").scanTokens());

        assertEquals("Unexpected character '@' at line 1", exception.getMessage());
    }

    @Test
    void rejectsLabelsWithoutTrailingColon() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> new Scanner("label").scanTokens());

        assertEquals("Label requires ':' to be defined at line: 1", exception.getMessage());
    }

    private static void assertToken(
            Token token,
            TokenType expectedType,
            String expectedLexeme,
            Object expectedLiteral,
            int expectedLine) {
        System.out.printf(
                "Testing token: type=%s, lexeme='%s', literal=%s, line=%d%n",
                token.getType(),
                token.getLexeme(),
                token.getLiteral(),
                token.getLine());
        assertEquals(expectedType, token.getType());
        assertEquals(expectedLexeme, token.getLexeme());
        assertEquals(expectedLiteral, token.getLiteral());
        assertEquals(expectedLine, token.getLine());
    }
}
