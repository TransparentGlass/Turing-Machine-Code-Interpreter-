package com.tmci;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Parser{
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    //program -> statements*
    public ASTNode parse_program(){
        List<ASTNode> statements = new ArrayList<>();

        if(isAtEnd()){
            throw new RuntimeException("Program is empty");
        }

        while (!isAtEnd()){
            statements.add(parse_statements());
        }

        Map<String, Integer> labelTable = new HashMap<>();

        for(int i = 0; i < statements.size(); i++){
            ASTNode stmt = statements.get(i);
            if(stmt instanceof LabelDeclNode labelNode){
                labelTable.put(labelNode.label, i);
            }
        }

        return new ProgramNode(statements);
        
    }
    // statements -> label_decl | expression
    private ASTNode parse_statements(){
        if (check(TokenType.IDENTIFIER)){
            return parse_labelDecl();
        }

        return parse_expression();
    }

    //label_decl -> IDENTIFIER expression
    private ASTNode parse_labelDecl(){
        Token labelToken = consume(TokenType.IDENTIFIER, "Expected an identifier");
        ASTNode expr = parse_expression();
        return new LabelDeclNode(labelToken.getLexeme(), expr);

    }
    //expression -> conditional | command
    private ASTNode parse_expression(){
        if(match(TokenType.IF)){
            return parse_conditionals();
        }
        return parse_command();
    }

    //conditional -> IF SYMBOL command
    private ASTNode parse_conditionals(){
        Token symbolToken = consume(TokenType.SYMBOL, "Expected symbol after IF");
        CommandNode cmd = parse_command();
        return new ConditionalNode(symbolToken.getLexeme(), cmd);
    }

    
    //command -> WRITE SYMBOL| LEFT | RIGHT | HALT | GOTO LABEL
    private CommandNode parse_command() {
        if (match(TokenType.WRITE)) {
            Token symbolToken = consume(TokenType.SYMBOL, "Expected symbol next to WRITE");
            String symbol = symbolToken.getLexeme();
            return new CommandNode("WRITE", symbol, null);
        }
        if (match(TokenType.LEFT)) {
            return new CommandNode("LEFT", null, null);
        }
        if (match(TokenType.RIGHT)) {
            return new CommandNode("RIGHT", null, null);
        }
        if (match(TokenType.HALT)) {
            return new CommandNode("HALT", null, null);
        }
        if (match(TokenType.GOTO)){
            Token labelToken = consume(TokenType.LABEL,"Expected a label next to GOTO");
            return new CommandNode("GOTO", null, labelToken.getLexeme());
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



    private Token peek(){
        return tokens.get(current);
    }

    private Boolean isAtEnd(){
        return peek().getType() == TokenType.EOF;
    }

    private Boolean check(TokenType type){
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String error_message) {
        if (check(type)) return advance(); //checks if correct type and advances
        Token token = peek(); //if not correct
        throw new RuntimeException(error_message + " (at line " + token.getLine() + " near '" + previous().getLexeme() + "')");
    }

    
}