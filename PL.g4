grammar PL;

@header {
import backend.*;
}

@members {
}

statement returns [Expr expr]
    : 'print' '(' e=expression ')' ';' { $expr = new Print($e.result); }
    | e=expression ';' { $expr = $e.result; }
    ;

program returns [Expr expr]
    : e=statement EOF { $expr = $e.expr; }
    ;

expression returns [Expr result]
	: e=expression '++' term { $result = new Concat($e.result, $term.result); } // Add this line
	| e=expression '*' term { $result = new Arith(Operator.MUL, $e.result, $term.result); }
	| e=expression '/' term { $result = new Arith(Operator.DIV, $e.result, $term.result); }
	| e=expression '+' term { $result = new Arith(Operator.ADD, $e.result, $term.result); }
	| e=expression '-' term { $result = new Arith(Operator.SUB, $e.result, $term.result); }
	| ID '=' expression { $result = new Assign($ID.text, $expression.result); }
	| term { $result = $term.result; }
	;

term returns [Expr result]
	: '(' e=expression ')' { $result = $e.result; }
	| value { $result = $value.result; }
	| funCall { $result = $funCall.result; }
	;

argList returns [List<Expr> result] 
	: e=expression { $result = new ArrayList<Expr>(); $result.add($e.result); } 
	(',' e=expression { $result.add($e.result); })* ;
funCall returns [Expr result] : ID '(' argList? ')' { $result = new FunCall($ID.text, $argList.result); };
value returns [Expr result] : NUMBER { $result = new IntLiteral($NUMBER.text); } | STRING { $result = new StringLiteral($STRING.text); } | ID { $result = new Deref($ID.text); };

ID : [a-zA-Z_][a-zA-Z_0-9]* ;
NUMBER : [0-9]+ ('.' [0-9]+)?;
STRING : '"' (~["\\] | '\\' .)* '"';
WHITESPACE : [ \t\r\n] -> skip;