package carmine.compiler.structures;

interface CarmineCallable {
    fun arity() : Int
    fun call(arguments : List<Any>) : Any?
}
