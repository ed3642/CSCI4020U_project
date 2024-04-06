package backend

abstract class Data

object None:Data() {
    override fun toString() = "None"
}

class StringData(val v:String): Data() {
    override fun toString() = "String:\"$v\""
}

class IntData(val v:Int) : Data() {
    override fun toString() = "Int:$v"
}

class Function(
    val params: List<String>,
    val body: Expr
): Data()