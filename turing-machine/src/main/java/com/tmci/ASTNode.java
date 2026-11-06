package com.tmci;

import java.util.List;

public abstract class ASTNode {}

class ProgramNode extends ASTNode {
    final List<ASTNode> statements;
    ProgramNode(List<ASTNode> statements) { this.statements = statements; }
}

class LabelDeclNode extends ASTNode {
    final String label;
    final ASTNode expression;
    LabelDeclNode(String label, ASTNode expression) {
        this.label = label;
        this.expression = expression;
    }

    
}

class ConditionalNode extends ASTNode {
    final String symbol; // SYMBOL
    final CommandNode command; // command
    ConditionalNode(String symbol, CommandNode cmd) {
        this.symbol = symbol;
        this.command = cmd;
    }
}

class GotoNode extends ASTNode {
    final String targetLabel;
    GotoNode(String targetLabel) { this.targetLabel = targetLabel; }
}

class CommandNode extends ASTNode {
    final String commandType; // "WRITE", "LEFT", "RIGHT", "HALT", "GOTO LABEL"
    final String symbol;      // null if not WRITE
    final String targetLabel; // null if not GOTO
    CommandNode(String commandType, String symbol, String targetlabel) {
        this.commandType = commandType;
        this.symbol = symbol;
        this.targetLabel = targetlabel;
    }
}
