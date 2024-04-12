grammar PL;

@header {
import backend.*;
import java.util.Arrays;
}

@members {
}

statement returns [Expr expr]
    : 'print' '(' e=expression ')' ';'? { $expr = new Print($e.result); }
    | e=expression ';'? { $expr = $e.result; }
    | a=assign ';'? { $expr = $a.result; }
    | f=forLoop { $expr = $f.expr; }
    | fd=functionDef { $expr = $fd.expr; }
    | ie=ifElse { $expr = $ie.expr; }
    ;
	
program returns [Expr expr]
		@init {
				List<Expr> exprs = new ArrayList<>();
		}
		: (fd=functionDef { exprs.add($fd.expr); })*
			(s=statement { exprs.add($s.expr); })* { $expr = new Program(exprs); }
		;

forLoop returns [Expr expr]
    : 'for' '(' ID 'in' start=expression '..' end=expression ')' '{' p=program '}' {
        $expr = new ForLoop($ID.text, ((ForLoopContext)_localctx).start.result, ((ForLoopContext)_localctx).end.result, ((Program)((ForLoopContext)_localctx).p.expr).getExprs());
    }
    ;

// list functions

listLength returns [Expr result]
    : ID '.length()' { $result = new ListLength(new Deref($ID.text)); }
    ;

listSum returns [Expr result]
    : ID '.sum()' { $result = new ListSum(new Deref($ID.text)); }
    ;

comparison returns [Expr expr]
    : left=expression op=('<' | '>' | '==') right=expression {
        CmpOperators operator = CmpOperators.EQ; // default value
        switch ($op.text) {
            case "<": operator = CmpOperators.LT; break;
            case ">": operator = CmpOperators.GT; break;
            case "==": operator = CmpOperators.EQ; break;
        }
        $expr = new Cmp(operator, $left.result, $right.result);
    }
    ;

functionDef returns [Expr expr]
    : 'function' ID '(' params=parameters ')' '{' p=program '}' {
        $expr = new FunctionDef($ID.text, $params.result, ((Program)$p.expr).getExprs());
    }
    | ID '=' '(' params=parameters ')' '>>' '{' p=program '}' {
        $expr = new FunctionDef($ID.text, $params.result, ((Program)$p.expr).getExprs());
    }
    ;

funCall returns [Expr result]
    : ID '(' argList? ')' { $result = new FunCall($ID.text, $argList.result); }
    ;

parameters returns [List<String> result]
    : p=ID { $result = new ArrayList<>(); $result.add($p.text); } (',' p=ID { $result.add($p.text); })*
    | { $result = new ArrayList<>(); } 
    ;

ifElse returns [Expr expr]
    : 'if' '(' cond=expression ')' '{' trueExpr=statement '}' 'else' '{' falseExpr=statement '}' {
        $expr = new Ifelse($cond.result, $trueExpr.expr, $falseExpr.expr);
    }
    | 'if' '(' cond=expression ')' '{' trueStmt=statement '}' 'else' '{' falseStmt=statement '}' {
        $expr = new Ifelse($cond.result, $trueStmt.expr, $falseStmt.expr);
    }
    ;

expression returns [Expr result]
    : left=expression op=('<' | '>' | '==') right=arithmetic {
        CmpOperators operator = CmpOperators.EQ; // default value
        switch ($op.text) {
            case "<": operator = CmpOperators.LT; break;
            case ">": operator = CmpOperators.GT; break;
            case "==": operator = CmpOperators.EQ; break;
        }
        $result = new Cmp(operator, $left.result, $right.result);
    }
    | arithmetic { $result = $arithmetic.result; }
    ;

arithmetic returns [Expr result]
	: e=arithmetic op=('++' | '*' | '/' | '+' | '-') term { 
		switch ($op.text) {
			case "++": $result = new Concat($e.result, $term.result); break;
			case "*": 
				if ($e.result instanceof StringLiteral || $term.result instanceof StringLiteral) {
					$result = new Repeat($e.result, $term.result);
				} else {
					$result = new Arith(Operator.MUL, $e.result, $term.result);
				}
				break;
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
    | listLiteral { $result = $listLiteral.result; }
    | listLength { $result = $listLength.result; }
    | listSum { $result = $listSum.result; }
    ;

//	list literal

listLiteral returns [Expr result]
    : '[' elements=argList? ']' { $result = new ListLiteral($elements.result); }
    ;

argList returns [List<Expr> result] 
    : e=expression { $result = new ArrayList<Expr>(); $result.add($e.result); } 
    (',' e=expression { $result.add($e.result); })*
    | e=expression { $result = new ArrayList<Expr>(); $result.add($e.result); }
    ;

value returns [Expr result] : NUMBER { $result = new IntLiteral($NUMBER.text); } | STRING { $result = new StringLiteral($STRING.text); } | ID { $result = new Deref($ID.text); };

// lexer rules
ID : [a-zA-Z_][a-zA-Z_0-9]*;
NUMBER : [0-9]+ ('.' [0-9]+)?;
STRING : '"' (~["\\] | '\\' .)* '"';
WHITESPACE : [ \t\r\n] -> skip;
COMMENT : '/*' .*? '*/' -> skip;
LINE_COMMENT : '//' .*? '\n' -> skip;