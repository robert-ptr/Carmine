# CarmineHDL
Carmine is a description language made with the end-goal of generating redstone circuits in Minecraft.

## Language Features

## Language Grammar
program -> statement* EOF  
statement -> mainStatement | moduleStatement | blockStatement | constStatement | enumStatement | expressionStatement ;  
mainStatement -> "def main()" block ;  
moduleStatement -> "def" IDENTIFIER "(" arguments ")" block ;  
blockStatement -> block ;  
constStatement -> "const" IDENTIFIER ( " = " expression)? ;  
enumStatement -> enum IDENTIFIER "{" assignment* "}" ;  
expressionStatement -> expression;  

expression ->   

call -> primary ( "(" arguments ")" )? ;  
primary -> true | false | null | IDENTIFIER | "(" expression ")" ;  
arguments -> expression ( "," expression )*;  

## Project Status 🚧
This project is currently **unfinished** and is very much a **work in progress**.
