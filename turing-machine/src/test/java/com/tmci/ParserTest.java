package com.tmci;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

                ConditionalNode leftCondition = assertInstanceOf(ConditionalNode.class, program.statements.get(1));
                assertEquals("_", leftCondition.symbol);
                assertCommand(leftCondition.command, "LEFT", null, null);

                ConditionalNode rightCondition = assertInstanceOf(ConditionalNode.class, program.statements.get(2));
                assertEquals("1", rightCondition.symbol);
                assertCommand(rightCondition.command, "RIGHT", null, null);

                CommandNode gotoCommand = assertInstanceOf(CommandNode.class, program.statements.get(3));
                assertCommand(gotoCommand, "GOTO", null, "next_state");

                LabelDeclNode nextState = assertInstanceOf(LabelDeclNode.class, program.statements.get(4));
                assertEquals("next_state", nextState.label);
                CommandNode haltCommand = assertInstanceOf(CommandNode.class, nextState.expression);
                assertCommand(haltCommand, "HALT", null, null);
        }

        @Test
        void rejectsEmptyProgram() {
                RuntimeException exception = assertThrows(RuntimeException.class, () -> parse(""));

                assertEquals("Program is empty", exception.getMessage());
        }

        @Test
        void acceptsLeadingTrailingWhitespaceAndBlankLines() {
                ProgramNode program = assertInstanceOf(
                                ProgramNode.class,
                                parse("""

                                                \tstart: HALT

                                                """));

                assertEquals(1, program.statements.size());
        }

        @Test
        void rejectsAnIdentifierWithoutAStatement() {
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> parse("JUMP"));

                assertTrue(exception.getMessage().contains("Unexpected expression or command token"));
        }

        @Test
        void rejectsWriteWithMissingSymbol() {
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> parse("WRITE"));

                assertTrue(exception.getMessage().contains("Expected symbol next to WRITE"));
        }

        @Test
        void rejectsWriteWithMissingSymbolAtEndOfFile() {
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> parse("IF 0 WRITE"));

                assertTrue(exception.getMessage().contains("Expected symbol next to WRITE"));
        }

        @Test
        void rejectsWriteWithTooManyArguments() {
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> parse("WRITE 1 0"));

                assertTrue(exception.getMessage().contains("Unexpected expression or command token"));
        }

        @Test
        void rejectsConditionalWithMultiCharacterSymbol() {
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> parse("IF 10 GOTO start"));

                assertTrue(exception.getMessage().contains("Expected symbol after IF"));
        }

        @Test
        void rejectsConditionalWithMissingCommand() {
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> parse("IF 0"));

                assertTrue(exception.getMessage().contains("Unexpected expression or command token"));
        }

        @Test
        void rejectsGotoWithMissingTargetAtEndOfFile() {
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> parse("GOTO"));

                assertTrue(exception.getMessage().contains("Expected a label next to GOTO"));
        }

        @Test
        void rejectsLabelWithMissingStatementAtEndOfFile() {
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> parse("start:"));

                assertTrue(exception.getMessage().contains("Unexpected expression or command token"));
        }

        @Test
        void rejectsMalformedLabelWithoutIdentifier() {
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> parse(": HALT"));

                assertTrue(exception.getMessage().contains("Unexpected character ':'"));
        }

        @Test
        void rejectsDuplicateLabels() {
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> parse("""
                                                start: HALT
                                                start: HALT
                                                """));

                assertTrue(exception.getMessage().contains("Duplicate"));
        }

        @Test
        void rejectsUndefinedGotoTarget() {
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> parse("GOTO missing"));

                assertTrue(exception.getMessage().contains("Undefined label"));
        }

        @Test
        void rejectsAdjacentConditionalExpressions() {
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> parse("IF 0 IF 1 LEFT"));

                assertTrue(exception.getMessage().contains("Unexpected expression or command token"));
        }

        @Test
        void rejectsExpressionStartingWithSymbol() {
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> parse("0 WRITE 1"));

                assertTrue(exception.getMessage().contains("Unexpected expression or command token"));
        }

        private static ASTNode parse(String source) {
                return new Parser(new Scanner(source).scanTokens()).parse_program();
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
