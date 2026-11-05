package com.tmci;

public enum TokenType {
    IDENTIFIER, SYMBOL,

    COLON,

    //Condition
    IF, GOTO,

    // movement
    LEFT, RIGHT,

    // Action
    WRITE, READ, HALT,

    //END OF FILE
    EOF
}
