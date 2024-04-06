package backend

abstract class Expr {
    abstract fun eval(runtime:Runtime):Data
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
    val op:Operator,
    val left:Expr,
    val right:Expr,
    ) : Expr() {
        override fun eval(runtime:Runtime):Data {
        val x = left.eval(runtime)
        val y = right.eval(runtime)
        if(x is IntData && y is IntData) {
            return IntData(
                when(op) {
                    Operator.ADD -> x.v + y.v
                    Operator.SUB -> x.v - y.v
                    Operator.MUL -> x.v * y.v
                    Operator.DIV -> x.v / y.v
                }
            )
        } else {
            throw Exception("only support int")
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

class StringLiteral(val lexeme:String):Expr() {
    override fun eval(runtime:Runtime):Data
    = StringData(lexeme.trim('"'))
}

class FunCall(
    val name: String,
    val args: List<Expr>
): Expr() {
    override fun eval(runtime: Runtime): Data {
        val function = runtime.symbolTable[name] as? Function
            ?: throw Exception("Function $name not found")

        val evaluatedArgs = args.map { it.eval(runtime) }

        // Create a new subscope with the function parameters bound to the evaluated arguments
        val bindings = function.params.zip(evaluatedArgs).toMap()
        val subscope = runtime.subscope(bindings)

        // Evaluate the function body in the subscope
        return function.body.eval(subscope)
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