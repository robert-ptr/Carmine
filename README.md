# 🧱 Carmine
Is a hardware description language made with the end-goal of generating **working 3D redstone circuits in Minecraft**.

## Language Features
- **Custom language syntax** for defining logic gates, modules, and control flow
- **Compile-time constructs** like loops and if-statements
- **Preprocessor** to evaluate constant expressions
- **Graph-based IR** representing digital logic flow
- **Circuit placer/router** (WIP) for 3D Minecraft redstone layout
- **.dot graph visualization** support for AST and circuit structure

## Roadmap

- [x] Lexer & Parser
- [x] .dot output of AST
- [ ] AST optimization (loop unrolling, constant folding, constant propagation, SSA, dead code elimination) - WIP
- [ ] Module connection logic
- [ ] Placement & routing
- [ ] Redstone layout generation
- [ ] Integration with Minecraft as a mod
- [ ] Functional ALU in Minecraft

## Language Grammar
```text
program → { declaration } EOF ;

declaration → moduleStatement
            | varStatement
            | enumStatement
            | statement ;

moduleStatement → "module" IDENTIFIER "(" arguments ")" "->" arguments blockStatement ;

varStatement → "var" IDENTIFIER "(" arguments ")" "->" arguments blockStatement ;

enumStatement → "enum" IDENTIFIER "{" { assignment "," } "}" ";" ;

statement → ifStatement
          | whileStatement
          | forStatement
          | blockStatement
          | expressionStatement ;

blockStatement → "{" { statement } "}" ;

ifStatement → "if" "(" expression ")" blockStatement [ "else" blockStatement ] ;

whileStatement → "while" "(" expression ")" statement ;

forStatement → "for" "(" [ expressionStatement ] [ expression ] ";" [ expression ] ")" statement ;

expressionStatement → expression ";" ;

expression → "var" assignment 
           | "module" assignment
           | assignment
           | or ;

or → and { "|" and } ;

and → equality { "&" equality } ;

equality → comparison { ( "!=" | "==" ) comparison } ;

comparison → term { ( ">" | "<" | ">=" | "<=" ) term } ;

term → factor { ( "+" | "-" ) factor } ;

factor → unary { ( "*" | "/" ) unary } ;

unary → ( "!" | "-" ) unary
       | call ;

call → primary [ "(" arguments ")" ] ;

primary → "true"
        | "false"
        | "null"
        | NUMBER
        | STRING
        | IDENTIFIER
        | IDENTIFIER "[" expression "]"
        | "(" expression ")" ;

arguments → [ expression { "," expression } ] ;

assignment → IDENTIFIER "=" expression ;

NUMBER → DIGIT+ [ "." DIGIT+ ] ;

STRING → "\"" { ALPHA | DIGIT | " " | SYMBOL }* "\"" ;

IDENTIFIER → ALPHA { ALPHA | DIGIT | "_" }* ;

ALPHA → "a" … "z" | "A" … "Z" | "_" ;

DIGIT → "0" … "9" ;

SYMBOL → Any printable symbol excluding quotes and control characters ;
```

## Usage

To use the Carmine compiler on a script, provide the path to your source file as the first argument:

```bash
java -jar carmine.jar <path-to-script>.car
```
*(During development, you run the main class `carmine.compiler.passes.Carmine` with the script path as arguments).*

The compiler will:
1. Lex and Parse your source code into an AST.
2. Run optimization passes (e.g. constant folding).
3. Emit a `.dot` string representation of the tree to standard output, which you can save to a `.dot` file or pipe to Graphviz to visually debug the output.

## Testing

Carmine utilizes **JSXM** (an eXtreme Machine for State Machines) for Model-Based Testing to ensure the robust handling of syntax edge cases without manually writing thousands of tests.

### 1. Generating Test Sequences
To generate the tests from our abstract state machine models (like `carmine_statements.xml`), run the following script:
```bash
./run_jsxm.sh
```
This script will automatically download the required JSXM tools (without polluting your global system environment), compile the Java specifications, and output exhaustive XML test sequences (e.g., `test/jsxm/carmine_statements_test.xml`).

