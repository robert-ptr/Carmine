# 🧱 Carmine
Is a hardware description language made with the end-goal of generating **working 3D redstone circuits in Minecraft**.

## Language Features
- **Custom language syntax** for defining logic gates, modules, and control flow
- **Compile-time constructs** like loops and if-statements
- **Preprocessor** to evaluate constant expressions
- **Graph-based IR** representing digital logic flow
- **Circuit placer/router** (WIP) for 3D Minecraft redstone layout
- **.dot graph visualization** support for AST and circuit structure

## Language Grammar
```text
program → { declaration } EOF ;

declaration → moduleStatement
            | constStatement
            | enumStatement
            | statement ;

moduleStatement → "module" IDENTIFIER "(" arguments ")" "->" arguments blockStatement ;

constStatement → "const" IDENTIFIER "(" arguments ")" "->" arguments blockStatement ;

enumStatement → "enum" IDENTIFIER "{" { assignment "," } "}" ";" ;

statement → ifStatement
          | whileStatement
          | forStatement
          | blockStatement
          | expressionStatement ;

blockStatement → "{" { statement } "}" ;

ifStatement → "if" "(" expression ")" statement [ "else" statement ] ;

whileStatement → "while" "(" expression ")" statement ;

forStatement → "for" "(" [ expressionStatement ] [ expression ] ";" [ expression ] ")" statement ;

expressionStatement → expression ";" ;

expression → IDENTIFIER "=" expression
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



## Project Status 🚧
This project is currently **unfinished** and is very much a **work in progress**.
