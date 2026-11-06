# Turing Machine Code Interpreter

This project is a Java interpreter for a small Turing-machine instruction
language. The project currently includes a scanner, parser, abstract syntax
tree classes, and a basic Turing-machine model.

## Current progress

### Scanner

The scanner converts source text into tokens and tracks source line numbers.
It currently supports:

- Keywords: `IF`, `WRITE`, `LEFT`, `RIGHT`, `GOTO`, and `HALT`
- Single-character non-whitespace tape symbols, such as `0`, `_`, `!`, or `?`
- Identifiers written with a trailing colon, such as `start_state:` or `a:`
- Label tokens used as jump targets, such as `next_state`
- `NEWLINE` tokens for enforcing one instruction per line
- Spaces, tabs, carriage returns, and blank lines
- An `EOF` token at the end of input

The scanner rejects:

- Unsupported comment markers such as `#`
- Unsupported quoted text
- Reserved keywords used as identifiers, such as `HALT:`
- Invalid labels that do not match `[A-Za-z][A-Za-z0-9_]*`

### Parser

The parser consumes scanner tokens and builds an abstract syntax tree.
Supported structures include:

- Label declarations:

  ```text
  start_state: HALT
  ```

- Conditional commands:

  ```text
  IF 0 WRITE 1
  IF _ LEFT
  IF 1 RIGHT
  ```

- Direct commands: `WRITE`, `LEFT`, `RIGHT`, and `HALT`
- Jump commands:

  ```text
  GOTO next_state
  ```

- One instruction per line; multiple instructions on one line are rejected
- Optional labels must appear before an instruction on the same line
- Label-only lines are rejected
- Empty and whitespace-only program detection
- Syntax errors for missing symbols, missing commands, missing jump targets,
  invalid command ordering, and incomplete statements at end of input

The parser currently produces AST nodes for programs, label declarations,
conditionals, and commands. Every AST node records its source line through
`ASTNode#getLine()`, allowing validation errors to identify the relevant
instruction.

The parser also validates:

- Duplicate label declarations
- Undefined `GOTO` targets
- Case-sensitive label names
- Forward jump targets after the complete program is parsed

### Input rules

- Commands must be uppercase.
- Labels must match `[A-Za-z][A-Za-z0-9_]*`.
- Labels are case-sensitive and must be unique.
- Every `GOTO` target must be declared.
- A symbol argument is exactly one non-whitespace character.
- Quoted strings are not supported.
- Comments are not supported and are rejected.
- Leading/trailing whitespace and blank lines are ignored.

Execution is not implemented yet. Runtime behavior such as step counting,
HALT handling, tape movement, and falling past the final instruction remains
part of the execution stage.

## Example program

```text
start_state: IF 0 WRITE 1
IF _ LEFT
IF 1 RIGHT
GOTO next_state
next_state: HALT
```

Each instruction must be on its own line. A one-character label is valid:

```text
a: RIGHT
```

but a label without an instruction is invalid:

```text
a:
```

## Project structure

```text
turing-machine/
├── pom.xml
└── src/
    ├── main/java/com/tmci/
    │   ├── Scanner.java
    │   ├── Parser.java
    │   ├── Token.java
    │   ├── TokenType.java
    │   └── ASTNode.java
    └── test/java/com/tmci/
        ├── ScannerTest.java
        ├── ParserTest.java
        └── TuringMachineTest.java
```

## Running tests

The project targets Java 21 and uses Maven with JUnit 5.

Run all tests from the `turing-machine` directory:

```bash
mvn test
```

Run scanner tests only:

```bash
mvn -Dtest=ScannerTest test
```

Run parser tests only:

```bash
mvn -Dtest=ParserTest test
```

The test suite includes scanner and parser edge cases for empty programs,
invalid characters, malformed symbols, missing arguments, invalid command
ordering, label-only statements, duplicate labels, and undefined jump
targets.
