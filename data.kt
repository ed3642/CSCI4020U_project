package backend

abstract class Data

object None:Data() {
    override fun toString() = "None"
}

class StringData(val v:String): Data() {
    override fun toString() = "\"$v\""
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

class Statement(val expr: Expr)