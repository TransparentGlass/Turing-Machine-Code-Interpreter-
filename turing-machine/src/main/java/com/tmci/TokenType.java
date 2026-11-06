package com.tmci;

public enum TokenType {
    LABEL, SYMBOL,

    //Condition
    IF, GOTO,

    // movement
    LEFT, RIGHT,

    // Action
    WRITE, READ, HALT,

    //END OF FILE
    EOF
}
