package com.tmci;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class ParserTest {

    @Test
    void parsesLabelsConditionalsCommandsAndGoto() {
        String source = """
                start_state: IF 0 WRITE 1
                IF _ LEFT
                IF 1 RIGHT
                GOTO next_state
                next_state: HALT
                """;

        ASTNode result = new Parser(new Scanner(source).scanTokens()).parse_program();
        ProgramNode program = assertInstanceOf(ProgramNode.class, result);

        assertEquals(5, program.statements.size());

        LabelDeclNode startState = assertInstanceOf(LabelDeclNode.class, program.statements.get(0));
        assertEquals("start_state", startState.label);
        ConditionalNode writeCondition = assertInstanceOf(ConditionalNode.class, startState.expression);
        assertEquals("0", writeCondition.symbol);
        assertCommand(writeCondition.command, "WRITE", "1", null);

        ConditionalNode leftCondition =
                assertInstanceOf(ConditionalNode.class, program.statements.get(1));
        assertEquals("_", leftCondition.symbol);
        assertCommand(leftCondition.command, "LEFT", null, null);

        ConditionalNode rightCondition =
                assertInstanceOf(ConditionalNode.class, program.statements.get(2));
        assertEquals("1", rightCondition.symbol);
        assertCommand(rightCondition.command, "RIGHT", null, null);

        CommandNode gotoCommand = assertInstanceOf(CommandNode.class, program.statements.get(3));
        assertCommand(gotoCommand, "GOTO", null, "next_state");

        LabelDeclNode nextState = assertInstanceOf(LabelDeclNode.class, program.statements.get(4));
        assertEquals("next_state", nextState.label);
        CommandNode haltCommand = assertInstanceOf(CommandNode.class, nextState.expression);
        assertCommand(haltCommand, "HALT", null, null);
    }

    private static void assertCommand(
            CommandNode command,
            String expectedType,
            String expectedSymbol,
            String expectedTargetLabel) {
        assertEquals(expectedType, command.commandType);
        assertEquals(expectedSymbol, command.symbol);
        assertEquals(expectedTargetLabel, command.targetLabel);
    }
}
