# CarmineHDL
Carmine is a description language made with the end-goal of generating redstone circuits in Minecraft.

## Language Features

## Language Grammar
| **Rule**              | **Definition**                                                                                                                          |           |
| --------------------- | --------------------------------------------------------------------------------------------------------------------------------------- | --------- |
| `program`             | `{ statement } EOF`                                                                                                                     |           |
| `statement`           | `mainStatement`<br>`\| moduleStatement`<br>`\| blockStatement`<br>`\| constStatement`<br>`\| enumStatement`<br>`\| expressionStatement` |           |
| `mainStatement`       | `"def main()" blockStatement`                                                                                                           |           |
| `moduleStatement`     | `"def" IDENTIFIER "(" arguments ")" "->" arguments blockStatement`                                                                      |           |
| `blockStatement`      | `"{" { statement } "}"`                                                                                                                 |           |
| `constStatement`      | `"const" IDENTIFIER [ "=" expression ]`                                                                                                 |           |
| `enumStatement`       | `"enum" IDENTIFIER "{" { assignment } "}"`                                                                                              |           |
| `expressionStatement` | `expression`                                                                                                                            |           |
| `expression`          | `IDENTIFIER "=" expression`<br>`\| or`                                                                                                  |           |
| `or`                  | `and { "\|" and }`                                                                                                                      |           |
| `and`                 | `equality { "&" equality }`                                                                                                             |           |
| `equality`            | `comparison { ( "!=" \| "==" ) comparison }`                                                                                            |           |
| `comparison`          | `term { ( ">" \| "<" \| ">=" \| "<=" ) term }`                                                                                          |           |
| `term`                | `factor { ( "+" \| "-" ) factor }`                                                                                                      |           |
| `factor`              | `unary { ( "*" \| "/" ) unary }`                                                                                                        |           |
| `unary`               | `( "!" \| "-" ) unary`<br>`\| call`                                                                                                     |           |
| `call`                | `primary [ "(" arguments ")" ]`                                                                                                         |           |
| `primary`             | `"true"`<br>`\| "false"`<br>`\| "null"`<br>`\| IDENTIFIER`<br>`\| "(" expression ")"`                                                   |           |
| `arguments`           | `expression { "," expression }`                                                                                                         |           |


## Project Status 🚧
This project is currently **unfinished** and is very much a **work in progress**.
