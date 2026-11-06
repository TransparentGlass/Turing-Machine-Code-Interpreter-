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

            default:
                if (isDigit(c) && isSymbol()) {
                    scanSymbol();
                } else if (isAlphaNumeric(c)) {
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
        String text = source.substring(start, current); // e.g., "start:"
        // Check if extracted substring is a reserved keyword
        TokenType type = keywords.get(text);
        if (peek() != ':') {

            // Situation 1: it isn't a label nor a keyword and its length is 1
            if (type == null && text.length() == 1) {
                type = TokenType.SYMBOL;

                // Situation 2: It isn't a label nor a keyword and its length is more than 1
            } else if (text.length() > 1 && type == null) {
                throw new RuntimeException("Label requires ':' to be defined at line: " + line);
            }

            addToken(type, text);
        } else {
            // Situation 3: It isn't a keyword and it has a colon next to it
            if (type == null) {
                advance(); // consume the ':'
                addToken(TokenType.LABEL, text);
                // Situation 4: It is a keyword
            } else {
                throw new RuntimeException(text + " is a reserved keyword at line: " + line);
            }

        }

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

    private boolean isSymbol() {
        // if the next char is a space, return true
        return !isAlphaNumeric(peek());
    }
}
