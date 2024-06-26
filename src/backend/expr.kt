package backend

abstract class Expr {
    abstract fun eval(runtime: Runtime): Data

    fun mapStatementsToExprs(statements: List<Expr>): List<Expr> {
        return statements.map { it }
    }

    fun evalAll(statements: List<Expr>, runtime: Runtime): Data {
        var lastResult: Data = None
        for (stmt in statements) {
            lastResult = stmt.eval(runtime)
        }
        return lastResult
    }
}

class NoneExpr(): Expr() {
    override fun eval(runtime:Runtime) = None
}

class IntLiteral(val lexeme:String):Expr() {
    override fun eval(runtime:Runtime):Data
    = IntData(lexeme.toInt())
}

enum class Operator {
    ADD,
    SUB,
    MUL,
    DIV
}

class Arith(
    val op: Operator,
    val left: Expr,
    val right: Expr,
) : Expr() {
    override fun eval(runtime: Runtime): Data {
        val x = left.eval(runtime)
        val y = right.eval(runtime)
        return when {
            x is IntData && y is IntData -> IntData(
                when (op) {
                    Operator.ADD -> x.v + y.v
                    Operator.SUB -> x.v - y.v
                    Operator.MUL -> x.v * y.v
                    Operator.DIV -> x.v / y.v
                }
            )
            op == Operator.MUL && x is StringData && y is IntData -> StringData(x.v.repeat(y.v))
            else -> throw Exception("Unsupported operation")
        }
    }
}

enum class CmpOperators {
    LT,
    GT,
    EQ,
}

class Cmp(
    val op:CmpOperators,
    val left:Expr,
    val right:Expr
) : Expr() {
    override fun eval(runtime:Runtime): Data {
        val x:Data = left.eval(runtime)
        val y:Data = right.eval(runtime)
        if(x is IntData && y is IntData) {
            val result = when(op) {
                CmpOperators.LT -> x.v < y.v
                CmpOperators.GT -> x.v > y.v
                CmpOperators.EQ -> x.v == y.v
            }
            return BooleanData(result)
        } else {
            throw Exception("Cannot perform comparison")
        }
    }
}

class Assign(
    val name: String,
    val expr: Expr
    ): Expr() {
        override fun eval(runtime:Runtime):Data {
        val v:Data = expr.eval(runtime)
        runtime.symbolTable[name] = v
        return None
    }
}

class ForLoop(
    private val id: String,
    private val start: Expr,
    private val end: Expr,
    private val body: List<Expr>
) : Expr() {
    override fun eval(runtime: Runtime): Data {
        val startValue = start.eval(runtime) as? IntData ?: throw Exception("Start value is not an integer")
        val endValue = end.eval(runtime) as? IntData ?: throw Exception("End value is not an integer")

        var lastResult: Data = None
        for (i in startValue.v..endValue.v) {
            runtime.symbolTable[id] = IntData(i)
            lastResult = evalAll(body, runtime)
        }

        return lastResult
    }
}

class Repeat(
    private val expr: Expr,
    private val times: Expr
) : Expr() {
    override fun eval(runtime: Runtime): Data {
        val str = expr.eval(runtime) as? StringData ?: throw Exception("Operand is not a string")
        val count = times.eval(runtime) as? IntData ?: throw Exception("Repeat count is not an integer")
        return StringData(str.v.repeat(count.v))
    }
}

class Deref(
    val name:String
    ): Expr() {
    override fun eval(runtime:Runtime):Data {
        val v = runtime.symbolTable[name]
        if(v != null) {
            return v
        } else {
            return None
        }
    }
}

class FunCall(
    val name: String,
    val args: List<Expr>
): Expr() {
    override fun eval(runtime: Runtime): Data {
        val function = runtime.symbolTable[name] as? FunctionData
            ?: throw Exception("Function $name not found")

        val evaluatedArgs = args.map { it.eval(runtime) }

        val bindings = function.params.zip(evaluatedArgs).toMap()
        val subscope = runtime.subscope(bindings)

        // Evaluate the function body in the subscope
        var lastResult: Data = None
        for (stmt in function.body) {
            lastResult = stmt.eval(subscope)
        }
        return lastResult
    }
}

class Concat(
    private val left: Expr,
    private val right: Expr
) : Expr() {
    override fun eval(runtime: Runtime): Data {
        val leftStr = left.eval(runtime) as? StringData ?: throw Exception("Left operand is not a string")
        val rightStr = right.eval(runtime) as? StringData ?: throw Exception("Right operand is not a string")
        return StringData(leftStr.v + rightStr.v)
    }
}

class Print(private val expr: Expr) : Expr() {
    override fun eval(runtime: Runtime): Data {
        val value = expr.eval(runtime)
        when (value) {
            is StringData -> println(value.v)
            else -> println(value)
        }
        return value
    }
}

class Ifelse(
    val cond: Expr,
    val trueExpr: Expr,
    val falseExpr: Expr,
) : Expr() {
    override fun eval(runtime:Runtime): Data {
        val result = cond.eval(runtime) as BooleanData
        return if(result.v) {
            trueExpr.eval(runtime)
        } else {
            falseExpr.eval(runtime)
        }
    }
}

// SHOW

class ListLiteral(val elements: List<Expr>) : Expr() {
    override fun eval(runtime: Runtime): Data {
        val evaluatedElements = elements.map { it.eval(runtime) }
        return ListData(evaluatedElements)
    }
}

class ListLength(val list: Expr) : Expr() {
    override fun eval(runtime: Runtime): Data {
        val listData = list.eval(runtime) as? ListData ?: throw Exception("Operand is not a list")
        return IntData(listData.v.size)
    }
}

class ListSum(val list: Expr) : Expr() {
    override fun eval(runtime: Runtime): Data {
        val listData = list.eval(runtime) as? ListData ?: throw Exception("Operand is not a list")
        val sum = listData.v.sumOf { (it as? IntData)?.v ?: throw Exception("List contains non-integer value") }
        return IntData(sum)
    }
}

class StringLiteral(val lexeme:String):Expr() {
    override fun eval(runtime:Runtime):Data
    = StringData(lexeme.trim('"'))
}

