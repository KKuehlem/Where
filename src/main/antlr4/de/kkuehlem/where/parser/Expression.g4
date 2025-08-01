grammar Expression;

expression: orExpr;

orExpr
    : andExpr ('OR' andExpr)*
    ;

andExpr
    : notExpr ('AND' notExpr)*
    ;

notExpr
    : 'NOT' notExpr
    | booleanExpr
    ;

booleanExpr
    : operand operator operand
    | IDENTIFIER // Boolean variables
    | '(' expression ')'
    ;

operand
    : IDENTIFIER
    | STRING
    | NUMBER
    ;

operator
    : '='
    | '!='
    | '<'
    | '<='
    | '>'
    | '>='
    ;

IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;
STRING: '\'' (~['\\] | '\\' .)* '\'';
NUMBER: [0-9]+ ('.' [0-9]+)?;

WS: [ \t\r\n]+ -> skip;