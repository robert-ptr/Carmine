package carmine.compiler.passes;

import carmine.compiler.structures.*;

private val constantFolder = ConstantFolder();
private val constantPropagator = ConstantPropagator();
private val ssaConverter = SsaConverter();
private val loopUnroller = LoopUnroller();

fun optimize(statements : List<Stmt>) // traverse all the statements and search for the expressions
{
    //statements.foldConstants().propagateConstants();
}

/*
fun foldConstants()
{
    for (Stmt statement : statements)
    {
        statement.accept(constantFolder);
    }
}

void constantPropagation()
{
    for (Stmt statement : statements)
    {
        statement.accept(constantPropagator);
    }
}

void convertToSSA()
{
    for (Stmt statement : statements)
    {
        statement.accept(ssaConverter);
    }
}

void loopUnrolling()
{
    for (Stmt statement : statements)
    {
        statement.accept(loopUnroller);
    }
}

void deadCodeElimination()
{

}
*/