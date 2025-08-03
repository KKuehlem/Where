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
    | qualifiedIdentifier // Boolean variables
    | '(' expression ')'
    ;

operand
    : qualifiedIdentifier
    | STRING
    | NUMBER
    ;

qualifiedIdentifier
    : IDENTIFIER ('.' IDENTIFIER)*
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