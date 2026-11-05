package com.tmci;

public enum TokenType {
    LABEL, SYMBOL,

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
