package com.tmci;

public enum TokenType {
    LABEL, SYMBOL, IDENTIFIER, NEWLINE,

    //Condition
    IF,

    // movement
    LEFT, RIGHT,

    // Action
    WRITE, HALT, GOTO,

    //END OF FILE
    EOF
}
