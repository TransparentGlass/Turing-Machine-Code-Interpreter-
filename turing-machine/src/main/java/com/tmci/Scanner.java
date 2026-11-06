package com.tmci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.tmci.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("HALT", TokenType.HALT);
        keywords.put("IF", TokenType.IF);
        keywords.put("GOTO", TokenType.GOTO);
        keywords.put("LEFT", TokenType.LEFT);
        keywords.put("RIGHT", TokenType.RIGHT);
        keywords.put("READ", TokenType.READ);
        keywords.put("WRITE", TokenType.WRITE);
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private char advance() {
        return source.charAt(current++);
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    // Overloading addToken
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;

            case ':':
                addToken(TokenType.COLON);
                break;

            // case '_':
            //     if (isAlphaNumeric(peek())) {
            //         scanLabelOrKeyword();
            //     } else {
            //         scanSymbol();
            //     }
            //     break;

            default:
                if (isDigit(c) && isSymbol()) {
                    scanSymbol();   
                }
                else if (isAlphaNumeric(c)) {
                    scanLabelOrKeyword();
                } else {
                    throw new RuntimeException("Unexpected character '" + c + "' at line " + (line));
                }
                break;

        }

    }

    private void scanLabelOrKeyword() {
        // Keep advancing 'current' while alphanumeric until it hits a colon
        while (isAlphaNumeric(peek())) {
            advance();
        }

        // 1-Step Extraction using String.substring()
        String text = source.substring(start, current);

        // Check if extracted substring is a reserved keyword
        TokenType type = keywords.get(text);
        if (type == null) {
            type = text.length() == 1 ? TokenType.SYMBOL : TokenType.LABEL;
        }

        addToken(type, text);
    }

    private void scanSymbol() {
        // Single character confirmed (followed by space, newline, or EOF)
        String text = source.substring(start, current);
        addToken(TokenType.SYMBOL, text);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') || (c == '_');
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean isSymbol(){
        //if the next char is a space, return true
        return !isAlphaNumeric(peek());
    }
}