### 2. Running the Tests
Once the XML sequences are generated, we use JUnit 5 to translate the abstract sequences into real `Token` streams and execute them against the Carmine parser. You can run these tests via Gradle:
```bash
# Run all tests
./gradlew test

# Or specifically run the statement parser tests
./gradlew test --tests "carmine.compiler.passes.StatementFormalTest"
```

## Architecture & Key Components

The Carmine compiler is organized into a pipeline, primarily living within `src/carmine/compiler`. The most vital components powering the translation from source code to functional Minecraft logic are:

### 1. Scanner (`passes/Scanner.java`)
The front-end lexer that reads raw Carmine source code and translates it sequentially into a standard list of `Token` representations. Details include:
- **Literal Support**: Detects and parses standard decimals along with advanced integer literals like binary (`0b...`) and hexadecimal (`0X...`).
- **Keyword Definitions**: Recognizes fundamental language keywords (e.g. `module`, `enum`, `true`/`false`, loop & conditional primitives) mapping them directly to dedicated `TokenType`s.
- **Line Tracking**: Maintains line numbers throughout tokenization to assist downstream systems in projecting accurate, human-readable error messages for syntax violations.
- **Comment Parsing**: Efficiently identifies and ignores inline comments (denoted by `//`) as well as conventional whitespace.

### 2. Parser (`passes/Parser.java`)
A robust top-down recursive descent parser. It continuously consumes tokens sequentially according to Carmine's language grammar and formulates an Abstract Syntax Tree (AST). Details include:
- **AST Nodes (`Expr` vs `Stmt`)**: Code constructs are strictly separated into Expressions (`structures/Expr.java`), which evaluate to values (such as literals and binary operations), and Statements (`structures/Stmt.java`), which produce state effects (such as declarations, module bindings, and control flow).
- **Expression Hierarchy**: Enforces operator precedence tightly by cascading method calls (`assignment() -> or() -> and() -> equality() -> comparison() -> term() -> factor() -> unary() -> call() -> primary()`), storing operations in structured `Expr` components.
- **Statement Support**: Distinguishes global declarations (modules, top-level variables, enumerations) versus scoped inner block `Stmt` code. 
- **Error Recovery (Panic Mode)**: When an unexpected token triggers a `ParseError`, the parser initiates panic mode. A dedicated `errorAtCurrent()` routine logs the fault, drops further incorrect nodes, and loops forward until it reaches a valid statement boundary (like a `;`) to resynchronize the parser state, avoiding massive cascading error stacks across miswritten code blocks.
- **Built-in Global Environment**: Implicit core mechanics, like basic functions (`print`, `and`, `or`), are mapped up-front inside a global `Environment` during runtime initialization.

### 3. Optimization & Passes (`passes/` directory)
The compiler iterates via AST visitor passes (`AstVisitor.java`) to refine the code structure before finally translating it into logic gates/redstone. Key transformation passes include:
- **`ConstantFolder.java`** & **`ConstantPropagator.java`**: Transverse the AST to evaluate static arithmetic and logical expressions, replacing complex trees with simpler literal constants at compile-time to reduce circuit size.
- **`LoopUnroller.java`**: Expands static loops into explicit, unrolled sequential logic. This is essential as static redstone circuits cannot natively handle dynamic recursive loops without immense complexity.
- **`SsaConverter.java`**: (WIP) Converts the program strictly into Static Single Assignment form, enabling further advanced compiler optimizations and easier layout routing.
- **`Optimizer.java`**: The main orchestrator that invokes these specialized passes sequentially.

### 4. Graph Visualization (`helpers/AstVisualizer.java`)
Since dealing with complex data structures inside the compiler can be tricky, this component translates the generated AST into a standard `.dot` graph format. By directing the compiler's textual output into Graphviz, developers can quickly generate a flowchart to debug structural transformations.

## References
Crafting Interpreters - Robert Nystrom  
Digital Design and Computer Architecture (RISC-V Edition) - Sarah L. Harris, David Money Harris  
Engineering a Compiler - Keith D. Cooper, Linda Torczon

## Project Status 🚧
This project is currently **unfinished** and is very much a **work in progress**.
