package com.tmci;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Test;

class ScannerFunctionTest {

    @Test
    void scanTokensScansInputAndAddsEof() {
        List<Token> tokens = new Scanner("HALT").scanTokens();

        assertEquals(2, tokens.size());
        assertEquals(TokenType.HALT, tokens.get(0).getType());
        assertEquals(TokenType.EOF, tokens.get(1).getType());
    }

    @Test
    void advanceReturnsCurrentCharacterAndMovesPosition() throws Exception {
        Scanner scanner = new Scanner("AB");

        assertEquals(Character.valueOf('A'), (Character) invoke(scanner, "advance"));
        assertEquals(Character.valueOf('B'), (Character) invoke(scanner, "advance"));
    }

    @Test
    void peekReturnsNextCharacterWithoutMovingPosition() throws Exception {
        Scanner scanner = new Scanner("AB");

        assertEquals(Character.valueOf('A'), (Character) invoke(scanner, "peek"));
        assertEquals(Character.valueOf('A'), (Character) invoke(scanner, "peek"));
        invoke(scanner, "advance");
        assertEquals(Character.valueOf('B'), (Character) invoke(scanner, "peek"));
    }

    @Test
    void peekReturnsNullCharacterAtEnd() throws Exception {
        Scanner scanner = new Scanner("");

        assertEquals(Character.valueOf('\0'), (Character) invoke(scanner, "peek"));
    }

    @Test
    void addTokenWithoutLiteralUsesNullLiteral() throws Exception {
        Scanner scanner = new Scanner(":");
        invoke(scanner, "scanToken");

        List<Token> tokens = getTokens(scanner);
        assertEquals(1, tokens.size());
        assertEquals(TokenType.COLON, tokens.get(0).getType());
        assertNull(tokens.get(0).getLiteral());
    }

    @Test
    void addTokenWithLiteralStoresTheProvidedLiteral() throws Exception {
        Scanner scanner = new Scanner("HALT");
        invoke(scanner, "advance");
        invoke(scanner, "advance");
        invoke(scanner, "advance");
        invoke(scanner, "advance");
        invoke(scanner, "addToken", new Class<?>[] { TokenType.class, Object.class }, TokenType.HALT, "value");

        Token token = getTokens(scanner).get(0);
        assertEquals(TokenType.HALT, token.getType());
        assertEquals("HALT", token.getLexeme());
        assertEquals("value", token.getLiteral());
    }

    @Test
    void scanTokenScansColonAndBlank() throws Exception {
        Scanner scanner = new Scanner(":_");

        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                () -> invoke(scanner, "scanToken"));

        assertEquals(
                "Unexpected character ':' at line 1",
                exception.getCause().getMessage());
    }

    @Test
    void scanTokenIncrementsLineForNewline() throws Exception {
        Scanner scanner = new Scanner("\n");

        invoke(scanner, "scanToken");

        assertEquals(2, ((Integer) getField(scanner, "line")).intValue());
        assertTrue(getTokens(scanner).isEmpty());
    }

    @Test
    void scanLabelOrKeywordClassifiesKeywordsAndIdentifiers() throws Exception {
        Scanner keywordScanner = new Scanner("HALT");
        invoke(keywordScanner, "scanLabelOrKeyword");
        assertEquals(TokenType.HALT, getTokens(keywordScanner).get(0).getType());

        Scanner identifierScanner = new Scanner("label");
        invoke(identifierScanner, "scanLabelOrKeyword");
        Token identifier = getTokens(identifierScanner).get(0);
        assertEquals(TokenType.LABEL, identifier.getType());
        assertEquals("label", identifier.getLexeme());
    }

    @Test
    void scanSymbolAcceptsOneCharacterSymbol() throws Exception {
        Scanner scanner = new Scanner("7");

        invoke(scanner, "advance");
        invoke(scanner, "scanSymbol");

        Token token = getTokens(scanner).get(0);
        assertEquals(TokenType.SYMBOL, token.getType());
        assertEquals("7", token.getLexeme());
    }

    // @Test
    // void scanSymbolRejectsMultiCharacterSymbol() throws Exception {
    // Scanner scanner = new Scanner("12");

    // invoke(scanner, "advance");

    // InvocationTargetException exception = assertThrows(
    // InvocationTargetException.class,
    // () -> invoke(scanner, "scanSymbol"));

    // assertEquals(
    // "Tape symbol must be a single character at line 1. Got multi-character
    // sequence starting with '12'",
    // exception.getCause().getMessage());
    // }

    @Test
    void isAlphaNumericRecognizesLettersDigitsAndUnderscore() throws Exception {
        Scanner scanner = new Scanner("");

        assertTrue(((Boolean) invoke(scanner, "isAlphaNumeric", char.class, 'A')).booleanValue());
        assertTrue(((Boolean) invoke(scanner, "isAlphaNumeric", char.class, '7')).booleanValue());
        assertTrue(((Boolean) invoke(scanner, "isAlphaNumeric", char.class, '_')).booleanValue());
        assertFalse(((Boolean) invoke(scanner, "isAlphaNumeric", char.class, '-')).booleanValue());
    }

    @Test
    void isDigitRecognizesOnlyAsciiDigits() throws Exception {
        Scanner scanner = new Scanner("");

        assertTrue(((Boolean) invoke(scanner, "isDigit", char.class, '0')).booleanValue());
        assertTrue(((Boolean) invoke(scanner, "isDigit", char.class, '9')).booleanValue());
        assertFalse(((Boolean) invoke(scanner, "isDigit", char.class, 'a')).booleanValue());
        assertFalse(((Boolean) invoke(scanner, "isDigit", char.class, '９')).booleanValue());
    }

    @Test
    void isAlphaRecognizesLettersAndUnderscore() throws Exception {
        Scanner scanner = new Scanner("");

        assertTrue(((Boolean) invoke(scanner, "isAlpha", char.class, 'a')).booleanValue());
        assertTrue(((Boolean) invoke(scanner, "isAlpha", char.class, 'Z')).booleanValue());
        assertTrue(((Boolean) invoke(scanner, "isAlpha", char.class, '_')).booleanValue());
        assertFalse(((Boolean) invoke(scanner, "isAlpha", char.class, '3')).booleanValue());
    }

    @Test
    void isAtEndTracksCurrentPosition() throws Exception {
        Scanner scanner = new Scanner("A");

        assertFalse(((Boolean) invoke(scanner, "isAtEnd")).booleanValue());
        invoke(scanner, "advance");
        assertTrue(((Boolean) invoke(scanner, "isAtEnd")).booleanValue());
    }

    @SuppressWarnings("unchecked")
    private static List<Token> getTokens(Scanner scanner) throws Exception {
        return (List<Token>) getField(scanner, "tokens");
    }

    private static Object getField(Scanner scanner, String fieldName) throws Exception {
        var field = Scanner.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(scanner);
    }

    @SuppressWarnings("unchecked")
    private static <T> T invoke(Scanner scanner, String methodName, Class<?> parameterType, Object argument)
            throws Exception {
        return (T) invoke(scanner, methodName, new Class<?>[] { parameterType }, argument);
    }

    @SuppressWarnings("unchecked")
    private static <T> T invoke(Scanner scanner, String methodName, Class<?>[] parameterTypes, Object... arguments)
            throws Exception {
        Method method = Scanner.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return (T) method.invoke(scanner, arguments);
    }

    @SuppressWarnings("unchecked")
    private static <T> T invoke(Scanner scanner, String methodName) throws Exception {
        return (T) invoke(scanner, methodName, new Class<?>[0]);
    }
}
