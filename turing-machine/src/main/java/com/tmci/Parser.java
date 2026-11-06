package com.tmci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // program -> statements*
    public ASTNode parse_program() {
        List<ASTNode> statements = new ArrayList<>();

        if (isAtEnd()) {
            throw new RuntimeException("Program is empty");
        }

        while (!isAtEnd()) {
            statements.add(parse_statements());
        }

        // check for duplicate labels
        Map<String, Integer> labelTable = new HashMap<>();
        Set<String> declaredLabels = new HashSet<>();

        for (int i = 0; i < statements.size(); i++) {
            ASTNode stmt = statements.get(i);

            if (stmt instanceof LabelDeclNode labelNode) {
                String labelName = labelNode.label;

                // Check for Duplicate Label
                if (declaredLabels.contains(labelName)) {
                    throw new RuntimeException(
                            "Duplicate label declaration '" + labelName + "'" + " at line: " + labelNode.getLine());
                }

                declaredLabels.add(labelName);
                labelTable.put(labelName, i);
            }
        }

        // check for undefined labels

        for (ASTNode stmt : statements) {
            List<CommandNode> gotoNodes = getGotoNodes(stmt);

            for (CommandNode gotoNode : gotoNodes) {
                String targetLabel = gotoNode.targetLabel;

                if (!declaredLabels.contains(targetLabel)) {
                    throw new RuntimeException(
                            "Undefined label declaration '" + targetLabel + "' at line "
                                    + gotoNode.getLine());
                }
            }
        }

        return new ProgramNode(statements, statements.get(0).getLine());

    }

    // statements -> label_decl | expression
    private ASTNode parse_statements() {
        if (check(TokenType.IDENTIFIER)) {
            return parse_labelDecl();
        }

        return parse_expression();
    }

    // label_decl -> IDENTIFIER expression
    private ASTNode parse_labelDecl() {
        Token labelToken = consume(TokenType.IDENTIFIER, "Expected an identifier");
        ASTNode expr = parse_expression();
        return new LabelDeclNode(labelToken.getLexeme(), expr, labelToken.getLine());

    }

    // expression -> conditional | command
    private ASTNode parse_expression() {
        if (match(TokenType.IF)) {
            return parse_conditionals();
        }
        return parse_command();
    }

    // conditional -> IF SYMBOL command
    private ASTNode parse_conditionals() {
        Token symbolToken = consume(TokenType.SYMBOL, "Expected symbol after IF");
        CommandNode cmd = parse_command();
        return new ConditionalNode(symbolToken.getLexeme(), cmd, symbolToken.getLine());
    }

    // command -> WRITE SYMBOL| LEFT | RIGHT | HALT | GOTO LABEL
    private CommandNode parse_command() {
        if (match(TokenType.WRITE)) {
            int commandLine = previous().getLine();
            Token symbolToken = consume(TokenType.SYMBOL, "Expected symbol next to WRITE");
            String symbol = symbolToken.getLexeme();
            return new CommandNode("WRITE", symbol, null, commandLine);
        }
        if (match(TokenType.LEFT)) {
            return new CommandNode("LEFT", null, null, previous().getLine());
        }
        if (match(TokenType.RIGHT)) {
            return new CommandNode("RIGHT", null, null, previous().getLine());
        }
        if (match(TokenType.HALT)) {
            return new CommandNode("HALT", null, null, previous().getLine());
        }
        if (match(TokenType.GOTO)) {
            int commandLine = previous().getLine();
            Token labelToken = consume(TokenType.LABEL, "Expected a label next to GOTO");
            return new CommandNode("GOTO", null, labelToken.getLexeme(), commandLine);
        }

        Token currentToken = peek();
        throw new RuntimeException("Unexpected expression or command token '" + currentToken.getLexeme() +
                "' at line " + currentToken.getLine());

    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private List<CommandNode> getGotoNodes(ASTNode node) {
        List<CommandNode> list = new ArrayList<>();

        if (node instanceof CommandNode g && g.targetLabel != null) {
            list.add(g);
        } else if (node instanceof ConditionalNode c) {
            list.addAll(getGotoNodes(c.command));
        } else if (node instanceof LabelDeclNode l) {
            list.addAll(getGotoNodes(l.expression));
        }

        return list;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String error_message) {
        if (check(type))
            return advance(); // checks if correct type and advances
        Token token = peek(); // if not correct
        throw new RuntimeException(
                error_message + " (at line " + token.getLine() + " near '" + previous().getLexeme() + "')");
    }

}