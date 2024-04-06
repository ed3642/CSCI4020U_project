package backend

abstract class Data

object None:Data() {
    override fun toString() = "None"
}

class StringData(val v:String): Data() {
    override fun toString() = "\"$v\""
}

class BooleanData(val v:Boolean): Data() {
    override fun toString() = "$v"
}

class IntData(val v:Int) : Data() {
    override fun toString() = "$v"
}

class Function(
    val params: List<String>,
    val body: Expr
): Data()

class Variable(val name: String, val value: Data) : Data()

class Program(val exprs: List<Expr>) : Expr() {
    override fun eval(runtime: Runtime): Data {
        var lastResult: Data = None
        for (expr in exprs) {
            lastResult = expr.eval(runtime)
        }
        return lastResult
    }
}

class FunctionDef(
    private val name: String,
    private val params: List<String>,
    private val body: List<Expr>
) : Expr() {
    override fun eval(runtime: Runtime): Data {
        runtime.symbolTable[name] = FunctionData(params, body)
        return None
    }
}

class FunctionData(
    val params: List<String>,
    val body: List<Expr>
) : Data()

class Statement(val expr: Expr)

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