# Java Syntactical Analyser

A two-phase compiler front-end for a subset of Java, implementing lexical analysis (tokenisation) followed by syntactic analysis (LL(1) parsing) to produce a parse tree.

## Overview

The analyser processes a restricted subset of Java that supports:
- Class and `main` method structure
- Variable declarations (`int`, `char`, `boolean`)
- Assignment statements
- Arithmetic expressions (`+`, `-`, `*`, `/`, `%`)
- Relational and boolean expressions (`==`, `!=`, `<`, `>`, `<=`, `>=`, `&&`, `||`)
- `if` / `else if` / `else` statements
- `while` loops
- `for` loops
- `System.out.println` print statements
- Literals: integers, characters (`'c'`), booleans (`true`/`false`), strings (`"..."`)

## Project Structure

```
SyntacticalAnalyser/
├── LexicalAnalyser.java        # Tokenises source code into a list of Tokens
├── Token.java                  # Token class with all TokenType variants
├── Symbol.java                 # Interface shared by Token.TokenType and TreeNode.Label
├── SyntacticAnalyser.java      # LL(1) table-driven parser, produces a ParseTree
├── ParseTree.java              # Wrapper holding the root TreeNode
├── TreeNode.java               # Parse tree node with grammar Labels
├── LexicalException.java       # Thrown on unrecognised / malformed tokens
├── SyntaxException.java        # Thrown on grammar violations
├── Runner.java                 # Entry point demonstrating usage
└── SyntacticalAnalysisTests.java  # JUnit 5 test suite
```

## How It Works

### Phase 1 — Lexical Analysis (`LexicalAnalyser`)

`LexicalAnalyser.analyse(String sourceCode)` scans the source string and returns a `List<Token>`. It handles:
- Keywords (`public`, `class`, `static`, `void`, `main`, `if`, `else`, `while`, `for`, `int`, `char`, `boolean`, `true`, `false`)
- Operators and delimiters (`+`, `-`, `*`, `/`, `%`, `=`, `==`, `!=`, `<`, `<=`, `>`, `>=`, `&&`, `||`, `{`, `}`, `(`, `)`, `;`)
- Identifiers and numeric literals
- Character literals (`'x'`) and string literals (`"..."`)

Throws `LexicalException` for unrecognised tokens (e.g. identifiers starting with `_` or `$`).

### Phase 2 — Syntactic Analysis (`SyntacticAnalyser`)

`SyntacticAnalyser.parse(List<Token> tokens)` uses an LL(1) parsing table driven by a stack to construct a `ParseTree`. The grammar non-terminals include:

`prog`, `los`, `stat`, `whilestat`, `forstat`, `ifstat`, `elseifstat`, `assign`, `decl`, `print`, `expr`, `arithexpr`, `relexpr`, `boolexpr`, `term`, `factor`, and more.

Throws `SyntaxException` for input that does not conform to the grammar.

## Usage

```java
List<Token> tokens = LexicalAnalyser.analyse(
    "public class Foo { public static void main(String[] args){ int i = 0; } }"
);
ParseTree tree = SyntacticAnalyser.parse(tokens);
System.out.println(tree);
```

Run `Runner.java` directly to see a sample parse in action.

## Running Tests

The test suite uses JUnit 5. Compile and run with your preferred build tool or IDE.

**With `javac` / `java` manually:**
```bash
javac -cp .:junit-platform-console-standalone.jar SyntacticalAnalyser/*.java
java -jar junit-platform-console-standalone.jar --class-path . --select-class SyntacticalAnalysisTests
```

**With an IDE (IntelliJ / Eclipse):** Add JUnit 5 to the project dependencies and run `SyntacticalAnalysisTests` directly.

## Requirements

- Java 8 or later
- JUnit 5 (for running tests only)
