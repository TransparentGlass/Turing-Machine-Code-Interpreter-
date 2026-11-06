package com.tmci;

import java.util.List;

public abstract class ASTNode {
    private final int line;

    protected ASTNode(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}

class ProgramNode extends ASTNode {
    final List<ASTNode> statements;
    ProgramNode(List<ASTNode> statements, int line) {
        super(line);
        this.statements = statements;
    }
}

class LabelDeclNode extends ASTNode {
    final String label;
    final ASTNode expression;
    LabelDeclNode(String label, ASTNode expression, int line) {
        super(line);
        this.label = label;
        this.expression = expression;
    }
}

class ConditionalNode extends ASTNode {
    final String symbol; // SYMBOL
    final CommandNode command; // command
    ConditionalNode(String symbol, CommandNode cmd, int line) {
        super(line);
        this.symbol = symbol;
        this.command = cmd;
    }
}

class CommandNode extends ASTNode {
    final String commandType; // "WRITE", "LEFT", "RIGHT", "HALT", "GOTO LABEL"
    final String symbol;      // null if not WRITE
    final String targetLabel; // null if not GOTO
    CommandNode(String commandType, String symbol, String targetlabel, int line) {
        super(line);
        this.commandType = commandType;
        this.symbol = symbol;
        this.targetLabel = targetlabel;
    }
}
