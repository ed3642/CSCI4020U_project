grammar PL;

@header {
import backend.*;
}

@members {
}

statement returns [Expr expr]
	: 'print' '(' e=expression ')' ';' { $expr = new Print($e.result); }
	| e=expression ';' { $expr = $e.result; }
	| a=assign ';' { $expr = $a.result; }
	;
	
program returns [Expr expr]
	@init {
		List<Expr> exprs = new ArrayList<>();
	}
	: (s=statement { exprs.add($s.expr); })* EOF { $expr = new Program(exprs); }
	;
	
expression returns [Expr result]
	: e=expression op=('++' | '*' | '/' | '+' | '-') term { 
		switch ($op.text) {
			case "++": $result = new Concat($e.result, $term.result); break;
			case "*": $result = new Arith(Operator.MUL, $e.result, $term.result); break;
			case "/": $result = new Arith(Operator.DIV, $e.result, $term.result); break;
			case "+": $result = new Arith(Operator.ADD, $e.result, $term.result); break;
			case "-": $result = new Arith(Operator.SUB, $e.result, $term.result); break;
		}
	}
	| term { $result = $term.result; }
	;

assign returns [Expr result]
	: ID '=' expression { $result = new Assign($ID.text, $expression.result); }
	;

term returns [Expr result]
	: '(' e=expression ')' { $result = $e.result; }
	| value { $result = $value.result; }
	| funCall { $result = $funCall.result; }
	| assign { $result = $assign.result; }
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