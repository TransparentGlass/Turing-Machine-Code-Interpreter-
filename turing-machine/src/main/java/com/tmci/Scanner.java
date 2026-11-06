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
                //For single characters and
                //Labels that start a number or a _
                if (isAlphaNumeric(c)) {
                    scanTypeToken();
                } else{
                    throw new RuntimeException("Unexpected character '" + c + "' at line " + (line));
                }
                break;

        }

    }

    private void scanTypeToken() {
        // Keep advancing 'current' while alphanumeric until it hits a colon or a space
        while (isAlphaNumeric(peek())) {
            advance();
        }

        // 1-Step Extraction using String.substring()
        String text = source.substring(start, current); // e.g., "start:"

        // Check if extracted substring is a reserved keyword
        TokenType type = keywords.get(text);
        
        //Situation 1: It is a single character
        if(text.length() == 1){
            scanSymbol();
            return;
        }
        //Situation 2: It is a label that has a name keyword
        if (type != null && text.length() > 1 && peek() == ':'){
            throw new RuntimeException(text + " is a reserved keyword at line: " + line);
        }
        // Situation 3: It isn't a keyword and it has a colon next to it
        if (type == null && peek() == ':') {
            addToken(TokenType.IDENTIFIER, text);
            advance(); // skip the colon
            return;  
        }
        // Situation 3: It isn't a label nor a keyword and its length is more than 1 
        if (text.length() > 1 && type == null) {
            addToken(TokenType.LABEL, text);
            return;
        }

        addToken(type, text);
    }

    private void scanSymbol() {
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
}
