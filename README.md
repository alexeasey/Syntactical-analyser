# Syntactical Analyser

A compiler front-end for a subset of Java, built in two phases: **lexical analysis** (tokenisation) and **syntactic analysis** (LL(1) parsing). The core goal of this project was to implement an LL(1) **parsing table** that drives the parser in O(1) lookup time, replacing slower linear-search or switch-based dispatch.

---

## Motivation

The central design challenge was building a table-driven LL(1) parser. Rather than encoding grammar rules inside a chain of `if`/`switch` branches, every production is pre-computed into a 3D array indexed directly by non-terminal row and terminal column — both derived from enum ordinals. This means any grammar lookup is a single array access at constant time.

---

## How It Works

### Phase 1 — Lexical Analysis

`LexicalAnalyser.analyse(String sourceCode)` scans the input and produces a `List<Token>`. It handles:

- Keywords: `public`, `class`, `static`, `void`, `main`, `if`, `else`, `while`, `for`, `int`, `char`, `boolean`, `true`, `false`
- Operators: `+`, `-`, `*`, `/`, `%`, `=`, `==`, `!=`, `<`, `<=`, `>`, `>=`, `&&`, `||`
- Delimiters: `{`, `}`, `(`, `)`, `;`
- Identifiers and integer literals
- Character literals `'c'` and string literals `"..."`
- `System.out.println` as a single `PRINT` token

Throws `LexicalException` for unrecognised or malformed tokens.

### Phase 2 — Syntactic Analysis and the Parsing Table

`SyntacticAnalyser.parse(List<Token> tokens)` is a stack-based LL(1) parser that builds a `ParseTree`.

#### The Parsing Table

The parser's grammar is encoded in a `String[][][]` table where:

- **Rows** correspond to non-terminals (grammar variables like `prog`, `stat`, `expr`, etc.)
- **Columns** correspond to terminals (token types like `IF`, `ID`, `NUM`, etc.)
- Each **cell** holds the right-hand side of the production rule to apply, as an array of symbol strings, or `null` if no rule applies

Row and column indices are computed directly from Java enum ordinals:

```java
int row    = TreeNode.Label.valueOf(nonTerminal).ordinal() + 1;
int column = token.getType().ordinal() + 1;
```

This makes every table lookup O(1) — no scanning, no branching over grammar rules.

#### Parsing Algorithm

1. Push `prog` onto the stack and set it as the parse tree root.
2. Peek at the top of the stack and the front of the token list.
3. Look up `table[row][column]` to get the production to apply.
4. If the top of stack is a **non-terminal**: expand it by pushing its production onto the stack and recursing to build child nodes.
5. If the top of stack is a **terminal**: match it against the current token, create a leaf node, and advance the token list.
6. If the cell is `null` or stack/token list mismatch: throw `SyntaxException`.
7. Repeat until the stack is empty and all tokens are consumed.

#### Grammar Non-Terminals

```
prog, los, stat, whilestat, forstat, forstart, forarith,
ifstat, elseifstat, elseorelseif, possif,
assign, decl, possassign, print, type,
expr, boolexpr, boolop, booleq, boollog,
relexpr, relexprprime, relop,
arithexpr, arithexprprime, term, termprime, factor,
printexpr, charexpr, epsilon
```

#### Supported Language Constructs

- Class and `main` method structure
- Variable declarations: `int`, `char`, `boolean`
- Assignment statements
- Arithmetic expressions: `+`, `-`, `*`, `/`, `%`
- Relational expressions: `==`, `!=`, `<`, `>`, `<=`, `>=`
- Boolean expressions: `&&`, `||`
- `if` / `else if` / `else` chains
- `while` loops
- `for` loops
- `System.out.println(...)` print statements
- Literals: integers, characters, booleans, strings

---

## Project Structure

```
SyntacticalAnalyser/
├── LexicalAnalyser.java          # Phase 1: tokenises source code
├── Token.java                    # Token class with all TokenType enum variants
├── Symbol.java                   # Interface shared by TokenType and TreeNode.Label
├── SyntacticAnalyser.java        # Phase 2: LL(1) table-driven parser
├── ParseTree.java                # Wrapper holding the parse tree root
├── TreeNode.java                 # Parse tree node with grammar Labels
├── LexicalException.java         # Thrown on unrecognised / malformed tokens
├── SyntaxException.java          # Thrown on grammar rule violations
├── Runner.java                   # Entry point with a sample parse
└── SyntacticalAnalysisTests.java # JUnit 5 test suite
```

---

## Usage

```java
List<Token> tokens = LexicalAnalyser.analyse(
    "public class Foo { public static void main(String[] args){ int i = 0; } }"
);
ParseTree tree = SyntacticAnalyser.parse(tokens);
System.out.println(tree);
```

Run `Runner.java` directly to see a sample parse in action.

---

## Running Tests

The test suite uses JUnit 5.

**With `javac` / `java` manually:**
```bash
javac -cp .:junit-platform-console-standalone.jar SyntacticalAnalyser/*.java
java -jar junit-platform-console-standalone.jar --class-path . --select-class SyntacticalAnalysisTests
```

**With an IDE (IntelliJ / Eclipse):** Add JUnit 5 to project dependencies and run `SyntacticalAnalysisTests` directly.

---

## Requirements

- Java 8 or later
- JUnit 5 (for running tests only)
